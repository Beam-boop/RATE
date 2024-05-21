import contrastexperiment.dp.CloudPlatform;
import contrastexperiment.dp.TaskParticipants;
import contrastexperiment.dp.TaskRequester;
import rate.SecureCloudPlatform;
import rate.SecureTaskParticipants;
import rate.SecureTaskRequester;
import utils.Utils;

/**
 * Author:wbGuo
 * Date: 2023/7/14
 */
public class LocalRun {
    public static void runTDrive(int[] alphaIndex, int[] budget, int[] keyIndex, int[] numIndex) throws Exception {
        String filenameItem = "res/T-Drive/T-Drive-";
        String filenameInfo = "res/T-Drive/T-Drive-info.csv";
        double[] alphaArr = new double[]{1, 2, 3, 4, 5};
        int[] numArr = new int[]{10, 20, 40, 50, 70, 100};
        int[] keyArr = new int[]{128, 512, 768, 1024, 1280};
        int taskTime = 600;
        Utils.csvHead();
        //rate
        System.out.println("-------------------TDrive--------------------");
        System.out.println("-------------------rate--------------------");
        for (int k = keyIndex[0]; k < keyIndex[1]; k++) {
            for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
                for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
                    for (int i = numIndex[0]; i < numIndex[1]; i++) {
                        long startTime = System.currentTimeMillis();
                        //set TP and TR
                        SecureTaskRequester tr = new SecureTaskRequester(b, taskTime, keyArr[k], filenameInfo);
                        long oneTime = System.currentTimeMillis();
                        SecureTaskParticipants tp = new SecureTaskParticipants(filenameItem + numArr[i] + "-new.csv", true);
                        long twoTime = System.currentTimeMillis();
                        //set CP and CSP
                        SecureCloudPlatform cp = new SecureCloudPlatform(alphaArr[n], tr, tp);
                        //numberOfParticipants, capOfPack, participantsWelfare, requesterRevenue
                        Number[] message = cp.solve();
                        long endTime = System.currentTimeMillis();
                        System.out.println("------------------------------------------");
                        Utils.writeResultToCsv("TDrive", "rate", alphaArr[n], (int) message[0],
                                (int) message[1], (double) message[2], (int) message[3], (int) (endTime - startTime), (int) (twoTime - oneTime), (int) (endTime - twoTime), keyArr[k]);

                    }
                }
            }
        }
        Utils.Text2csv();
        System.out.println("---------------------------dp-------------------------");
        for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
            for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
                for (int i = numIndex[0]; i < numIndex[1]; i++) {
                    long startTime = System.currentTimeMillis();
                    //set TP and TR
                    TaskRequester tr = new TaskRequester(b, taskTime, filenameInfo);
                    long oneTime = System.currentTimeMillis();
                    TaskParticipants tp = new TaskParticipants(filenameItem + numArr[i] + "-new.csv");
                    long twoTime = System.currentTimeMillis();
                    //set CP and CSP
                    CloudPlatform cp = new CloudPlatform(alphaArr[n], tr, tp);
                    Number[] message = cp.solveDp();
                    long endTime = System.currentTimeMillis();
                    System.out.println("------------------------------------------");
                    Utils.writeResultToCsv("TDrive", "DP", alphaArr[n], (int) message[0],
                            (int) message[1], (double) message[2], (int) message[3], (int) (endTime - startTime), (int) (twoTime - oneTime), (int) (endTime - twoTime), 0);

                }
            }
        }
        Utils.Text2csv();
        System.out.println("---------------------------BBOM-------------------------");
        for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
            for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
                for (int i = numIndex[0]; i < numIndex[1]; i++) {
                    long startTime = System.currentTimeMillis();
                    //set TP and TR
                    TaskRequester tr = new TaskRequester(b, taskTime, filenameInfo);
                    long oneTime = System.currentTimeMillis();
                    TaskParticipants tp = new TaskParticipants(filenameItem + numArr[i] + "-new.csv");
                    long twoTime = System.currentTimeMillis();
                    //set CP and CSP
                    CloudPlatform cp = new CloudPlatform(alphaArr[n], tr, tp);
                    Number[] message = cp.solveBBOM();
                    long endTime = System.currentTimeMillis();
                    System.out.println("------------------------------------------");
                    Utils.writeResultToCsv("TDrive", "BBOM", alphaArr[n], (int) message[0],
                            (int) message[1], (double) message[2], (int) message[3], (int) (endTime - startTime), (int) (twoTime - oneTime), (int) (endTime - twoTime), 0);

                }
            }
        }
        Utils.Text2csv();

        System.out.println("---------------------------NSGAII-------------------------");
        for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
            for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
                for (int i = numIndex[0]; i < numIndex[1]; i++) {
                    long startTime = System.currentTimeMillis();
                    //set TP and TR
                    TaskRequester tr = new TaskRequester(b, taskTime, filenameInfo);
                    long oneTime = System.currentTimeMillis();
                    TaskParticipants tp = new TaskParticipants(filenameItem + numArr[i] + "-new.csv");
                    long twoTime = System.currentTimeMillis();
                    //set CP and CSP
                    CloudPlatform cp = new CloudPlatform(alphaArr[n], tr, tp);
                    Number[] message = cp.solveNsga2();
                    long endTime = System.currentTimeMillis();
                    System.out.println("------------------------------------------");
                    Utils.writeResultToCsv("TDrive", "NSGAII", alphaArr[n], (int) message[0],
                            (int) message[1], (double) message[2], (int) message[3], (int) (endTime - startTime), (int) (twoTime - oneTime), (int) (endTime - twoTime), 0);

                }
            }
        }
        Utils.Text2csv();
