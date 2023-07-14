package utils.sg.smu.securecom.utils;

import java.math.BigInteger;

/**
 * Author:wbGuo
 * Date: 2022/12/15
 */
public class Good {
    public int weight, value;
    BigInteger eWeight, eValue;
    public double r;

    /**
     * Constructs an object for plaintext computation.
     *
     * @param x weight int
     * @param y value int
     */
    public Good(int x, int y) {
        weight = x;
        value = y;
        r = (1.0 * x) / (1.0 * y);
    }

    /**
     * Constructs an object for ciphertext computation.
     *
     * @param x weight of encryption.
     * @param y value of encryption.
     */
    Good(BigInteger x, BigInteger y) {
        eWeight = x;
        eValue = y;
    }

    /**
     * Constructs an object for ciphertext computation.
     *
     * @param x     weight of encryption.
     * @param y     value of encryption.
     * @param ratio the ratio of unencrypted weight to value
     */
    Good(BigInteger x, BigInteger y, double ratio) {
        eWeight = x;
        eValue = y;
        r = ratio;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
