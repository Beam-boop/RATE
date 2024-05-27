package rate;

import utils.sg.smu.securecom.protocol.*;
import utils.sg.smu.securecom.utils.Good;
import utils.sg.smu.securecom.utils.Pair;
import utils.sg.smu.securecom.utils.Solution;
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
public class SecureCloudPlatform {
    private static final int SIGMA = 128;//118;
    //receive the task position
    protected BigInteger[] eTaskLoc = null;
    //receive the taskTime
    protected BigInteger eTaskTime = null;
    //receive the TPs current position
    protected List<List<BigInteger>> eStartLocs = null;
    //receive the vel
    protected List<BigInteger> eVel;
    public List<BigInteger> eServeTimes = new ArrayList<>();
    protected List<BigInteger> eCost = new ArrayList<>();
    protected List<Integer> payments = new ArrayList<>();
    ArrayList<Good> goods;
    protected double alpha;
    protected double avgCost;
    protected int numOfThings;

    int numberOfParticipants;
    //receive the budget
    protected int capOfPack;
    /**
     * dynamic programming array
     */
    Solution[][] dp;
    //offline
    public HashMap<String, BigInteger> randomRestore = new HashMap<String, BigInteger>();
    //receive the sk1 and sk2
    protected PaillierThdDec cp = SecureTaskRequester.getCp();
    protected PaillierThdDec csp = SecureTaskRequester.getCsp();
    protected Paillier pai = SecureTaskRequester.getPai();
    SecureTaskRequester tr = null;
    SecureTaskParticipants tp = null;
    double participantsWelfare;
    int requesterRevenue;

    public SecureCloudPlatform(double a, SecureTaskRequester r, SecureTaskParticipants p) throws ExecutionException, InterruptedException {
        tr = r;
        tp = p;

        capOfPack = tr.budget;
        eTaskTime = tr.eTaskTime;
        eTaskLoc = tr.eTaskLoc;

        eStartLocs = tp.eStartLocs;
        eCost = tp.eCost;

        numberOfParticipants = eStartLocs.size();
        eVel = tp.eVel;
        goods = new ArrayList<>();

        alpha = a;
        System.out.println("alpha is: " + a);
        secCmpRandom();
    }

    //calculate the service time
    public void calculateServiceTime() throws ExecutionException, InterruptedException {
        HashMap<String, BigInteger> divRandom = new HashMap<>();
        BigInteger temp = Utils.getRandom(SIGMA);
        divRandom.put("er", pai.encrypt(temp));
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
                BigInteger c = SecDiv.secDiv_1(pai.add(eLong, eLat), eVel.get(ij), pai, cp, csp);

                //transmit [c/v] to cp
                BigInteger ecv = tp.getEcv(c, ij);

                //calculate the [d/v]
                BigInteger dv = SecDiv.secDiv_2(ecv, pai, divRandom);

                // phase two: Calculate service Times with the space-time constrains.
                int flg = SecCmp.secCmp(dv, eTaskTime, pai, cp, csp, randomRestore);

                return new Pair(BigInteger.valueOf(flg), pai.sub(eTaskTime, dv), tp.payments.get(ij));
            }, executor);
