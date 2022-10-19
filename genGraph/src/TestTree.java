import agent.*;
import environment.SynthEnvironment;
import goalplantree.GoalNode;
import goalplantree.Literal;
import simulation.Simulator;
import xml2bdi.XMLReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对完全陌生的环境进行跑树测试
 *
 * 需要的参数为
 * 1.文件夹路径（遍历）  main
 * 2.智能体类型  config
 * 3.每个文件测试次数  config
 *
 * 记得不需写入txt文件
 */

public class TestTree {
    static boolean isFirst = true;
    static long startTime = System.currentTimeMillis();

    public static void main(String[] args) throws IOException {

        List<File> fileList = getFileList("F:\\project\\gpt\\8\\test4");

//        startTime = System.currentTimeMillis();
//        long startAll = System.currentTimeMillis();
        double total = 0;
        int type;
        int testNum;
        List<Integer> resultList = new ArrayList<>();

        String gptFilePath;
        XMLReader reader;

        long start = System.currentTimeMillis();
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
                // check the number of goals achieved
                System.out.println(agent.getNumAchivedGoal());
                resultList.add(agent.getNumAchivedGoal());
                total += agent.getNumAchivedGoal();
//            long end = System.currentTimeMillis();

//            long endAll = System.currentTimeMillis();
//            System.out.println("程序运行时间" + (endAll - startAll));

//            System.out.println("程序运行时间" + (end - startTime));
            }

        }

        long end = System.currentTimeMillis();
        System.out.println("程序运行时间" + (end - start));
        int x = 0;
        for (int i = 0; i < resultList.size(); i++) {
            x += resultList.get(i);
        }
        System.out.println(resultList);
        System.out.println("一共测试了" + resultList.size() + "个陌生环境");
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
}
