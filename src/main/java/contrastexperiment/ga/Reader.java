package contrastexperiment.ga;

import utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:wbGuo
 * Date: 2022/12/20
 */

public class Reader {

    public static List<Object> read(String filenameItems) {
        List<List<Integer>> dataTemp = Utils.readRows(filenameItems, 3,2);
        int len = dataTemp.size();
        List<Object> data = new ArrayList<Object>();
        float[] weight = new float[len];
        float[] value = new float[len];
        for (int i = 0; i < len; i++) {
            weight[i] = Float.parseFloat(String.valueOf(dataTemp.get(i).get(0)));
            value[i] = Float.parseFloat(String.valueOf(dataTemp.get(i).get(1)));
        }
        data.add(weight);
        data.add(value);
        return data;
    }

    public static List<double[]> readKnap(String filenameItems) {
        List<List<Integer>> dataTemp = Utils.readRows(filenameItems, 3,2);
        int len = dataTemp.size();
        List<double[]> data = new ArrayList<double[]>();
        double[] weight = new double[len];
        double[] value = new double[len];
        for (int i = 0; i < len; i++) {
            weight[i] = Double.parseDouble(String.valueOf(dataTemp.get(i).get(0)));
            value[i] = Double.parseDouble(String.valueOf(dataTemp.get(i).get(1)));
        }
        data.add(weight);
        data.add(value);
        return data;
    }

    public static List<int[]> readZoKnap(String filenameItems) {
        List<List<Integer>> dataTemp = Utils.readRows(filenameItems, 1,1);
        int len = dataTemp.size();
        List<int[]> data = new ArrayList<int[]>();
        int[] weight = new int[len];
        int[] value = new int[len];
        for (int i = 0; i < len; i++) {
            weight[i] = Integer.parseInt(String.valueOf(dataTemp.get(i).get(0)));
            value[i] = Integer.parseInt(String.valueOf(dataTemp.get(i).get(1)));
        }
        data.add(weight);
        data.add(value);
        return data;
    }
}
