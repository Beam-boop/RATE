package contrastexperiment.dp;

import utils.sg.smu.securecom.utils.Good;
import utils.sg.smu.securecom.utils.Solution;

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
    protected List<Integer> costs;
    protected List<Integer> payments;
    //receive the task position
    protected List<Integer> taskLoc = null;
    protected List<Integer> serviceTimes = new ArrayList<>();
    //receive the taskTime
    protected int taskTime = 0;
    //receive the TPs current position
    protected List<List<Integer>> startLocs = null;
    //receive the vel
    protected List<Integer> vel;
    ArrayList<Good> goods;
    protected double alpha;
    protected int numOfThings;
    public int numOfParticipants;
    //receive the budget
    protected int capOfPack;
    protected int requesterRevenue;
    protected double participantsWelfare;
    protected double avgCost;
    /**
     * dynamic programming array
     */
    int[][] dp;

    Solution[][] dp2;

    TaskRequester tr = null;
    TaskParticipants tp = null;

    public CloudPlatform(double a, TaskRequester r, TaskParticipants p) {
        tr = r;
        tp = p;

        capOfPack = tr.budget;
        taskTime = tr.taskTime;
        taskLoc = tr.taskLoc;

        startLocs = tp.startLocs;
        numOfParticipants = startLocs.size();
        vel = tp.vel;
        goods = new ArrayList<>();
        costs = p.costs;
        payments = p.payments;

        alpha = a;
        System.out.println("alpha is: " + a);
    }

    //calculate the service time
    public List<Integer> calculateServiceTime() {
        for (int i = 0; i < startLocs.size(); i++) {
            int arrivalTime = (Math.abs(startLocs.get(i).get(0) - taskLoc.get(0)) + Math.abs(startLocs.get(i).get(1) - taskLoc.get(1))) / vel.get(i);
            int serviceTime = taskTime - arrivalTime;
            if (serviceTime > 0) {
                serviceTimes.add(serviceTime);
            }

        }

        for (int i = 0; i < serviceTimes.size(); i++) {
            goods.add(new Good(payments.get(i), (int) (serviceTimes.get(i) * alpha)));
        }

        numOfThings = goods.size();
        System.out.println("number of TPs " + numOfParticipants);
        System.out.println("budget of task " + capOfPack);
//        dp = new int[numOfThings + 1][capOfPack + 1];
        dp2 = new Solution[numOfThings + 1][capOfPack + 1];
        return serviceTimes;
    }

    //calculate the result via dp


    /**
     * Dynamic programming solves the 0-1 knapsack problem(dp)
     * in plaint text
     *
     * @return dp Array
     */
    public void dpCalculate() {
        for (int i = 0; i <= numOfThings; i++) {
            for (int j = 0; j <= capOfPack; j++) {
                if (i == 0 || j == 0) {
                    dp2[i][j] = new Solution(0, 0);
                } else if (goods.get(i - 1).weight <= j) {
                    int valNew = dp2[i - 1][j - goods.get(i - 1).weight].totalValue + goods.get(i - 1).value;
                    int countNew = dp2[i - 1][j - goods.get(i - 1).weight].itemCount + 1;
//                    List<Good> goodsNew = new ArrayList<>(dp[i-1][j-goods.get(i-1).weight].goods);
//                    goodsNew.add(goods.get(i-1));
                    if (dp2[i - 1][j].totalValue < valNew || (dp2[i - 1][j].totalValue == valNew && dp2[i - 1][j].itemCount > countNew)) {
                        dp2[i][j] = new Solution(valNew, countNew);
                    } else {
                        dp2[i][j] = dp2[i - 1][j];
                    }
                } else {
                    dp2[i][j] = dp2[i - 1][j];
                }
            }
        }
        requesterRevenue = dp2[numOfThings][capOfPack].totalValue - capOfPack;
        participantsWelfare = capOfPack - dp2[numOfThings][capOfPack].itemCount * avgCost;
        System.out.println("requesterRevenue is: " + requesterRevenue);
        System.out.println("participantsWelfare is: " + participantsWelfare);
        System.out.println("chosen TPs are: " + dp2[numOfThings][capOfPack].itemCount);
        System.out.println("avgCost is: " + avgCost);
    }

    public void BBOM() {
        double totalResult = 0;
        int totalWeight = 0;
        List<Integer> selectedWorkers = new ArrayList<>();
        while (true) {
            double maxResult = 0;
            int maxIndex = 0;
            for (int i = 0; i < numOfThings; i++) {
                if (selectedWorkers.contains(i)) {
                    continue;
                }
                double temp = goods.get(i).value - avgCost;
                if (temp > maxResult && totalWeight + goods.get(i).weight <= capOfPack) {
                    maxResult = temp;
                    maxIndex = i;
                }
            }
            if (maxResult > 0) {
                selectedWorkers.add(maxIndex);
                totalResult += maxResult;
                totalWeight += goods.get(maxIndex).weight;
            } else {
                break;
            }
        }
        participantsWelfare = capOfPack - selectedWorkers.size() * avgCost;
        requesterRevenue = (int) Math.round(totalResult + selectedWorkers.size() * avgCost) - capOfPack;
        System.out.println("requesterRevenue is: " + requesterRevenue);
        System.out.println("participantsWelfare is: " + participantsWelfare);
        System.out.println("chosen TPs are: " + selectedWorkers.size());
        System.out.println("avgCost is: " + avgCost);
    }

    private void calculateAvgCost() {
        int sum = 0;
        for (int i = 0; i < costs.size(); i++) {
            sum += costs.get(i);
        }
        avgCost = (double) sum / (double) costs.size();
    }

    //return numOfThings, capOfPack, requesterBenefit, workerBenefit, time, time - decryptTime[1], time - decryptTime[0], keyLen
    public Number[] solve() throws ExecutionException, InterruptedException {
        calculateServiceTime();
        calculateAvgCost();
        BBOM();
        return new Number[]{numOfParticipants, capOfPack, participantsWelfare, requesterRevenue};
    }
}