//
//        System.out.println("-------------------ga--------------------");
//        for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
//            for (int m = betaIndex[0]; m < betaIndex[1]; m++) {
//                for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
//                    for (int i = numIndex[0]; i < numIndex[1]; i++) {
//                        int totalTime = 0;
//                        TaskRequester tr = new TaskRequester(b, taskTime, filenameInfo);
//                        TaskParticipants tp = new TaskParticipants(velArr, filenameItem + numArr[i] + ".csv", false);
//                        //set CP and CSP
//                        CloudPlatform cp = new CloudPlatform(alphaArr[n], betaArr[m], tr, tp);
//                        List<Integer> serveTimes = cp.calculateServiceTime();
//                        int numOfParticipants = cp.numOfParticipants;
//                        int numOfThing = serveTimes.size();
//                        int capOfPack = b;
//                        GeneticAlgorithm gaKnapsack = null;
//                        int requesterBenefit = 0;
//                        int workerBenefit = 0;
//                        for (int t = 0; t < 1; t++) {
//                            long startTime = System.currentTimeMillis();
//                            gaKnapsack = new GeneticAlgorithm(100, capOfPack, numOfThing, 5000, 0.5f, 0.01f, serveTimes, alphaArr[n], betaArr[m]);
//                            requesterBenefit = gaKnapsack.geneticAlgorithmProcess(0);
//                            workerBenefit = gaKnapsack.sumWeight();
//                            System.out.println("TPs benefit is:" + workerBenefit);
//                            long endTime = System.currentTimeMillis();
//                            int time = (int) (endTime - startTime);
//                            totalTime += time;
//                            System.out.println("plaintext running time: " + time + "ms");
//                        }
//                        System.out.println("ciphertext encrypt total time: " + totalTime + "ms");
//                        System.out.println("----------------------------------------------------------------------");
//                        Utils.writeResultToCsv("TDrive", "GA", alphaArr[n], betaArr[m], numOfParticipants,
//                                capOfPack, requesterBenefit, workerBenefit, totalTime, 0, 0, 0);
//                    }
//                }
//            }
//        }
//        Utils.Text2csv();
    }

    public static void main(String[] args) throws Exception {
        //alpha 1 2 3 4 5
        //vel=22 B=500
        //rate dp ga
        //keylen=128
        int[] keyIndex = new int[]{0, 1};

//        System.out.println("------------------------------------ratio alpha--------------------------------------");
        int[] alphaIndex = new int[]{0, 5};
        int[] budgetTDrive = new int[]{2000, 2000, 1};
        int[] numIndex = new int[]{0, 1};
//
//
//        runTDrive(alphaIndex, budgetTDrive, keyIndex, numIndex);

        //beta/alpha 1.0
        //vel=1,2,4,5,11,22 B=500
        //rate dp ga
        //keylen=128
//        System.out.println("------------------------------------worker number n--------------------------------------");
//        alphaIndex = new int[]{0, 1};
//        budgetTDrive = new int[]{2000, 2000, 1};
//        int[] budgetKnap = new int[]{997, 997, 1};
//        numIndex = new int[]{0, 6};

//        runTDrive(alphaIndex, budgetTDrive, keyIndex, numIndex);
//        runKanp(budgetKnap, velIndex, keyIndex);

        //beta/alpha 1.0
        //vel=22 B=100,1000,100
        //rate dp ga
        //keylen=128
        System.out.println("------------------------------------Budget B--------------------------------------");
        alphaIndex = new int[]{0, 1};
        budgetTDrive = new int[]{1000, 4000, 500};
        numIndex = new int[]{5, 6};

        runTDrive(alphaIndex, budgetTDrive, keyIndex, numIndex);
//        runKanp(budgetTDrive, velIndex, keyIndex);

        //beta/alpha 1.0
        //vel=22 B=500, 500, 1
        //rate dp ga
        //keylen=512, 768, 1024, 1280
//        System.out.println("------------------------------------keyLen K--------------------------------------");
//        alphaIndex = new int[]{0, 1};
//        budgetTDrive = new int[]{2000, 2000, 1};
//        keyIndex = new int[]{1, 5};
//        numIndex = new int[]{4, 5};

//        runTDrive(alphaIndex, budgetTDrive, keyIndex, numIndex);
    }
}
