import agent.AbstractAgent;
import agent.Belief;
import agent.MCTSAgent;
import environment.SynthEnvironment;
import goalplantree.GoalNode;
import goalplantree.Literal;
import xml2bdi.XMLReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test_NLearning {
    public static void main(String[] args) throws IOException {
        //写数据
//        File actionPath1 = new File("F:\\project\\SQ-MCTS\\data_N_MCTS.txt");
        FileWriter actionPath = new FileWriter("data_N_MCTS.txt", true);

        XMLReader reader;
        //测试时环境
        String trainPath;

        //获取总目录
        File file = new File("F:\\project\\gpt\\End8\\zongtest");
        List<File> sourceList = Arrays.stream(file.listFiles()).toList();

        //遍历每个测试集
        for (int i = 0; i < sourceList.size(); i++) {
            //每个测试集新建一个存储时间和目标的list
            List<Long> allTime = new ArrayList<>();
            List<Double> allAchievedGoal = new ArrayList<>();

            for (int m = 0; m < 50; m++) {
//                System.out.println(sourceList.get(i).getName() + "第" + m + "次");
                List<Integer> thisAverageAchieveGoal = new ArrayList<>();
                //不同环境,之后可以不用读文件
                List<File> fileList = TestGraph.getFileList(sourceList.get(i).getAbsolutePath());
//                System.out.println("开始测试");
                long start = System.currentTimeMillis();
                for (int k = 0; k < fileList.size(); k++) {
                    trainPath = fileList.get(k).getPath();
                    reader = new XMLReader(trainPath);

                    ArrayList<Literal> literals = reader.getLiterals();
                    ArrayList<GoalNode> tlgs = reader.getTlgs();

                    SynthEnvironment envir = new SynthEnvironment(literals, 0);
                    System.out.println(envir.onPrint());
                    System.out.println("--------------------------------------------------------");

                    ArrayList<Belief> beliefs = new ArrayList<>();
                    for (Literal l : literals) {
                        beliefs.add(new Belief(l.getName(), l.getState() ? 1 : 0));
                    }

                    //在当前环境加入treeAgent
                    AbstractAgent treeAgent = new MCTSAgent("MCTS-Agent", beliefs, tlgs);
                    envir.addAgent(treeAgent);

                    boolean running1 = true;
                    int step1 = 1;
                    while (running1) {
//                        System.out.println("---------------------step树 " + step1 + "------------------------------");
                        running1 = envir.run();
                        step1++;
                    }
                    // check the number of goals achieved
//                    System.out.println(treeAgent.getNumAchivedGoal());
                    thisAverageAchieveGoal.add(treeAgent.getNumAchivedGoal());
                }//50个文件
                long end = System.currentTimeMillis();
//                System.out.println(sourceList.get(i).getName() + "第" + m + "次程序运行时间" + (end - start));
                allTime.add(end - start);

                //计算每次里的50个文件的平均实现目标
                int thisAver = 0;
                for (int j = 0; j < thisAverageAchieveGoal.size(); j++) {
                    thisAver += thisAverageAchieveGoal.get(j);
                }
                double wushiGoal = thisAver / (double) thisAverageAchieveGoal.size();
//                System.out.println(sourceList.get(i).getName() + "第" + m + "次平均实现目标数量" + wushiGoal);
                allAchievedGoal.add(wushiGoal);
            }//次数

            //求该测试集下的平均时间
            long x = 0;
            for (int j = 0; j < allTime.size(); j++) {
                x += allTime.get(j);
            }
            double averageTime = x / (double) allTime.size();
            System.out.println(sourceList.get(i) + "平均运行时间为" + averageTime);

            //求该测试集下的平均目标数量
            double y = 0;
            for (int j = 0; j < allAchievedGoal.size(); j++) {
                y += allAchievedGoal.get(j);
            }
            double averageGoal = y / (double) allAchievedGoal.size();
            System.out.println(sourceList.get(i) + "平均实现目标数量为" + averageGoal);

            //记录下来
            actionPath.append(sourceList.get(i).getName());
            actionPath.append("\n");
            actionPath.append(String.valueOf(averageTime));
            actionPath.append("\n");
            actionPath.append(String.valueOf(averageGoal));
            actionPath.append("\n");
            actionPath.append("\n");
        }
        actionPath.close();
    }
}
