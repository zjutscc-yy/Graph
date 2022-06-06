package xml;

import bdi.gpt.generators.XMLWriter;
import bdi.gpt.structure.GoalNode;
import goalplantree.Literal;
import xml2bdi.XMLReader;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String gptPath = "F:\\project\\gpt\\5.xml";
        //读取文件
        File sourceFile = new File(gptPath);
        String line = "";
        List<String> fileCon = new ArrayList<>();
        try{
            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(sourceFile));
            BufferedReader br = new BufferedReader(fileReader);
            line = br.readLine();
            //这里读取了一行，就是文件的第一行
            while (line != null){
                //读取下一行
                if (line != null || !line.equals("")){
                    fileCon.add(line);
                }
                line = br.readLine();
            }
            // while读取了文件的所有内容
        }catch (IOException ioe){
            System.out.println("io异常");
        }
        //修改文件
        int environmentIndex = 0;
        boolean needEditFlag = false;
        Random rd = new Random();
        for (int i = 0; i < fileCon.size(); i++) {
            if (fileCon.get(i).contains("Literal")){
                // 如果文件某一行含有 Literal ，说明该行是environment 判断是否修改
                //先随便大概修改30个environment 的状态

                //判断是否需要修改
                needEditFlag = rd.nextDouble()<0.3;
                if (needEditFlag){
                    //需要修改这一行
                    //根据initVal把这一行分成两部分
                    String[] arrTemp = fileCon.get(i).split("initVal");
                    if (arrTemp[1].contains("true")){
                        arrTemp[1] = arrTemp[1].replace("true","false");
                    }else{
                        arrTemp[1] = arrTemp[1].replace("false","true");
                    }
                    // 上面把字符串换完了，之后把字符串写回去
                    fileCon.set(i,arrTemp[0]+"initVal"+arrTemp[1]);
                }
            }
        }
        //存储文件
        System.out.println("修改环境结束");

        String newPath = "F:\\project\\gpt\\gen5_Graph\\5.2.xml";
        File newFile = new File(newPath);
        try {
            newFile.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
            for (int i = 0; i < fileCon.size(); i++) {
                bw.write(fileCon.get(i)+"\r\n");    // 这里的r和n是换行
            }
            bw.flush();
            bw.close();
        }catch (IOException ioe){
            System.out.println("ioe");
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
