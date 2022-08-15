package xml;

import java.io.*;
import java.util.*;

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

public class Main {
    public static void main(String[] args) {
        //要修改的树的存储路径
        String gptFilePath;
        //改动环境变量的比例（只改影响结果大的环境变量）
        double rate;
        //生成新的xml（新环境）的个数
        int genAmount;
        //原本树中环境变量的个数
        int envirNum = 0;
        //整体需要修改的环境变量个数
        int changeNum = 0;
        //absolutetEnv中需要修改的概率
        double absoluteRate = 0.0;

        //例如：要改12个，实际absoluteEnv小于9个，就需要修改其余的，修改其余的个数
        int restNum = 0;
        //其余的修改的概率
        double restRate = 0;


        if (args.length == 0) {
            System.out.println("ERROR: no GPT file specified!");
            return;
        }
        gptFilePath = args[0];

        //获取到完全来自于环境的环境变量
        SummaryEnv summaryEnv = new SummaryEnv(gptFilePath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

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
        String newPath = "F:\\project\\gpt\\genGraph_5_0.2\\5.";   //生成的文件的名字
        changeNum = (int) (rate * envirNum);

        absoluteRate = (double) changeNum / absolutetEnv.size();

        if (changeNum > absolutetEnv.size()) {
            //120个要改12个，有9个是必须改，剩下要在111里挑3个改
            restNum = changeNum - absolutetEnv.size();//3
            restRate = (double) restNum / (envirNum - absolutetEnv.size());
        }

        for (int i = 0; i < genAmount; i++) {
            List<String> newArr = new ArrayList<>();
            /**
             * 如果是基本数据类型，那么是赋值，如果是引用数据类型，那么是引用（也就是两个变量指向同一个地址，改变其中一个，那么相应的另外一个也相应变化）
             */
            newArr.addAll(fileCon);     //基本不用判断
            //修改文件
            boolean needEditFlag = false;
            Random rd = new Random();
//            boolean isCurrentLineEdited = false;
            //修改的变量个数
            int m = 0;  //已经必须修改的
            int n = 0;  //剩余的
            int mustEdit = (changeNum > absolutetEnv.size()) ? (absolutetEnv.size()) : changeNum;// 12或8
            int restEdit = (changeNum > mustEdit) ? (changeNum - mustEdit) : 0;//12-9或0(9=9下为0)
            ArrayList<Integer> hasEditLineArr = new ArrayList<>();
            //遍历获取到的xml文件的每一行
            while (m + n < changeNum) {     //满足 必须修改和非必须没修改完  m=9 n=2
                for (int j = 0; j < newArr.size(); j++) {
                    // 如果文件某一行含有 Literal ，说明该行是environment 判断是否修改
//                    isCurrentLineEdited = false;
                    if (newArr.get(j).contains("Literal") && !newArr.get(j).contains("G-")) {
                        String[] str = newArr.get(j).split("\"");
                        String envName = str[1];
                        for (String s : absolutetEnv) {
                            if (envName.equals(s)) {
                                //判断是否需要修改
                                needEditFlag = rd.nextDouble() < absoluteRate;
                                if (needEditFlag && m < mustEdit && !hasEditLineArr.contains(j)) {
                                    m++;
                                    hasEditLineArr.add(j);
                                    //需要修改这一行
//                                    isCurrentLineEdited = true;
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
                        //这一行不是必须要改的，例如：要改12个，实际absoluteEnv小于9个，就需要修改其余的
//                        if (changeNum > absolutetEnv.size()) {
                        if (restEdit > n && !hasEditLineArr.contains(j)) {
                            needEditFlag = rd.nextDouble() < restRate;
                            if (needEditFlag) {
//                                m++;
                                n++;
                                hasEditLineArr.add(j);
                                //需要修改这一行
                                //根据initVal把这一行分成两部分
                                String[] arr = newArr.get(j).split("initVal");
                                if (arr[1].contains("true")) {
                                    arr[1] = arr[1].replace("true", "false");
                                } else {
                                    arr[1] = arr[1].replace("false", "true");
                                }
                                // 上面把字符串换完了，之后把字符串写回去
                                newArr.set(j, arr[0] + "initVal" + arr[1]);
                            }
                        }

                    }
                }
            }
            //存储文件
//            System.out.println("修改环境结束");
//            System.out.println("该文件修改了" + m + "个环境变量");
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

        //获取原本环境变量
//        XMLReader reader = new XMLReader(gptPath);
//        ArrayList<Literal> literals = reader.getLiterals();
//        HashMap<String, bdi.gpt.structure.Literal> map = new HashMap<>();
//        for (int i = 0; i < literals.size(); i++) {
//            bdi.gpt.structure.Literal LiterTemp = new bdi.gpt.structure.Literal(literals.get(i).getName(),literals.get(i).getState(),true,false);
//            map.put(literals.get(i).getName(),LiterTemp);
//        }
//        ArrayList<GoalNode> tlgs = reader.getTlgs();
//        ArrayList<goalplantree.GoalNode> tlgsGoalTree = new ArrayList<>();
//        for (int i = 0; i < tlgs.size(); i++) {
//            tlgsGoalTree.add(tlgs.get(i))
//        }
//
//
//
//        //修改环境变量
//        XMLUtils xmlUtils = new XMLUtils();
//
//        //获取环境变量的true和false
//        boolean[] envList = xmlUtils.getBooleanList(literals);
//
//        //改变环境变量
//        boolean[] afterChange = xmlUtils.changeBooleanListState(envList, 0.3);
//
//        //获取改变后的环境变量
//        ArrayList<Literal> changedLiterals = xmlUtils.changeLiteralState(afterChange, literals);
//
//        //测试环境变量是否变化
////        for (Literal changedLiteral : changedLiterals) {
////            System.out.println(changedLiteral.getName() + " " + changedLiteral.getState());
////        }
//
//
//        XMLWriter xmlWriter = new XMLWriter();
////        xmlWriter.CreateXML(literals,tlgs,"test");
//
//        //存一个新文件
//        xmlWriter.CreateXML(map,tlgs,"path");
    }
}
