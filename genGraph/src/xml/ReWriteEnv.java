package xml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReWriteEnv {
    //要修改的树的存储路径
    String gptFilePath;
    //改动环境变量的比例（只改影响结果大的环境变量）
    double rate;
    //原本树中环境变量的个数
    int envirNum = 0;
    //原来xml文件中的每一行
    List<String> fileCon = new ArrayList<>();
    //修改后的xml文件
    List<String> newArr = new ArrayList<>();

    public ReWriteEnv(String gptFilePath, double rate) {
        this.gptFilePath = gptFilePath;
        this.rate = rate;
    }

    //把xml文件的每一行都存到filCon中
    public void readLine(){
        //读取文件
        File sourceFile = new File(gptFilePath);
        String line = "";
        try{
            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(sourceFile));
            BufferedReader br = new BufferedReader(fileReader);
            line = br.readLine();
            //这里读取了一行，就是文件的第一行
            while (line != null){
                //读取下一行
                if (line != null || !line.equals("")){
                    fileCon.add(line);
                    if (line.contains("Literal") && !line.contains("G-")){
                        envirNum++;
                    }
                }
                line = br.readLine();
            }
            // while读取了文件的所有内容
        }catch (IOException ioe){
            System.out.println("io异常");
        }
    }

    //改写一定概率的环境变量
    public void reWrite(){
        //需要修改的环境变量个数
        int changeNum = (int) (rate * envirNum);
        newArr = fileCon;
        //修改文件
        boolean needEditFlag = false;
        Random rd = new Random();
        //修改的变量个数
        int m = 0;
        //遍历获取到的xml文件的每一行
        for (int j = 0; j < newArr.size(); j++) {
            // 如果文件某一行含有 Literal ，说明该行是environment 判断是否修改
            if (newArr.get(j).contains("Literal")){
                String[] str = newArr.get(j).split("\"");
                String envName = str[1];
                //判断是否需要修改
                needEditFlag = rd.nextDouble() < rate;
                if (needEditFlag && m < changeNum) {
                    m++;
                    //需要修改这一行
                    //根据initVal把这一行分成两部分
                    String[] arrTemp = newArr.get(j).split("initVal");
                    if (arrTemp[1].contains("true")) {
                        arrTemp[1] = arrTemp[1].replace("true", "false");
                    } else {
                        arrTemp[1] = arrTemp[1].replace("false", "true");
                    }
                    // 上面把字符串换完了，之后把字符串写回去
                    newArr.set(j, arrTemp[0] + "initVal" + arrTemp[1]);
                }
            }
        }
    }

    //另存为文件
//    public void {
//        //存储文件
//        File newFile = new File(newPath + (i + 1) + ".xml");
//        try {
//            newFile.createNewFile();
//            BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
//            for (int j = 0; j < newArr.size(); j++) {
//                bw.write(newArr.get(j) + "\r\n");    // 这里的r和n是换行
//            }
//            bw.flush();
//            bw.close();
//        } catch (IOException ioe) {
//            System.out.println("另存为XML文件失败");
//        }
//    }

}
