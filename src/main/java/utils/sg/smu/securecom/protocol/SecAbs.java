package utils.sg.smu.securecom.protocol;

import utils.sg.smu.securecom.utils.Utils;
import utils.sg.smu.securecom.utils.User;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Author:wbGuo
 * Date: 2023/2/25
 */
public class SecAbs {
    public static BigInteger secAbs(BigInteger ex, BigInteger ey, Paillier pai, PaillierThdDec cp, PaillierThdDec csp, HashMap<String, BigInteger> randomRestore) {
//        BigInteger temp = pai.sub(pai.multiply(SecCmp.secCmp(ex, ey, pai, cp, csp, randomRestore, 1), BigInteger.valueOf(2)), pai.encrypt(BigInteger.ONE));
//        return SecMul.secMul(pai.sub(ey, ex), temp, pai, cp, csp, randomRestore);
        return SecCmp.secCmp(ex, ey, pai, cp, csp, randomRestore, 1);
    }

    public static void main(String[] args) {
        int x = 1234;
        int y = 1024;
        int sigma = 80;
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
        System.out.println(user.pai.decrypt(secAbs(user.pai.encrypt(BigInteger.valueOf(x)), user.pai.encrypt(BigInteger.valueOf(y)), user.pai, user.cp, user.csp, randomRestore)));
//        System.out.println(user.pai.decrypt(user.pai.multiply(user.pai.encrypt(BigInteger.valueOf(y)), user.pai.encrypt(BigInteger.ONE))));
    }
}
