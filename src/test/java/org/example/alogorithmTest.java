package org.example;

import org.apache.commons.math3.fraction.BigFraction;

import javax.jws.soap.SOAPBinding;
import java.math.BigInteger;

/**
 * Author:wbGuo
 * Date: 2023/9/19
 */
public class alogorithmTest {
    public static void main(String[] args) {
        BigInteger precision = BigInteger.valueOf(2).shiftLeft(106);

        BigFraction A = new BigFraction(82.2323674);
        System.out.println(A);
        System.out.println(A.getNumerator());
        A = A.multiply(precision);
        System.out.println(A);
        System.out.println(A.getNumerator());
        BigFraction b = new BigFraction(A.getNumerator(), precision);
        System.out.println(b);
    }
}
