package rate;

import utils.sg.smu.securecom.protocol.Paillier;
import utils.sg.smu.securecom.utils.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:wbGuo
 * Date: 2023/7/14
 * task participants: send the current locations and vel to cp
 */
public class TP {
    /**
     * the paillier to encrypt
     */
    public Paillier pai = null;
    /**
     * the location of enter system
     */
    protected List<List<Double>> startLocs = new ArrayList<>();

    /**
     * the encrypted location of enter system
     */
    protected List<List<BigInteger>> eStartLocs = new ArrayList<>();
    private int vel;
    private static final int NUMBER = 111000;
    String pathItem = null;
    public BigInteger eVel;

    public TP() {
        vel = 11;
        pathItem = "res/T-Drive/T-Drive-4.csv";
    }

    /**
     * encrypt task location
     * turn double to integer, multiply 10^5
     * because 10^5 is about equal to 111*1000
     * one longitude = 111km = 111000m
     */
    private void encryptInformation() throws ExecutionException, InterruptedException {
        //KGC send the paillier to TR
        pai = TR.getPai();

        eVel = pai.encrypt(BigInteger.valueOf(vel));

        ExecutorService executor = Executors.newFixedThreadPool(100);
        CompletableFuture<Pair>[] temps = new CompletableFuture[startLocs.size()];
        for (int i = 0; i < startLocs.size(); i++) {
            final int ij = i;
            temps[i] = CompletableFuture.supplyAsync(() -> {
                int temp1 = (int) Math.round(startLocs.get(ij).get(0) * NUMBER);
                int temp2 = (int) Math.round(startLocs.get(ij).get(1) * NUMBER);
                return new Pair(pai.encrypt(BigInteger.valueOf(temp1)), pai.encrypt(BigInteger.valueOf(temp2)));
            }, executor);
        }
        for (int i = 0; i < temps.length; i++) {
            Pair tmp = temps[i].get();
            List<BigInteger> eStartLoc = new ArrayList<>();
            eStartLoc.add((BigInteger) tmp.getKey());
            eStartLoc.add((BigInteger) tmp.getValue());
            eStartLocs.add(eStartLoc);
        }
    }

    private void readData() {
        try {
            File file = new File(pathItem);
            BufferedReader readCsv = new BufferedReader(new FileReader(file));
            readCsv.readLine();
            String temp;
            while ((temp = readCsv.readLine()) != null) {
                List<Double> startLoc = new ArrayList<>();
                String[] line = temp.split(",");
                String time = line[4];
                startLoc.add(Double.valueOf(line[2].split("'")[1]));
                startLoc.add(Double.valueOf(line[3].split("'")[1]));
                startLocs.add(startLoc);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BigInteger getEcv(BigInteger c){
        return pai.encrypt(c.divide(BigInteger.valueOf(vel)));
    }

    public List<List<BigInteger>> getEncStartLocs() throws ExecutionException, InterruptedException {
        readData();
        encryptInformation();
        return eStartLocs;
    }
}
