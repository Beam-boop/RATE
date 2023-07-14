package rate;

import utils.sg.smu.securecom.protocol.*;
import utils.sg.smu.securecom.utils.Good;
import utils.sg.smu.securecom.utils.Keys;
import utils.sg.smu.securecom.utils.Pair;
import utils.sg.smu.securecom.utils.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:wbGuo, Paren
 * Date: 2023/7/14
 * Cloud platform: store the encrypted data and computing data
 * Computing Service platform: provides online computation services
 */
public class CpCsp {
    private static final int SIGMA = 128;//118;
    //receive the task position
    protected BigInteger[] eTaskLoc = null;
    //receive the taskTime
    protected BigInteger eTaskTime = null;
    //receive the TPs current position
    protected List<List<BigInteger>> eStartLocs = null;
    //receive the vel
    protected BigInteger eVel = null;
    public List<BigInteger> eServeTimes = new ArrayList<>();
    ArrayList<Good> goods;
    protected int alpha;
    protected int beta;
    protected int numOfThings;
    //receive the budget
    protected int capOfPack;
    /**
     * dynamic programming array
     */
    int[][] dp;
    //offline
    public HashMap<String, BigInteger> randomRestore = new HashMap<String, BigInteger>();
    //receive the sk1 and sk2
    protected PaillierThdDec cp = TR.getCp();
    protected PaillierThdDec csp = TR.getCsp();
    protected Paillier pai = TR.getPai();

    TR tr = new TR();
    TP tp = new TP();

    public CpCsp() throws ExecutionException, InterruptedException {
        capOfPack = tr.budget;
        eTaskTime = tr.eTaskTime;
        eTaskLoc = tr.getETaskLoc();

        eStartLocs = tp.getEncStartLocs();
        numOfThings = eStartLocs.size();
        eVel = tp.eVel;
        goods = new ArrayList<>();

        alpha = 1;
        beta = 1;
        dp = new int[numOfThings + 1][capOfPack + 1];
        secCmpRandom();
    }

    //calculate the service time
    public void calculateServiceTime() throws ExecutionException, InterruptedException {
        HashMap<String, BigInteger> divRandom = new HashMap<>();
        BigInteger temp = Utils.getRandom(SIGMA);
        divRandom.put("enr", pai.encrypt(temp.negate()));
        cp.setRandom(temp);

        ExecutorService executor = Executors.newFixedThreadPool(100);
        CompletableFuture<Pair>[] temps = new CompletableFuture[eStartLocs.size()];

        // phase one: Calculate arrival times under privacy-protection
        for (int i = 0; i < eStartLocs.size(); i++) {
            final int ij = i;
            temps[i] = CompletableFuture.supplyAsync(() -> {
                BigInteger eLong = SecAbs.secAbs(eStartLocs.get(ij).get(0), eTaskLoc[0], pai, cp, csp, randomRestore);
                BigInteger eLat = SecAbs.secAbs(eStartLocs.get(ij).get(1), eTaskLoc[1], pai, cp, csp, randomRestore);
                // turn c to worker
                BigInteger c = SecDiv.secDiv_1(pai.add(eLong, eLat), eVel, pai, cp, csp);

                //transmit [c/v] to cp
                BigInteger ecv = tp.getEcv(c);

                //calculate the [d/v]
                BigInteger dv = SecDiv.secDiv_2(ecv, pai, divRandom);

                // phase two: Calculate service Times with the space-time constrains.
                int flg = SecCmp.secCmp(dv, eTaskTime, pai, cp, csp, randomRestore);

                return new Pair(BigInteger.valueOf(flg), pai.sub(eTaskTime, dv));
            }, executor);
        }
        for (int i = 0; i < temps.length; i++) {
            Pair tmp = temps[i].get();
            if (tmp.getKey().equals(BigInteger.ONE)) {
                eServeTimes.add((BigInteger) tmp.getValue());
            }
        }
        executor.shutdown();
        //decrypt service Times
        for (BigInteger eServeTime : eServeTimes) {
            int serviceTimes = Integer.parseInt(String.valueOf(pai.decrypt(eServeTime)));
            goods.add(new Good((serviceTimes * alpha), (serviceTimes * beta)));
        }
    }

    //calculate the result via dp


    public void secCmpRandom() {
        BigInteger mid = pai.getPublicKey().getMid();
        BigInteger r1 = Utils.getRandom(SIGMA);
        BigInteger r2 = mid.subtract(Utils.getRandomwithUpper(SIGMA, r1));
        BigInteger er1addr2 = pai.encrypt(r1.add(r2));
        BigInteger er2 = pai.encrypt(r2);
        BigInteger er1 = pai.encrypt(r1);
        BigInteger mulr2 = Utils.getRandom(SIGMA);
        BigInteger mulr1 = Utils.getRandom(SIGMA);
        BigInteger emulr2 = pai.encrypt(mulr2);
        BigInteger emulr1 = pai.encrypt(mulr1);
        randomRestore.put("r1", r1);
        randomRestore.put("er1addr2", er1addr2);
        randomRestore.put("er1", er1);
        randomRestore.put("er2", er2);
        randomRestore.put("mulr1", mulr1);
        randomRestore.put("mulr2", mulr2);
        randomRestore.put("emulr1", emulr1);
        randomRestore.put("emulr2", emulr2);
    }

    /**
     * Dynamic programming solves the 0-1 knapsack problem
     * in plaint text
     *
     * @return dp Array
     */
    public int dpCalculate() {
//        for (int i = 0; i < numOfThings; i++) {
//            int w = goods.get(i).weight, v = goods.get(i).value;
//            for (int j = capOfPack; j >= w; j--) {
//                int temp = dp[j - w] + v;
//                if (temp > dp[j]) {
//                    dp[j] = temp;
//                }
//            }
//        }
        for (int i = 1; i <= numOfThings; i++) {
            int w = goods.get(i - 1).weight, v = goods.get(i - 1).value;
            for (int j = 1; j <= capOfPack; j++) {
                if (j < w) {
                    dp[i][j] = dp[i - 1][j];
                } else {
                    int temp = dp[i - 1][j - w] + v;
                    dp[i][j] = Math.max(temp, dp[i - 1][j]);
                }
            }
        }

        return dp[numOfThings][capOfPack];
    }

    public int chooseItems() {
        int[] x = new int[numOfThings];
        int j = capOfPack;
        for (int i = numOfThings; i > 0; i--) {
            int w = goods.get(i - 1).weight;
            if (j - w < 0)
                continue;
            if (dp[i][j] > dp[i - 1][j]) {
                x[i - 1] = 1;
                j -= w;
            }
        }
        int sumWeight = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] == 1)
                sumWeight += goods.get(i).weight;
        }
        System.out.println("Worker benefit is:" + sumWeight);
        return sumWeight;
    }


}
