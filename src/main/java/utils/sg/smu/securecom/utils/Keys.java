package utils.sg.smu.securecom.utils;

import utils.sg.smu.securecom.keys.KeyGen;
import utils.sg.smu.securecom.keys.PaillierKey;
import utils.sg.smu.securecom.keys.PaillierPrivateKey;
import utils.sg.smu.securecom.keys.PaillierThdPrivateKey;
import utils.sg.smu.securecom.protocol.Paillier;
import utils.sg.smu.securecom.protocol.PaillierThdDec;

import java.math.BigInteger;
import java.util.Random;

public class Keys {

	protected static final int f = 1;
	
	protected static Random rnd = new Random();
	
	// secure level 2^80 bits
	protected static int sigma = 80;
	
	protected PaillierPrivateKey prikey = null;
	protected Paillier pai = null;
	protected PaillierThdPrivateKey[] ThdKey = null;
	protected PaillierThdDec cp = null;
	protected PaillierThdDec csp = null;
	
	public Keys(int len) {

//	    private static BigInteger p = BigInteger.valueOf(5);
//	    private static BigInteger q = BigInteger.valueOf(7);
		BigInteger p = new BigInteger(len, 64, rnd);
	    BigInteger q = new BigInteger(len, 64, rnd);
		
	    BigInteger n = p.multiply(q);
	    BigInteger lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

	    prikey = new PaillierPrivateKey(n, lambda);
	    pai = new Paillier(new PaillierKey(n));
		
		ThdKey = KeyGen.genThdKey(prikey.getLambda(), prikey.getN(), prikey.getNsquare(), rnd);
		cp = new PaillierThdDec(ThdKey[0]);
		csp = new PaillierThdDec(ThdKey[1]);
	}
	
	public static void main(String[] args) {
		
		Keys key = new Keys(512);
		BigInteger m = BigInteger.valueOf(2);
		BigInteger em = key.pai.encrypt(m);
		BigInteger M1 = key.cp.partyDecrypt(em);
		BigInteger M2 = key.csp.partyDecrypt(em);
		System.out.println(key.csp.finalDecrypt(M1, M2));
	}
}
