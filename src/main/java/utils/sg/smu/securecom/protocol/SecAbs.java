package utils.sg.smu.securecom.protocol;

import utils.sg.smu.securecom.utils.Utils;
import utils.sg.smu.securecom.utils.User;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

/**
 * Author:wbGuo
 * Date: 2023/2/25
 * Secure Absolute Value Protocol
 */
public class SecAbs {
    public static BigInteger secAbs(BigInteger ex, BigInteger ey, Paillier pai, PaillierThdDec cp, PaillierThdDec csp, HashMap<String, BigInteger> randomRestore) {
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

//        BigInteger m1 = pai.sub(ex, ey);
//        BigInteger m2 = pai.multiply(m1, -1);
//        if (u == 1) {
//            BigInteger t = m1;
//            m1 = m2;
//            m2 = t;
//        }


        //Step-3
        if (pi == 0)
            return m1;
        else
            return m2;
    }

    public static void main(String[] args) {
        int sigma = 128;
        User user = new User(128);
        HashMap<String, BigInteger> randomRestore = new HashMap<String, BigInteger>();
        BigInteger mid = user.pai.getPublicKey().getMid();
        BigInteger r1 = Utils.getRandom(sigma);
        BigInteger r2 = mid.subtract(Utils.getRandomwithUpper(sigma, r1));
        BigInteger er1addr2 = user.pai.encrypt(r1.add(r2));
        BigInteger er2 = user.pai.encrypt(r2);
        randomRestore.put("r1", r1);
        randomRestore.put("er1addr2", er1addr2);
        randomRestore.put("er2", er2);
        user.pai.setDecryption(user.prikey);

        int totalTime = 0;
        for (int i = 0; i < 100; i++) {
            int x = Math.abs(new Random().nextInt(10000));
            int y = Math.abs(new Random().nextInt(10000));
            BigInteger a = user.pai.encrypt(BigInteger.valueOf(x));
            BigInteger b = user.pai.encrypt(BigInteger.valueOf(y));
            long s = System.currentTimeMillis();
            BigInteger result = secAbs(a, b, user.pai, user.cp, user.csp, randomRestore);
            long e = System.currentTimeMillis();
            System.out.println(String.valueOf(Math.abs(x - y)) + "  " + user.pai.decrypt(result));
            totalTime += (int) (e - s);
        }
        System.out.println(totalTime / 100 + "ms");

    }
}
