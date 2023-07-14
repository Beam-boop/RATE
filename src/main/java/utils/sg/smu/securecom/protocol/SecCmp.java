package utils.sg.smu.securecom.protocol;

import utils.sg.smu.securecom.utils.User;
import utils.sg.smu.securecom.utils.Utils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

/**
 * when ey greater than ex, return one; others return zero;
 */
public class SecCmp {

    private static final int sigma = 80;//118;

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

    public static BigInteger secCmp(BigInteger ex, BigInteger ey, Paillier pai, PaillierThdDec cp,
                                    PaillierThdDec csp, HashMap<String, BigInteger> randomRestore, int num) {

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

        BigInteger m1 = pai.multiply(pai.sub(ey, ex), BigInteger.valueOf(2 * u - 1));
        BigInteger m2 = pai.multiply(pai.sub(ey, ex), BigInteger.valueOf(1 - 2 * u));

        //Step-3
        if (pi == 0)
            return m1;
        else
            return m2;
    }

    public static void main(String[] args) {

        int key_len = 256;
        User user = new User(key_len);
        int x = 12;
        int y = 22;
        Paillier pal = new Paillier();
        BigInteger ex = user.pai.encrypt(BigInteger.valueOf(x));
        BigInteger ey = user.pai.encrypt(BigInteger.valueOf(y));

        HashMap<String, BigInteger> randomRestore = new HashMap<>();
        BigInteger mid = user.pai.getPublicKey().getMid();
        BigInteger r1 = Utils.getRandom(sigma);
        BigInteger r2 = mid.subtract(Utils.getRandomwithUpper(sigma, r1));
        BigInteger er1addr2 = user.pai.encrypt(r1.add(r2));
        BigInteger er2 = user.pai.encrypt(r2);
        randomRestore.put("r1", r1);
        randomRestore.put("er1addr2", er1addr2);
        randomRestore.put("er2", er2);
//        System.out.println(ex);
//        System.out.println(ex2);
        System.out.println(secCmp(ex, ey, user.pai, user.cp, user.csp, randomRestore));

        // double type compare
//        double dx = 12.34;
//        double dy = 10;
//        BigInteger precision = BigInteger.valueOf(2).pow(106);
//        BigInteger edx = user.pai.encrypt(new BigFraction(dx).multiply(precision).getNumerator());
//        BigInteger edy = user.pai.encrypt(new BigFraction(dy).multiply(precision).getNumerator());
//        System.out.println(secCmp(edx, edy, user.pai, user.cp, user.csp, randomRestore));
//		for(int i = 0; i < 1000; i++) {
//
//			x = Math.abs(new Random().nextInt());
//			y = Math.abs(new Random().nextInt());
////			System.out.println(x + " " + y);
//			int flg = x >= y ? 0 : 1;
//			BigInteger ex = user.pai.encrypt(BigInteger.valueOf(x));
//			BigInteger ey = user.pai.encrypt(BigInteger.valueOf(y));
//			System.out.println(secCmp(ex, ey, user.pai, user.cp, user.csp, randomRestore) == flg);
//		}
    }
}
