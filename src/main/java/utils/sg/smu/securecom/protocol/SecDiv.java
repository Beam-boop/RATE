package utils.sg.smu.securecom.protocol;

import org.apache.commons.math3.fraction.BigFraction;
import utils.sg.smu.securecom.utils.User;
import utils.sg.smu.securecom.utils.Utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

/**
 * Author:wbGuo
 * Date: 2023/2/22
 */
public class SecDiv {
    public static BigInteger secDiv_1(BigInteger ex, BigInteger ey, Paillier pai, PaillierThdDec cp,
                                      PaillierThdDec csp) {

        // Step-1 turn c to user, [𝑐]←[𝑑]⋅[𝑣]^𝑟
        BigInteger c = pai.add(ex, pai.multiply(ey, cp.getRandom()));
        BigInteger c1 = cp.partyDecrypt(c);
        BigInteger c2 = csp.partyDecrypt(c);
        return csp.finalDecrypt(c1, c2);

    }

    public static BigInteger secDiv_2(BigInteger ex, Paillier pai, HashMap<String, BigInteger> divRandom) {

        // Step-3 [𝑑/𝑣]←[𝑐/𝑣]⋅[−𝑟]
//        BigInteger nr = cp.getRandom().negate();
//        BigInteger enr = pai.encrypt(nr);
        return pai.sub(ex, divRandom.get("er"));
    }

    public static void main(String[] args) {

        int SIGMA = 80;//118;
        int key_len = 512;
        User user = new User(key_len);
        BigInteger precision = BigInteger.valueOf(2).shiftLeft(106);

        int x;
        int y;

        for (int i = 0; i < 100; i++) {

            x = 34;
            y = 1;
//            x = 500;
//            y = 10000;
            System.out.println(x + " " + y);
            System.out.println("明文结果：" + x * 1.0 / y);

            BigInteger ex = user.pai.encrypt(BigInteger.valueOf(x));
            BigInteger ey = user.pai.encrypt(BigInteger.valueOf(y));

            BigInteger temp = Utils.getRandom(SIGMA);
            user.cp.setRandom(temp);
            HashMap<String, BigInteger> divRandom = new HashMap<>();
            divRandom.put("er", user.pai.encrypt(temp.multiply(precision)));
            // Step-1 turn c to user, [𝑐]←[𝑑]⋅[𝑣]^𝑟
            BigInteger cm = user.pai.add(ex, user.pai.multiply(ey, user.cp.getRandom()));
            BigInteger c1 = user.cp.partyDecrypt(cm);
            BigInteger c2 = user.csp.partyDecrypt(cm);
            BigInteger c = user.csp.finalDecrypt(c1, c2);

            //Step-2 [𝑐/𝑣]
            BigDecimal cv = new BigDecimal(c).divide(new BigDecimal(y), 15, BigDecimal.ROUND_CEILING).multiply(new BigDecimal(precision));
            BigInteger ecv = user.pai.encrypt(cv.toBigInteger().mod((user.pai.getPublicKey().getN())));

            BigInteger edres = user.pai.sub(ecv, divRandom.get("er"));

            user.pai.setDecryption(user.prikey);
            System.out.println("密文解密后结果：" + new BigDecimal(user.pai.decrypt(edres)).divide(new BigDecimal(precision)).doubleValue());
            System.out.println("---");
        }
    }
}
