package rate;

import utils.sg.smu.securecom.protocol.Paillier;
import utils.sg.smu.securecom.protocol.PaillierThdDec;
import utils.sg.smu.securecom.utils.Keys;

/**
 * Author:wbGuo
 * Date: 2023/7/14
 * Key Generation Center
 * distribute public key to TP. private key 1 to CP and private key 2 to CSP.
 */
public class KGC {
    //512\768\1024\1280\1536
    private static final int keyLen = 128;

    Keys key = new Keys(keyLen);

    //to  encrypt
    public Paillier getPai(){
        return key.pai;
    }
    //send to cp
    public PaillierThdDec getCp(){
        return key.cp;
    }
    //send to csp
    public PaillierThdDec getCsp(){
        return key.csp;
    }
}
