import agent.*;
import environment.SynthEnvironment;
import goalplantree.GoalNode;
import goalplantree.Literal;
import structure.Graph;
import xml.ReadGraph;
import xml.SummaryEnv;
import xml2bdi.XMLReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphTreeTest {
    //记录图
    public static Graph resultGraph;

    public static void main(String[] args) throws Exception {
        //写数据
//        File actionPath1 = new File("F:\\project\\SQ-MCTS\\data.txt");
        FileWriter actionPath = new FileWriter("data.txt", true);

        XMLReader reader;
        //测试时环境
        String trainPath;
        //原本树的路径
        String gptPath = "F:\\project\\gpt\\8.xml";
        //图的路径
        String graphPath = "F:\\project\\graph\\graph8_GPT_500.xml";

        //得到完全来自于环境中的变量名
        SummaryEnv summaryEnv = new SummaryEnv(gptPath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        resultGraph = new Graph();
        //得到图
        ReadGraph read = new ReadGraph(graphPath, gptPath);
        resultGraph = read.translate(graphPath);

        //获取总目录
        File file = new File("F:\\project\\gpt\\End8\\zongtest");
        List<File> sourceList = Arrays.stream(file.listFiles()).toList();

        //遍历每个测试集
        for (int i = 0; i < sourceList.size(); i++) {
            //每个测试集新建一个存储时间和目标的list
            System.out.println("当前测试的测试集为" + sourceList.get(i).getName());
            List<Long> allTime = new ArrayList<>();
            List<Double> allAchievedGoal = new ArrayList<>();

            for (int m = 0; m < 50; m++) {
                System.out.println(sourceList.get(i).getName() + "第" + m + "次");
                List<Integer> thisAverageAchieveGoal = new ArrayList<>();
                //不同环境,之后可以不用读文件
                List<File> fileList = TestGraph.getFileList(sourceList.get(i).getAbsolutePath());
                System.out.println("开始测试");
                long start = System.currentTimeMillis();
                for (int k = 0; k < fileList.size(); k++) {
                    trainPath = fileList.get(k).getPath();
                    reader = new XMLReader(trainPath);

                    //树和图都用同一个literals和tlgs
                    // 环境
                    ArrayList<Literal> literals = reader.getLiterals();
                    // 顶级目标
                    ArrayList<GoalNode> tlgs = reader.getTlgs();

                    //判断环境是否已存在
                    ArrayList<Integer> thisEnv = new ArrayList<>();
                    //1.保存当前环境下为真的完全来自于环境中的变量的名字
                    for (int j = 0; j < literals.size(); j++) {
                        if (absolutetEnv.contains(literals.get(j).getName()) && literals.get(j).getState() == true) {
                            thisEnv.add(j);
                        }
                    }

                    if (resultGraph.getEnvs().keySet().contains(thisEnv)) {
                        System.out.println("该环境已存在于图中");
                        System.out.println(fileList.get(i).getName());

                        resultGraph.setRunCurrentNode(resultGraph.getRoot());

                        // 构建环境
                        SynthEnvironment environment = new SynthEnvironment(literals, 0);
                        System.out.println(environment.onPrint());
                        System.out.println("--------------------------------------------------------");

                        // 构建智能体
                        ArrayList<Belief> bs = new ArrayList<>();
                        for (Literal l : literals) {
                            bs.add(new Belief(l.getName(), l.getState() ? 1 : 0));
                        }

                        AbstractAgent agent = new ClawGraphAgent("ClawGraph-Agent", bs, resultGraph, thisEnv);
                        environment.addAgent(agent);

                        boolean running = true;
                        int step = 1;
                        while (running) {
                            System.out.println("---------------------step图 " + step + "------------------------------");
                            running = environment.run();
                            step++;
                        }
                        System.out.println(resultGraph.getRunCurrentNode().getAchievedGoal().size());
                        thisAverageAchieveGoal.add(resultGraph.getRunCurrentNode().getAchievedGoal().size());
                    } else {
                        /**
                         * start 树
                         */
                        //到这，说明没有进if，即当前环境不在 图里，需要进行mcts跑树,合并图

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
                            System.out.println("---------------------step树 " + step1 + "------------------------------");
                            running1 = envir.run();
                            step1++;
                        }
                        // check the number of goals achieved
                        System.out.println(treeAgent.getNumAchivedGoal());
                        thisAverageAchieveGoal.add(treeAgent.getNumAchivedGoal());
                    }

                }//50个文件
                long end = System.currentTimeMillis();
                System.out.println(sourceList.get(i).getName() + "第" + m + "次程序运行时间" + (end - start));
                allTime.add(end - start);

                //计算每次里的50个文件的平均实现目标
                int thisAver = 0;
                for (int j = 0; j < thisAverageAchieveGoal.size(); j++) {
                    thisAver += thisAverageAchieveGoal.get(j);
                }
                double wushiGoal = thisAver / (double) thisAverageAchieveGoal.size();
                System.out.println(sourceList.get(i).getName() + "第" + m + "次平均实现目标数量" + wushiGoal);
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
        }
        actionPath.close();
    }


    public static ArrayList<ArrayList<Integer>> readEnvs(String fileName) throws IOException {
        //存储所有环境
        ArrayList<ArrayList<Integer>> readEnv = new ArrayList<>();
        //存储当前行环境
//        ArrayList<Integer> thisLine = new ArrayList<>();
        BufferedReader brTemp = new BufferedReader(new FileReader(fileName));
        String lineTemp = "";
        while ((lineTemp = brTemp.readLine()) != null) {
            //存储当前行环境
            ArrayList<Integer> thisLine = new ArrayList<>();
            String[] s = lineTemp.split(" ");
            for (String s1 : s) {
                thisLine.add(Integer.parseInt(s1));
            }
            readEnv.add(thisLine);
        }
        brTemp.close();
        return readEnv;
    }
}
