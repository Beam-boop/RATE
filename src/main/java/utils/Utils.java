package utils;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.processor.RowListProcessor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParser;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author:wbGuo
 * Date: 2022/12/13
 */
public class Utils {
    /**
     * get the line number / the number of things
     *
     * @param filename the path of file
     * @return the line number
     */
    public int parseCSV(String filename) {

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        RowListProcessor rowListProcessor = new RowListProcessor();
        parserSettings.setProcessor(rowListProcessor);     //配置解析器

        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setLineSeparatorDetectionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new File(filename));
//        String[] headers = rowListProcessor.getHeaders();
        List<String[]> rows = rowListProcessor.getRows();
        return rows.size();
    }

    /**
     * Reads the data for the specified column of the file.
     * the first position is zero.
     *
     * @param filename the path of file
     * @param index    the index of column, int 1,2
     * @return List data
     * @throws IOException IOException
     */
    public static List<Integer> readColumn(String filename, int index) {
        List<Integer> data = new ArrayList<>();
        data.add(0);
        try {
            File file = new File(filename);
            BufferedReader readCsv = new BufferedReader(new FileReader(file));
            readCsv.readLine();
            String temp;
            while ((temp = readCsv.readLine()) != null) {
                data.add(Integer.parseInt(temp.split(",")[index - 1]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public static List<List<Integer>> readRows(String filename, int index, int length) {
        List<List<Integer>> data = new ArrayList<>();
        try {
            File file = new File(filename);
            BufferedReader readCsv = new BufferedReader(new FileReader(file));
            readCsv.readLine();
            String temp;
            while ((temp = readCsv.readLine()) != null) {
                List<Integer> row = new ArrayList<>();
                for (int i = 1; i <= length; i++) {
                    row.add(Integer.parseInt(temp.split(",")[index - i]));
                }
                data.add(row);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public static void solve(BufferedWriter writeText, HashMap<String, String> resultMap) throws IOException {
        //调用write的方法将字符串写到流中

        writeText.append(resultMap.get("dataMode") + ',');
        writeText.append(resultMap.get("solutionMode") + ',');
        writeText.append(resultMap.get("ratio") + ',');
        writeText.append(resultMap.get("number") + ',');
        writeText.append(resultMap.get("B") + ',');
        writeText.append(resultMap.get("request") + ',');
        writeText.append(resultMap.get("worker") + ',');
        writeText.append(resultMap.get("time") + ',');
        writeText.append(resultMap.get("keyLen") + ',');
        writeText.newLine();

    }

    public static void solve_(BufferedWriter writeText, HashMap<String, String> resultMap) throws IOException {
        //调用write的方法将字符串写到流中

        writeText.append(resultMap.get("dataMode") + ',');
        writeText.append(resultMap.get("solutionMode") + ',');
        writeText.append(resultMap.get("ratio") + ',');
        writeText.append(resultMap.get("number") + ',');
        writeText.append(resultMap.get("B") + ',');
        writeText.append(resultMap.get("TR benefit") + ',');
        writeText.append(resultMap.get("TPs benefit") + ',');
        writeText.append(resultMap.get("TR time") + ',');
        writeText.append(resultMap.get("TPs time") + ',');
        writeText.append(resultMap.get("cp time") + ',');
        writeText.append(resultMap.get("keyLen") + ',');
        writeText.newLine();

    }

    public static void csvHead() throws Exception {
        // 如果该目录下不存在该文件，则文件会被创建到指定目录下。如果该目录有同名文件，那么该文件将被覆盖。
        String FilePath = "result.csv";
        File file = new File(FilePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            //通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
            BufferedWriter writeText = new BufferedWriter(new FileWriter(FilePath, true));
            writeText.append("Dataset" + ',');
            writeText.append("Algorithm" + ',');
            writeText.append("Ratio" + ',');
            writeText.append("Number" + ',');
            writeText.append("Budget" + ',');
            writeText.append("TR Benefit" + ',');
            writeText.append("TPs Benefit" + ',');
            writeText.append("TR Time" + ',');
            writeText.append("TPs Time" + ',');
            writeText.append("cp Time" + ',');
            writeText.append("KeyLen" + ',');
            writeText.newLine();    //换行
            writeText.flush();
            writeText.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }

    //输出结果为csv文件
    public static void Text2csv(HashMap<String, String> resultMap) throws Exception {
        // 如果该目录下不存在该文件，则文件会被创建到指定目录下。如果该目录有同名文件，那么该文件将被覆盖。
        String FilePath = "result.csv";
        File file = new File(FilePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            //通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
            BufferedWriter writeText = new BufferedWriter(new FileWriter(FilePath, true));
            solve_(writeText, resultMap);
            writeText.flush();
            writeText.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }

    public static void Text2csv() throws Exception {
        // 如果该目录下不存在该文件，则文件会被创建到指定目录下。如果该目录有同名文件，那么该文件将被覆盖。
        String FilePath = "result.csv";
        File file = new File(FilePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            //通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
            BufferedWriter writeText = new BufferedWriter(new FileWriter(FilePath, true));
            writeText.newLine();
            writeText.flush();
            writeText.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }

    public static void writeText(String data, int t) throws Exception {
        String FilePath = "p08Ga/GAResult_" + t + ".txt";
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(FilePath, true)));
            bf.write(data + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeResultToCsv(String dataMode, String solutionMode, double alpha, int number, int B, double participantWelfare, int requesterRevenue, int requestTime, int workerTime, int cpTime, int keyLen) throws Exception {
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put("dataMode", dataMode);
        resultMap.put("solutionMode", solutionMode);
        if (alpha == 0) {
            resultMap.put("ratio", "---");
        } else {
            resultMap.put("ratio", String.valueOf(alpha));
        }
        resultMap.put("number", String.valueOf(number));
        resultMap.put("B", String.valueOf(B));
        resultMap.put("TR benefit", String.valueOf(requesterRevenue));
        resultMap.put("TPs welfare", String.valueOf(participantWelfare));
        resultMap.put("TR time", String.valueOf(requestTime));
        resultMap.put("TPs time", String.valueOf(workerTime));
        resultMap.put("cp time", String.valueOf(cpTime));
        resultMap.put("keyLen", String.valueOf(keyLen));
        Utils.Text2csv(resultMap);
    }

    public static void writeTimeToCsv(List<int[]> timeList) throws IOException {
        // 如果该目录下不存在该文件，则文件会被创建到指定目录下。如果该目录有同名文件，那么该文件将被覆盖。
        String FilePath = "result.csv";
        File file = new File(FilePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        try {
            //通过BufferedReader类创建一个使用默认大小输出缓冲区的缓冲字符输出流
            BufferedWriter writeText = new BufferedWriter(new FileWriter(FilePath, true));
            for (int i = 0; i < timeList.size(); i++) {
                for (int j = 0; j < timeList.get(i).length; j++) {
                    writeText.append(String.valueOf(timeList.get(i)[j]) + ',');
                }
                writeText.newLine();
            }
            writeText.flush();
            writeText.close();
        } catch (FileNotFoundException e) {
            System.out.println("没有找到指定文件");
        } catch (IOException e) {
            System.out.println("文件读写出错");
        }
    }
}
