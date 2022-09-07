package xml;

/*生成有用的环境
 要修改的树的存储路径 config
 修改后的xml文件放置路径（在main中修改）

 */

import goalplantree.GoalNode;
import xml2bdi.XMLReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class genEnableEnv {
    public static ArrayList<HashMap<String, String>> goalAchieve;
    public static void main(String[] args) {
        //要修改的树的存储路径
        String gptFilePath;

        if (args.length == 0) {
            System.out.println("ERROR: no GPT file specified!");
            return;
        }
        gptFilePath = args[0];

        XMLReader reader = new XMLReader(gptFilePath);

        SummaryEnv summaryEnv = new SummaryEnv(gptFilePath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        genAllCase gens = new genAllCase(absolutetEnv);

        //保存所有目标的可能计划
        goalAchieve = new ArrayList<>();
        ArrayList<GoalNode> tlgs = reader.getTlgs();

        for (GoalNode tlg : tlgs) {
            ArrayList<HashMap<String, String>> hashMaps = gens.checkGoal(tlg);
            goalAchieve = genAllCase.mergeGoal(goalAchieve,hashMaps);
        }

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
                }
                line = br.readLine();
            }
            // while读取了文件的所有内容
        } catch (IOException ioe) {
            System.out.println("io异常");
        }

        //上面读取了文件,下面就是修改和生成
        String newPath = "F:\\project\\gpt\\gen1\\1.";   //生成的文件的名字

        int j = 0;
        for (HashMap<String, String> map : goalAchieve) {
            j++;
            List<String> newArr = new ArrayList<>();
            newArr.addAll(fileCon);
            //遍历文件的每一行
            for (int i = 0; i < newArr.size(); i++) {
                //如果是环境变量
                if (newArr.get(i).contains("Literal") && !newArr.get(i).contains("G-")){
                    String[] str = newArr.get(i).split("\"");
                    String envName = str[1];
                    if (map.keySet().contains(envName)){
                        //根据initVal把这一行分成两部分
                        String[] arrTemp = newArr.get(i).split("initVal");
                        if (!arrTemp[1].contains(map.get(envName))){
                            if (arrTemp[1].contains("true")) {
                                arrTemp[1] = arrTemp[1].replace("true", "false");
                            } else {
                                arrTemp[1] = arrTemp[1].replace("false", "true");
                            }
                            // 上面把字符串换完了，之后把字符串写回去
                            newArr.set(i, arrTemp[0] + "initVal" + arrTemp[1]);
                        }
                    }
                }
            }
            //存储文件
            // control + alt + L
            File newFile = new File(newPath + j + ".xml");
            try {
                newFile.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(newFile));
                for (int m = 0; m < newArr.size(); m++) {
                    bw.write(newArr.get(m) + "\r\n");    // 这里的r和n是换行
                }
                bw.flush();
                bw.close();
            } catch (IOException ioe) {
                System.out.println("另存为XML文件失败");
            }
        }
    }
}
