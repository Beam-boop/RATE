import contrastexperiment.dp.CloudPlatform;
import contrastexperiment.dp.TaskParticipants;
import contrastexperiment.dp.TaskRequester;
import contrastexperiment.ga.GeneticAlgorithm;
import rate.SecureCloudPlatform;
import rate.SecureTaskParticipants;
import rate.SecureTaskRequester;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:wbGuo
 * Date: 2023/7/14
 */
public class AvgTimeRun {
    public static void runTDrive(int[] alphaIndex, int[] betaIndex, int[] budget, int[] velIndex, int[] keyIndex, int[] numIndex) throws Exception {
        String filenameItem = "res/T-Drive/T-Drive-";
        String filenameInfo = "res/T-Drive/T-Drive-info.csv";
        double[] alphaArr = new double[]{1};
        double[] betaArr = new double[]{0.2, 0.4, 0.6, 0.8, 1.0};
        int[] velArr = new int[]{1, 2, 4, 5, 11, 22};
        int[] numArr = new int[]{10, 20, 40, 50, 70, 100};
        int[] keyArr = new int[]{128, 512, 768, 1024, 1280};
        int taskTime = 600;
        boolean containOneForm = true;
        Utils.csvHead();
        //rate
        System.out.println("-------------------TDrive--------------------");
        System.out.println("-------------------rate--------------------");
        List<int[]> listRequester = new ArrayList<>();
        List<int[]> listWorker = new ArrayList<>();
        List<int[]> listCp = new ArrayList<>();
        for (int k = keyIndex[0]; k < keyIndex[1]; k++) {
            for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
                for (int m = betaIndex[0]; m < betaIndex[1]; m++) {
                    for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
                        for (int i = numIndex[0]; i < numIndex[1]; i++) {
                            if (containOneForm) {
                                for (int v = velIndex[0]; v < velIndex[1]; v++) {
                                    int[] requesterTime = new int[10];
                                    int[] workerTime = new int[10];
                                    int[] cpTime = new int[10];
                                    for (int t = 0; t < 10; t++) {
                                        long startTime = System.currentTimeMillis();
                                        //set TP and TR
                                        SecureTaskRequester tr = new SecureTaskRequester(b, taskTime, keyArr[k], filenameInfo);
                                        long oneTime = System.currentTimeMillis();
                                        SecureTaskParticipants tp = new SecureTaskParticipants(velArr[v], filenameItem + numArr[i] + ".csv");
                                        long twoTime = System.currentTimeMillis();
                                        //set CP and CSP
                                        SecureCloudPlatform cp = new SecureCloudPlatform(alphaArr[n], betaArr[m], tr, tp);
                                        int[] message = cp.solve();
                                        long endTime = System.currentTimeMillis();

                                        System.out.println("------------------------------------------");
                                        requesterTime[t] = (int) (endTime - startTime);
                                        workerTime[t] = (int) (twoTime - oneTime) ;
                                        cpTime[t] = (int) (endTime - twoTime);
                                    }
                                    listRequester.add(requesterTime);
                                    listWorker.add(workerTime);
                                    listCp.add(cpTime);
                                }
                            }
                        }
                    }
                }
            }
        }
        Utils.writeTimeToCsv(listRequester);
        Utils.writeTimeToCsv(listWorker);
        Utils.writeTimeToCsv(listCp);
        Utils.Text2csv();

        System.out.println("-------------------GA--------------------");
        List<int[]> timeList = new ArrayList<>();

        for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
            for (int m = betaIndex[0]; m < betaIndex[1]; m++) {
                for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
                    for (int i = numIndex[0]; i < numIndex[1]; i++) {
                        if (containOneForm) {
                            int[] time = new int[10];
                            for (int v = velIndex[0]; v < velIndex[1]; v++) {
                                int totalTime = 0;
                                TaskRequester tr = new TaskRequester(b, taskTime, filenameInfo);
                                TaskParticipants tp = new TaskParticipants(velArr[v], filenameItem + numArr[i] + ".csv");
                                //set CP and CSP
                                CloudPlatform cp = new CloudPlatform(alphaArr[n], betaArr[m], tr, tp);
                                List<Integer> serveTimes = cp.calculateServiceTime();
                                int numOfParticipants = cp.numOfParticipants;
                                int numOfThing = serveTimes.size();
                                int capOfPack = b;
                                GeneticAlgorithm gaKnapsack = null;
                                int requesterBenefit = 0;
                                int workerBenefit = 0;
                                for (int t = 0; t < 10; t++) {
                                    long startTime = System.currentTimeMillis();
                                    gaKnapsack = new GeneticAlgorithm(100, capOfPack, numOfThing, 5000, 0.5f, 0.01f, serveTimes, alphaArr[n], betaArr[m]);
                                    requesterBenefit = gaKnapsack.geneticAlgorithmProcess(0);
                                    workerBenefit = gaKnapsack.sumWeight();
                                    System.out.println("TPs benefit is:" + workerBenefit);
                                    long endTime = System.currentTimeMillis();
                                    time[t] = (int) (endTime - startTime);
                                    System.out.println("plaintext running time: " + time[t] + "ms");
                                }
                                timeList.add(time);
                                System.out.println("----------------------------------------------------------------------");
                            }
                        }
                    }
                }
            }
        }
        Utils.writeTimeToCsv(timeList);
        Utils.Text2csv();
    }

    public static void main(String[] args) throws Exception {
        //beta/alpha 0.2 0.4 0.6 0.8 1.0
        //vel=22 B=500
        //rate dp ga
        //keylen=128
        int[] keyIndex = new int[]{0, 1};

        System.out.println("------------------------------------ratio beta/alpha--------------------------------------");
        int[] alphaIndex = new int[]{0, 1};
        int[] betaIndex = new int[]{0, 5};
        int[] budgetTDrive = new int[]{750, 750, 1};
        int[] velIndex = new int[]{0, 1};
        int[] numIndex = new int[]{4, 5};


        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex, numIndex);

        //beta/alpha 1.0
        //vel=1,2,4,5,11,22 B=500
        //rate dp ga
        //keylen=128
        System.out.println("------------------------------------worker number n--------------------------------------");
        alphaIndex = new int[]{0, 1};
        betaIndex = new int[]{4, 5};
        budgetTDrive = new int[]{750, 750, 1};
        int[] budgetKnap = new int[]{997, 997, 1};
        velIndex = new int[]{5, 6};
        numIndex = new int[]{0, 6};

        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex, numIndex);
//        runKanp(budgetKnap, velIndex, keyIndex);

        //beta/alpha 1.0
        //vel=22 B=100,1000,100
        //rate dp ga
        //keylen=128
        System.out.println("------------------------------------Budget B--------------------------------------");
        alphaIndex = new int[]{0, 1};
        betaIndex = new int[]{4, 5};
        budgetTDrive = new int[]{400, 2000, 200};
        velIndex = new int[]{5, 6};
        numIndex = new int[]{4, 5};

        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex, numIndex);
//        runKanp(budgetTDrive, velIndex, keyIndex);

        //beta/alpha 1.0
        //vel=22 B=500, 500, 1
        //rate dp ga
        //keylen=512, 768, 1024, 1280
        System.out.println("------------------------------------keyLen K--------------------------------------");
        alphaIndex = new int[]{0, 1};
        betaIndex = new int[]{4, 5};
        budgetTDrive = new int[]{750, 750, 1};
        velIndex = new int[]{5, 6};
        keyIndex = new int[]{1, 5};
        numIndex = new int[]{4, 5};

        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex, numIndex);
    }
}