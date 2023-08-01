package contrastexperiment.dp;

import utils.sg.smu.securecom.utils.Good;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Author:wbGuo, Paren
 * Date: 2023/7/14
 * Cloud platform: store the encrypted data and computing data
 * Computing Service platform: provides online computation services
 */
public class CloudPlatform {
    //receive the task position
    protected List<Integer> taskLoc = null;
    protected List<Integer> serviceTimes = new ArrayList<>();
    //receive the taskTime
    protected int taskTime = 0;
    //receive the TPs current position
    protected List<List<Integer>> startLocs = null;
    //receive the vel
    protected int vel = 0;
    ArrayList<Good> goods;
    protected int alpha;
    protected int beta;
    protected int numOfThings;
    public int numOfParticipants;
    //receive the budget
    protected int capOfPack;
    /**
     * dynamic programming array
     */
    int[][] dp;

    TaskRequester tr = null;
    TaskParticipants tp = null;

    public CloudPlatform(int a, int b, TaskRequester r, TaskParticipants p) {
        tr = r;
        tp = p;

        capOfPack = tr.budget;
        taskTime = tr.taskTime;
        taskLoc = tr.taskLoc;

        startLocs = tp.startLocs;
        numOfParticipants = startLocs.size();
        vel = tp.vel;
        goods = new ArrayList<>();

        alpha = a;
        beta = b;
        System.out.println("alpha is: " + a);
        System.out.println("beta is: " + b);
    }

    //calculate the service time
    public List<Integer> calculateServiceTime() {
        for (List<Integer> startLoc : startLocs) {
            int arrivalTime = (Math.abs(startLoc.get(0) - taskLoc.get(0)) + Math.abs(startLoc.get(1) - taskLoc.get(1))) / vel;
            int serviceTime = taskTime - arrivalTime;
            if (serviceTime > 0) {
                serviceTimes.add(serviceTime);
            }

        }

        for (int serviceTime : serviceTimes) {
            goods.add(new Good((serviceTime * alpha), (serviceTime * beta)));
        }

        numOfThings = goods.size();
        System.out.println("number of TPs " + numOfParticipants);
        System.out.println("budget of task " + capOfPack);
        dp = new int[numOfThings + 1][capOfPack + 1];
        return serviceTimes;
    }

    //calculate the result via dp


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
        System.out.println("TR benefit is :" + dp[numOfThings][capOfPack]);
        return dp[numOfThings][capOfPack];
    }

    public int chooseItems() {
        int[] selectTps = new int[numOfThings];
        int j = capOfPack;
        for (int i = numOfThings; i > 0; i--) {
            int w = goods.get(i - 1).weight;
            if (j - w < 0)
                continue;
            if (dp[i][j] > dp[i - 1][j]) {
                selectTps[i - 1] = 1;
                j -= w;
            }
        }
        int tpBenefit = 0;
        for (int i = 0; i < selectTps.length; i++) {
            if (selectTps[i] == 1)
                tpBenefit += goods.get(i).weight;
        }
        System.out.println("TPs benefit is:" + tpBenefit);
        return tpBenefit;
    }

    //return numOfThings, capOfPack, requesterBenefit, workerBenefit, time, time - decryptTime[1], time - decryptTime[0], keyLen
    public int[] solve() throws ExecutionException, InterruptedException {
        calculateServiceTime();
        return new int[]{numOfParticipants, capOfPack, dpCalculate(), chooseItems()};
    }
}