//            BigInteger eLong = SecAbs.secAbs(eStartLocs.get(i).get(0), eTaskLoc[0], pai, cp, csp, randomRestore);
//            System.out.println("test:"+Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(eStartLocs.get(i).get(0)), csp.partyDecrypt(eStartLocs.get(i).get(0))))));
//            System.out.println("test:"+Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(eTaskLoc[0]), csp.partyDecrypt(eTaskLoc[0])))));
//            System.out.println("test:"+Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(eLong), csp.partyDecrypt(eLong)))));
//
//            BigInteger eLat = SecAbs.secAbs(eStartLocs.get(i).get(1), eTaskLoc[1], pai, cp, csp, randomRestore);
//            System.out.println("test:"+Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(eStartLocs.get(i).get(1)), csp.partyDecrypt(eStartLocs.get(i).get(1))))));
//            System.out.println("test:"+Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(eTaskLoc[1]), csp.partyDecrypt(eTaskLoc[1])))));
//            System.out.println("test:"+Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(eLat), csp.partyDecrypt(eLat)))));
//
//            BigInteger a = pai.add(eLong, eLat);
//            System.out.println("test:"+Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(a), csp.partyDecrypt(a)))));
//            // turn c to worker
//            BigInteger c = SecDiv.secDiv_1(pai.add(eLong, eLat), eVel.get(i), pai, cp, csp);
//
//            //transmit [c/v] to cp
//            BigInteger ecv = tp.getEcv(c, i);
//
//            //calculate the [d/v]
//            BigInteger dv = SecDiv.secDiv_2(ecv, pai, divRandom);
//
//            // phase two: Calculate service Times with the space-time constrains.
//            int flg = SecCmp.secCmp(dv, eTaskTime, pai, cp, csp, randomRestore);
//
//            //test
//            if (flg == 1) {
//                eServeTimes.add(pai.sub(eTaskTime, dv));
//                payments.add(tp.payments.get(i));
//            }
        }
        executor.shutdown();

        for (int i = 0; i < temps.length; i++) {
            Pair tmp = temps[i].get();
            if (tmp.getKey().equals(BigInteger.ONE)) {
                //将选中的TP的支付信息加入到支付列表中
                payments.add((int) tmp.getValue2());
                eServeTimes.add((BigInteger) tmp.getValue1());
            }
        }
        //decrypt service Times
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < eServeTimes.size(); i++) {
            int serviceTimes = Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(eServeTimes.get(i)), csp.partyDecrypt(eServeTimes.get(i)))));
            goods.add(new Good((payments.get(i)), ((int) (serviceTimes * alpha))));
            values.add((int) (serviceTimes * alpha));
        }
        System.out.println("weights: " + payments);
        System.out.println("values: " + values);

        numOfThings = goods.size();
        System.out.println("number of TPs " + numberOfParticipants);
        System.out.println("budget of task " + capOfPack);
        dp = new Solution[numOfThings + 1][capOfPack + 1];
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
    public void resultCalculate() {
        for (int i = 0; i <= numOfThings; i++) {
            for (int j = 0; j <= capOfPack; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = new Solution(0, 0);
                } else if (goods.get(i - 1).weight <= j) {
                    int valNew = dp[i - 1][j - goods.get(i - 1).weight].totalValue + goods.get(i - 1).value;
                    int countNew = dp[i - 1][j - goods.get(i - 1).weight].itemCount + 1;
//                    List<Good> goodsNew = new ArrayList<>(dp[i-1][j-goods.get(i-1).weight].goods);
//                    goodsNew.add(goods.get(i-1));
                    if (dp[i - 1][j].totalValue < valNew || (dp[i - 1][j].totalValue == valNew && dp[i - 1][j].itemCount > countNew)) {
                        dp[i][j] = new Solution(valNew, countNew);
                    } else {
                        dp[i][j] = dp[i - 1][j];
                    }
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        requesterRevenue = dp[numOfThings][capOfPack].totalValue - capOfPack;
        participantsWelfare = capOfPack - dp[numOfThings][capOfPack].itemCount * avgCost;
        System.out.println("requesterRevenue is: " + requesterRevenue);
        System.out.println("participantsWelfare is: " + participantsWelfare);
        System.out.println("chosen TPs are: " + dp[numOfThings][capOfPack].itemCount);
        System.out.println("avgCost is: " + avgCost);
    }

    //return numOfThings, capOfPack, requesterBenefit, workerBenefit, time, time - decryptTime[1], time - decryptTime[0], keyLen
    public Number[] solve() throws ExecutionException, InterruptedException {
        calculateServiceTime();
        calculateAvgCost();
        resultCalculate();
        return new Number[]{numberOfParticipants, capOfPack, participantsWelfare, requesterRevenue};
    }

    private void calculateAvgCost() {
        BigInteger eSum = eCost.get(0);
        for (int i = 1; i < eCost.size(); i++) {
            eSum = pai.add(eSum, eCost.get(i));
        }
        int sum = Integer.parseInt(String.valueOf(cp.finalDecrypt(cp.partyDecrypt(eSum), csp.partyDecrypt(eSum))));
        avgCost = (double) sum / (double) eCost.size();
    }
}
