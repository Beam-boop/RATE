package utils.sg.smu.securecom.protocol;

import org.apache.commons.math3.fraction.BigFraction;
import utils.sg.smu.securecom.utils.User;
import utils.sg.smu.securecom.utils.Utils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

/**
 * when ey greater than ex, return one; others return zero;
 */
public class SecCmp {

    private static final int sigma = 128;//118;

    public static int secCmp(BigInteger ex, BigInteger ey, Paillier pai, PaillierThdDec cp,
                             PaillierThdDec csp, HashMap<String, BigInteger> randomRestore) {

        //Step-1
        int pi = new Random().nextInt(2);
        BigInteger mid = pai.getPublicKey().getMid();
//        BigInteger r1 = Utils.getRandom(sigma);
//        BigInteger r2 = mid.subtract(Utils.getRandomwithUpper(sigma, r1));
        BigInteger r1 = randomRestore.get("r1");

        BigInteger D;
        if (pi == 0) {

            BigInteger er1addr2 = randomRestore.get("er1addr2");
            D = pai.add(pai.add(pai.multiply(ex, r1), pai.multiply(ey, r1.negate())),
                    er1addr2);
        } else {

            BigInteger er2 = randomRestore.get("er2");
            D = pai.add(pai.add(
                            pai.multiply(ey, r1), pai.multiply(ex, r1.negate())),
                    er2);
        }
        BigInteger D1 = cp.partyDecrypt(D);

        //Step-2
        BigInteger D2 = csp.partyDecrypt(D);
        BigInteger d = csp.finalDecrypt(D1, D2);
        int u = d.compareTo(mid) > 0 ? 0 : 1;

        //Step-3
        return pi ^ u;
    }

    public static void main(String[] args) {

        int key_len = 128;
        User user = new User(key_len);
        BigInteger precision = BigInteger.valueOf(2).pow(106);
        double x = 22.1;
        double y = 22.1;
        BigInteger ex = user.pai.encrypt(new BigFraction(x).multiply(precision).getNumerator());
        BigInteger ey = user.pai.encrypt(new BigFraction(y).multiply(precision).getNumerator());

        HashMap<String, BigInteger> randomRestore = new HashMap<>();
        BigInteger mid = user.pai.getPublicKey().getMid();
        BigInteger r1 = Utils.getRandom(sigma);
        BigInteger r2 = mid.subtract(Utils.getRandomwithUpper(sigma, r1));
        BigInteger er1addr2 = user.pai.encrypt(r1.add(r2));
        BigInteger er2 = user.pai.encrypt(r2);
        randomRestore.put("r1", r1);
        randomRestore.put("er1addr2", er1addr2);
        randomRestore.put("er2", er2);
        System.out.println(secCmp(ex, ey, user.pai, user.cp, user.csp, randomRestore));

    }
}
