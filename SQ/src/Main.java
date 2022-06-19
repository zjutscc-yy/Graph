
import goalplantree.*;
import simulation.Simulator;
import xml2bdi.XMLReader;
import environment.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import agent.*;


public class Main {

    static long startTime = System.currentTimeMillis();

    public static void main(String[] args) throws IOException {

//        List<File> fileList = getFileList("tarDir");
        //for循环file列表

//        startTime = System.currentTimeMillis();
//        long startAll = System.currentTimeMillis();
        double total = 0;
        int type;
        int testNum;
        List<Integer> resultList = new ArrayList<>();

        String gptFilePath;
        XMLReader reader;

        if (args.length == 0) {
            System.out.println("ERROR: no GPT file specified!");
            return;
        }
        gptFilePath = args[0];


        // read the type
        try {
            type = Integer.parseInt(args[1]);
            if (type < 2 || type > 5) {
                type = 2;
            }
        } catch (Exception e) {
            type = 0;
        }
        // read the test num
        try {
            testNum = Integer.parseInt(args[2]);
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
            // check the number of goals achieved
            System.out.println(agent.getNumAchivedGoal());
            resultList.add(agent.getNumAchivedGoal());
            total += agent.getNumAchivedGoal();
//            long end = System.currentTimeMillis();

//            long endAll = System.currentTimeMillis();
//            System.out.println("程序运行时间" + (endAll - startAll));

//            System.out.println("程序运行时间" + (end - startTime));
            FileWriter actionPath  = new FileWriter("actions6.txt",true);
            if (m != testNum - 1){
                actionPath.append("//");
                actionPath.append("\n");
                actionPath.append("\n");
            }
            actionPath.close();

        }
        int x = 0;
        for (int i = 0; i < resultList.size(); i++) {
            x += resultList.get(i);
        }
        System.out.println(resultList);
        System.out.println("一共测试" + testNum + "次");
        double averageAchieveGoal = x / (double) resultList.size();
        System.out.println("平均实现目标数：" + averageAchieveGoal);
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
        //if istxt
        List<File> sourceList = new ArrayList<>();
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
}


