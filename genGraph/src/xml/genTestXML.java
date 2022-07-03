package xml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 改变环境变量
 * 1.原本的gpt路径
 * 2.修改环境变量的概率
 * 3.生成xml文件个数
 * <p>
 * 以上在config中修改
 * <p>
 * 修改后的xml文件放置路径（在main中修改）
 */

public class genTestXML {
    public static void main(String[] args) {
        //要修改的树的存储路径
        String gptFilePath;
        //改动环境变量的比例（只改影响结果大的环境变量）
        double rate;
        //生成新的xml（新环境）的个数
        int genAmount;
        //原本树中环境变量的个数
        int envirNum = 0;
        //需要修改的环境变量个数
        int changeNum = 0;

        if (args.length == 0) {
            System.out.println("ERROR: no GPT file specified!");
            return;
        }
        gptFilePath = args[0];

        rate = Double.valueOf(args[1]);

        genAmount = Integer.parseInt(args[2]);

        //读取文件
        File sourceFile = new File(gptFilePath);
        String line = "";
        List<String> fileCon = new ArrayList<>();
        try {
            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(sourceFile));
            BufferedReader br = new BufferedReader(fileReader);
            line = br.readLine();
            //这里读取了一行，就是文件的第一行
            while (line != null) {
                //读取下一行
                if (line != null || !line.equals("")) {
                    fileCon.add(line);
                    if (line.contains("Literal") && !line.contains("G-")) {
                        envirNum++;
                    }
                }
                line = br.readLine();
            }
            // while读取了文件的所有内容
        } catch (IOException ioe) {
            System.out.println("io异常");
        }

        //上面读取了文件,下面就是修改和生成
        String newPath = "F:\\project\\gpt\\TestGraph_5_120_0.1\\5.";   //生成的文件的名字
        changeNum = (int) (rate * envirNum);

        for (int i = 0; i < genAmount; i++) {
            List<String> newArr = new ArrayList<>();
            /**
             * 如果是基本数据类型，那么是赋值，如果是引用数据类型，那么是引用（也就是两个变量指向同一个地址，改变其中一个，那么相应的另外一个也相应变化）
             */
            newArr.addAll(fileCon);     //基本不用判断
            //修改文件
            boolean needEditFlag = false;
            Random rd = new Random();
            //修改的变量个数
            int m = 0;
            //遍历获取到的xml文件的每一行
            while (m < changeNum) {
                for (int j = 0; j < newArr.size(); j++) {
                    // 如果文件某一行含有 Literal ，说明该行是environment 判断是否修改
                    if (newArr.get(j).contains("Literal") && !newArr.get(j).contains("G-")) {
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
            //存储文件
            System.out.println("修改环境结束");
            System.out.println("该文件修改了" + m + "个环境变量");
            // control + alt + L
            File newFile = new File(newPath + (i + 1) + ".xml");
            try {
                newFile.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
                for (int j = 0; j < newArr.size(); j++) {
                    bw.write(newArr.get(j) + "\r\n");    // 这里的r和n是换行
                }
                bw.flush();
                bw.close();
            } catch (IOException ioe) {
                System.out.println("另存为XML文件失败");
            }
        }
    }
}
