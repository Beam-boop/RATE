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
        return pai.add(ex, divRandom.get("enr"));
    }

    public static void main(String[] args) {

        int SIGMA = 128;//118;
        int key_len = 128;
        User user = new User(key_len);
        BigInteger precision = BigInteger.valueOf(2).pow(106);

        double x = 12345.124;
        int ix = 18608;
        int y = 16;

        for(int i =0; i<1000;i++){

            x = Math.abs(new Random().nextDouble())*100;
            y = Math.abs(new Random().nextInt(100))+1;
            ix = Math.abs(new Random().nextInt(10000))+1;
            System.out.println(ix + " " + y);
            System.out.println(x / y);
            System.out.println(ix / y);

            //double type approximates the integer type
            BigInteger ex = user.pai.encrypt(new BigFraction(x).multiply(precision).getNumerator());
            BigInteger eix = user.pai.encrypt(BigInteger.valueOf(ix));
            BigInteger ey = user.pai.encrypt(BigInteger.valueOf(y));

            BigInteger temp = Utils.getRandom(SIGMA);
            user.cp.setRandom(temp);
            HashMap<String, BigInteger> divRandom = new HashMap<>();
            divRandom.put("enr", user.pai.encrypt(temp.negate()));

            BigInteger c = secDiv_1(ex, ey, user.pai, user.cp, user.csp);
            BigInteger ic = secDiv_1(eix, ey, user.pai, user.cp, user.csp);

            //Step-2 [𝑐/𝑣]
            BigInteger cv = c.divide(BigInteger.valueOf(y));
            BigInteger icv = ic.divide(BigInteger.valueOf(y));

            BigInteger eres = secDiv_2(user.pai.encrypt(icv), user.pai, divRandom);
            BigInteger edres = secDiv_2(user.pai.encrypt(cv), user.pai, divRandom);

            user.pai.setDecryption(user.prikey);
            BigInteger res = user.pai.decrypt(eres);
            BigInteger dres = user.pai.decrypt(edres);
            System.out.println(new BigDecimal(dres).divide(new BigDecimal(precision)).doubleValue());
            System.out.println(res);
            System.out.println("---");
        }
    }
}
