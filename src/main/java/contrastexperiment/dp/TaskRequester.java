package contrastexperiment.dp;

import utils.sg.smu.securecom.protocol.Paillier;
import utils.sg.smu.securecom.protocol.PaillierThdDec;
import utils.sg.smu.securecom.utils.Keys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:wbGuo
 * Date: 2023/7/14
 * Task requester: send the encrypted task location, budget and task time to cp.
 * generate the private key and public key. and send them to the related entities.
 */
public class TaskRequester {
    /**
     * the location of task
     */
    protected List<Integer> taskLoc = new ArrayList<>();

    /**
     * the budget of task requester
     */
    public int budget;

    /**
     * the time of whole task.
     */
    public int taskTime = 0;
    protected String pathInfo;
    private static final int NUMBER = 111000;
    //512\768\1024\1280\1536

    public TaskRequester() {

    }

    /**
     * when you use this class, you can modify the SecureTaskRequester to
     * update the information, such as budget, taskTime and pathInfo.
     */
    public TaskRequester(int b, int t, String p) {
        budget = b;
        taskTime = t;
        pathInfo = p;

        readData();
    }


    /**
     * read data to form pattern
     */

    private void readData() {
        try {
            File file = new File(pathInfo);
            BufferedReader readCsv = new BufferedReader(new FileReader(file));
            readCsv.readLine();
            String[] temp = readCsv.readLine().split(",");
            taskLoc.add((int) (Double.valueOf(temp[0].split("'")[1]) * NUMBER));
            taskLoc.add((int) (Double.valueOf(temp[1].split("'")[1]) * NUMBER));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
