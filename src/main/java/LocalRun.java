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
        int[] velArr = new int[]{1, 2, 4, 5, 11, 22};
        int[] keyArr = new int[]{128, 512, 768, 1024, 1280};
        int keyLen = keyArr[keyIndex[0]];
        boolean containOneForm = true;
        Utils.csvHead();
        //rate
        System.out.println("-------------------TDrive--------------------");
        System.out.println("-------------------rate--------------------");
        for (int n = alphaIndex[0]; n < alphaIndex[1]; n++) {
            for (int m = betaIndex[0]; m < betaIndex[1]; m++) {
                for (int b = budget[0]; b <= budget[1]; b = b + budget[2]) {
                    if (containOneForm) {
                        for (int v = velIndex[0]; v < velIndex[1]; v++) {
                            //set TP and TR
                            TaskParticipants tp = new TaskParticipants(velArr[v], filenameItem);
                            TaskRequester tr = new TaskRequester(b, 600, filenameInfo);
                            //set CP and CSP
                            CloudPlatform cp = new CloudPlatform(alphaArr[n], betaArr[m], tr, tp);
                            cp.solve();
                            }
                        }
                    }
                }
            }
        }

    public static void main(String[] args) throws Exception {
        //beta/alpha 0.2 0.4 0.6 0.8 1.0
        //vel=22 B=500
        //rate dp ga
        //keylen=128

        System.out.println("------------------------------------ratio beta/alpha--------------------------------------");
        int[] alphaIndex = new int[]{0, 1};
        int[] betaIndex = new int[]{0, 1};
        int[] budgetTDrive = new int[]{750, 750, 1};
        int[] velIndex = new int[]{5, 6};
        int[] keyIndex = new int[]{0, 1};

        runTDrive(alphaIndex, betaIndex, budgetTDrive, velIndex, keyIndex);
    }
}
