import rate.CloudPlatform;
import rate.TaskParticipants;
import rate.TaskRequester;
import utils.Utils;

import java.util.List;

/**
 * Author:wbGuo
 * Date: 2023/7/14
 */
public class LocalRun {
    public static void runTDrive(int[] alphaIndex, int[] betaIndex, int[] budget, int[] velIndex, int[] keyIndex) throws Exception {
        String filenameItem = "res/T-Drive/T-Drive-4.csv";
        String filenameInfo = "res/T-Drive/T-Drive-4-info.csv";
        int[] alphaArr = new int[]{5};
        int[] betaArr = new int[]{1, 2, 3, 4, 5};
        int[] velArr = new int[]{1, 2, 5, 6, 11, 22};
        int[] keyArr = new int[]{128, 512, 768, 1024, 1280};
        boolean containOneForm = true;
        Utils.csvHead();
        //rate
        System.out.println("-------------------TDrive--------------------");
        System.out.println("-------------------rate--------------------");
        for (int k = keyIndex[0]; k < keyIndex[1]; k++) {
            for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
                for (int m = betaIndex[0]; m < betaIndex[1]; m++) {
                    for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
                        if (containOneForm) {
                            for (int v = velIndex[0]; v < velIndex[1]; v++) {
                                long startTime = System.currentTimeMillis();
                                //set TP and TR
                                TaskRequester tr = new TaskRequester(b, 550, keyArr[k], filenameInfo);
                                long oneTime = System.currentTimeMillis();
                                TaskParticipants tp = new TaskParticipants(velArr[v], filenameItem);
                                long twoTime = System.currentTimeMillis();
                                //set CP and CSP
                                CloudPlatform cp = new CloudPlatform(alphaArr[n], betaArr[m], tr, tp);
                                int[] message = cp.solve();
                                long endTime = System.currentTimeMillis();

                                System.out.println("------------------------------------------");
                                Utils.writeResultToCsv("TDrive", "rate", alphaArr[n], betaArr[m], message[0],
                                        message[1], message[2], message[3], (int) (endTime - startTime), (int) (twoTime - oneTime), (int) (endTime - twoTime), keyArr[k]);
                            }
                        }
                    }
                }
            }
        }
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
        int[] velIndex = new int[]{5, 6};


        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex);

        //beta/alpha 1.0
        //vel=1,2,4,5,11,22 B=500
        //rate dp ga
        //keylen=128
        System.out.println("------------------------------------worker number n--------------------------------------");
        alphaIndex = new int[]{0, 1};
        betaIndex = new int[]{4, 5};
        budgetTDrive = new int[]{750, 750, 1};
        int[] budgetKnap = new int[]{997, 997, 1};
        velIndex = new int[]{0, 6};

        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex);
//        runKanp(budgetKnap, velIndex, keyIndex);

        //beta/alpha 1.0
        //vel=22 B=100,1000,100
        //rate dp ga
        //keylen=128
        System.out.println("------------------------------------Budget B--------------------------------------");
        alphaIndex = new int[]{0, 1};
        betaIndex = new int[]{4, 5};
        budgetTDrive = new int[]{100, 1000, 100};
        velIndex = new int[]{5, 6};

        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex);
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

        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex);
    }
}
