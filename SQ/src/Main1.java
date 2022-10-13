
import goalplantree.*;
import simulation.Simulator;
import xml2bdi.XMLReader;
import environment.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agent.*;

/**
 * 需要的参数
 * 1.文件夹路径（遍历）  main
 * 2.智能体类型  config
 * 3.每个文件测试次数  config
 *
 * 记得需要写入txt文件
 * main函数里的txt文件名也要改
 */

public class Main1 {
    static boolean isFirst = true;
    static long startTime = System.currentTimeMillis();
    static ArrayList<String> exeXML = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        List<File> fileList = getFileList("F:\\project\\gpt\\8\\8gen");
        String newFilePath = "F:\\project\\gpt\\8\\envs_txt";

//        startTime = System.currentTimeMillis();
//        long startAll = System.currentTimeMillis();
        double total = 0;
        int type;
        int testNum;
        List<Integer> resultList = new ArrayList<>();

        String gptFilePath;
        XMLReader reader;

        //遍历改变环境后的文件夹里的所有xml文件
        for (int j = 0; j < fileList.size(); j++) {
            gptFilePath = fileList.get(j).getPath();

            // read the type
            try {
                type = Integer.parseInt(args[0]);
                if (type < 2 || type > 5) {
                    type = 2;
                }
            } catch (Exception e) {
                type = 0;
            }
            // read the test num
            try {
                testNum = Integer.parseInt(args[1]);
            } catch (Exception e) {
                testNum = 10;
            }

            System.out.println("type: " + type);

            Simulator simulator = new Simulator();

            for (int m = 0; m < testNum; m++) {
                startTime = System.currentTimeMillis();
                try {
                    reader = new XMLReader(gptFilePath);
                } catch (Exception e) {
                    System.out.println("ERROR: unable to open GPT file: " + gptFilePath);
                    return;
                }


                // get the list of literals in the environment
                ArrayList<Literal> literals = reader.getLiterals();
                // get the list of goals
                ArrayList<GoalNode> tlgs = reader.getTlgs();


                if (type == 5) {
                    for (int i = 0; i < tlgs.size(); i++) {
                        simulator.runSimulation(10000, tlgs.get(i));
                        System.out.println("sim:" + i);
                    }
                }


                // build the environment
                SynthEnvironment environment = new SynthEnvironment(literals, 0);
                System.out.println(environment.onPrint());
                System.out.println("--------------------------------------------------------");

                // build the agent
                ArrayList<Belief> bs = new ArrayList<>();
                for (Literal l : literals) {
                    bs.add(new Belief(l.getName(), l.getState() ? 1 : 0));
                }

                // build the fifo agent
                MCTSAgent mctsAgent = new MCTSAgent("MCTS-Agent", bs, tlgs);
                SPMCTSAgent spmctsAgent = new SPMCTSAgent("SPMCTS-Agent", bs, tlgs);
                QSISPMCTSAgent qsispmctsAgent = new QSISPMCTSAgent("QSISPMCTS-Agent", bs, tlgs);

                AbstractAgent agent = null;

                System.out.println("type:" + type);
                switch (type) {
                    case 2:
                        agent = mctsAgent;
                        break;
                    case 3:
                        agent = spmctsAgent;
                        break;
                    case 5:
                        agent = qsispmctsAgent;
                        break;
                }

                // add this agent to the environment
                environment.addAgent(agent);

                boolean running = true;
//            startTime = System.currentTimeMillis();
//            long startAll = System.currentTimeMillis();
                int step = 1;
                while (running) {
                    System.out.println("---------------------step " + step + "------------------------------");
                    running = environment.run();
                    step++;
                }

                if(agent.getNumAchivedGoal() != tlgs.size()){
                    reWriteFileEnd("F:\\project\\SQ-MCTS\\actions_8.txt");
                }else {
                    resultList.add(agent.getNumAchivedGoal());
                    copyFile(fileList.get(j).getAbsolutePath(),newFilePath+"\\"+fileList.get(j).getName());
                }

                // check the number of goals achieved
                System.out.println(agent.getNumAchivedGoal());
                total += agent.getNumAchivedGoal();
//            long end = System.currentTimeMillis();

//            long endAll = System.currentTimeMillis();
//            System.out.println("程序运行时间" + (endAll - startAll));

//            System.out.println("程序运行时间" + (end - startTime));
                FileWriter actionPath  = new FileWriter("actions_8.txt",true);

                actionPath.append("//");
                actionPath.append("\n");
                actionPath.append("\n");
                actionPath.close();

                //对每个文件测试testNum次，一旦找到目标全部实现的，则再跑下一个xml文件
                if (agent.getNumAchivedGoal() == tlgs.size()){
                    exeXML.add(fileList.get(j).getName());
                    break;
                }

            }

        }
        int x = 0;
        for (int i = 0; i < resultList.size(); i++) {
            x += resultList.get(i);
        }
        System.out.println(resultList);
        System.out.println("一共生成" + resultList.size() + "条路径");
        double averageAchieveGoal = x / (double) resultList.size();
        System.out.println("平均实现目标数：" + averageAchieveGoal);
        System.out.println(exeXML);
    }

    static boolean isTimeEnd() {
        return (startTime + 27000 > System.currentTimeMillis());
    }


    /**
     * 获取文件夹下的文件列表
     *
     * @param dirStr 文件夹的路径
     * @return
     */
    public static List<File> getFileList(String dirStr) {
//        File dir = new File(dirStr);
//        if (!dir.exists()){
//            System.out.println("目录不存在");
//        }

        //if istxt
        File file = new File(dirStr);
        List<File> sourceList = Arrays.stream(file.listFiles()).toList();
        List<File> resultList = new ArrayList<>();

        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i).isFile()) {
                if (sourceList.get(i).getName().contains("txt")) {
                    System.out.println(sourceList.get(i).getName());
                } else {
                    resultList.add(sourceList.get(i));
                }
            }
        }
        return resultList;
    }

    static void reWriteFileEnd(String sourceFilePath){
        List<String> filecon = new ArrayList<>();
//        if (isFirst){
//
//        }
        //新建文件对象
        File sourceFile = new File(sourceFilePath);
        File newFile = new File(sourceFilePath+"test");
        PrintWriter pw ;
        try {
            BufferedReader br = new BufferedReader(new FileReader(sourceFile));
            pw = new PrintWriter(new FileWriter(newFile));
            String nextLine = br.readLine();
            while (nextLine!=null){
                //读取文件内容
                filecon.add(nextLine);
                nextLine = br.readLine();
            }
            br.close();
            //找到最后一个双斜线
            int lastIndex = filecon.lastIndexOf("//");
            //把写入 // 之前的内容
            for (int i = 0; i < lastIndex; i++) {
                pw.print(filecon.get(i)+"\n");
                pw.flush();
            }
            pw.flush();
            pw.close();
            //删除
            sourceFile.delete();
            //重命名
            newFile.renameTo(sourceFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void copyFile(String source,String dest) throws Exception{
        FileInputStream in = new FileInputStream(new File(source));
        FileOutputStream out = new FileOutputStream(new File(dest));
        byte[] buff = new byte[512];
        int n = 0;
        while ((n = in.read(buff)) != -1){
            out.write(buff,0,n);
        }
        out.flush();
        in.close();
        out.close();
    }
}



