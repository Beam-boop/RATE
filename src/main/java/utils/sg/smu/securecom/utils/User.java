package utils.sg.smu.securecom.utils;

import utils.sg.smu.securecom.keys.PaillierPrivateKey;
import utils.sg.smu.securecom.keys.PaillierThdPrivateKey;
import utils.sg.smu.securecom.protocol.Paillier;
import utils.sg.smu.securecom.protocol.PaillierThdDec;

public class User {

	public static PaillierPrivateKey prikey = null;
	public static Paillier pai = null;
	private static PaillierThdPrivateKey[] ThdKey = null;
	public static PaillierThdDec cp = null;
	public static PaillierThdDec csp = null;
	
	public User(int key_len) {

		Keys key = new Keys(key_len);
		prikey = key.prikey;
		pai = key.pai;
		ThdKey = key.ThdKey;
		cp = key.cp;
		csp = key.csp;
	}
}
