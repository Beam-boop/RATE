package utils.sg.smu.securecom.protocol;

import org.apache.commons.math3.fraction.BigFraction;
import utils.sg.smu.securecom.utils.User;
import utils.sg.smu.securecom.utils.Utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

public class SecMul {

    private static final int SIGMA = 80;//118;

    public static BigInteger secMul(BigInteger ex, BigInteger ey, Paillier pai, PaillierThdDec cp, PaillierThdDec csp, HashMap<String, BigInteger> randomRestore) {
        // Step-1 CP
        BigInteger r1 = randomRestore.get("mulr1");
        BigInteger r2 = randomRestore.get("mulr2");
        BigInteger x = pai.add(ex, randomRestore.get("emulr1"));
        BigInteger y = pai.add(ey, randomRestore.get("emulr2"));
//        BigInteger r1 = Utils.getRandom(SIGMA);
//        BigInteger r2 = Utils.getRandom(SIGMA);
//        BigInteger x = pai.add(ex, pai.encrypt(r1));
//        BigInteger y = pai.add(ey, pai.encrypt(r2));
        BigInteger x1 = cp.partyDecrypt(x);
        BigInteger y1 = cp.partyDecrypt(y);

        // Step-2 CSP
        BigInteger x2 = csp.partyDecrypt(x);
        BigInteger maskedX = csp.finalDecrypt(x1, x2);
        BigInteger y2 = csp.partyDecrypt(y);
        BigInteger maskedY = csp.finalDecrypt(y1, y2);
        BigInteger maskedXY = maskedX.multiply(maskedY);
        BigInteger eXY = pai.encrypt(maskedXY.mod(pai.getPrivateKey().getN()));

        // Step-3 CP
        BigInteger e_r2_x = pai.multiply(ex, r2);
        BigInteger e_r1_y = pai.multiply(ey, r1);
        BigInteger e_r1_r2 = pai.encrypt(r1.multiply(r2));
        pai.getPublicKey().getN();
        return pai.add(
                eXY,
                pai.multiply(
                        pai.add(pai.add(e_r2_x, e_r1_y), e_r1_r2),
                        pai.getPublicKey().getN().subtract(BigInteger.valueOf(1))
                )
        );
    }

    public static void main(String[] args) {
        int key_len = 128;
        User user = new User(key_len);
        BigInteger precision = BigInteger.valueOf(2).pow(106);
//        BigInteger precision = BigInteger.valueOf(1000);
        double x = 40.1;
        double y = 20.2;
        double z = 10.1;
        HashMap<String, BigInteger> randomRestore = new HashMap<>();
        BigInteger r1 = Utils.getRandom(SIGMA);
        BigInteger r2 = Utils.getRandom(SIGMA);
        randomRestore.put("mulr1", r1);
        randomRestore.put("mulr2", r2);
        randomRestore.put("emulr1", user.pai.encrypt(r1));
        randomRestore.put("emulr2", user.pai.encrypt(r2));

        Paillier pal = new Paillier();
        BigInteger ex = user.pai.encrypt(new BigFraction(x).multiply(precision).getNumerator());
        BigInteger ey = user.pai.encrypt(new BigFraction(y).multiply(precision).getNumerator());
        BigInteger ez = user.pai.encrypt(new BigFraction(z).multiply(precision).getNumerator());
        System.out.println((x + y) * z);
        BigInteger exy = user.pai.add(ex, ey);
        BigInteger exyz = secMul(exy, ez, user.pai, user.cp, user.csp, randomRestore);
        user.pai.setDecryption(user.prikey);
//        System.out.println(user.pai.decrypt(exy));
//
//        //double type
//        double dx = 12.34;
//        double dy = 11.1;
//        int x = 12;
//        int z = 2;
//        BigInteger precision = BigInteger.valueOf(2).pow(106);
//        BigInteger edx = user.pai.encrypt(new BigFraction(dx).multiply(precision).getNumerator());
//        BigInteger edy = user.pai.encrypt(new BigFraction(dy).multiply(precision).getNumerator());
//        BigInteger ez = user.pai.encrypt(BigInteger.valueOf(z));
//        BigInteger ex = user.pai.encrypt(BigInteger.valueOf(x));
//        System.out.println(dx * z);
////        System.out.println(dx * dy);
//
//        BigInteger exz = secMul(edx, ez, user.pai, user.cp, user.csp);
//        BigInteger edxy = secMul(edx, edy, user.pai, user.cp, user.csp);
////        BigInteger exz = user.pai.sub(ex, ez);
////        BigInteger edxy = user.pai.sub(edx, edy);
//        user.pai.setDecryption(user.prikey);
//        BigInteger res = user.pai.decrypt(exz);
//        BigInteger resdxy = user.pai.decrypt(edxy);
//        System.out.println(new BigDecimal(res).divide(new BigDecimal(precision)).doubleValue());
////        System.out.println(res);
        System.out.println(new BigDecimal(user.pai.decrypt(exyz)).divide(new BigDecimal(precision.multiply(precision))).doubleValue());

//
    }
}
