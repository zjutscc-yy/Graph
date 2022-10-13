import agent.AbstractAgent;
import agent.Belief;
import agent.GraphAgent;
import agent.MCTSAgent;
import environment.SynthEnvironment;
import goalplantree.GoalNode;
import goalplantree.Literal;
import structure.Graph;
import xml.ReadGraph;
import xml.SummaryEnv;
import xml2bdi.XMLReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphTreeTest {

    //记录图
    public static Graph resultGraph;
    //记录环境
    public static ArrayList<ArrayList<Integer>> envs;

    public static void main(String[] args) throws Exception {
        XMLReader reader;
        //训练生成图的路径，由文件里xml组成
        String trainPath;
        //原本树的路径
        String gptPath = "F:\\project\\gpt\\8.xml";
        //保存执行结果
        List<Integer> resultList = new ArrayList<>();
        //不同环境,之后可以不用读文件
        List<File> fileList = TestGraph.getFileList("F:\\project\\gpt\\8\\test1");

        //得到完全来自于环境中的变量名
        SummaryEnv summaryEnv = new SummaryEnv(gptPath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        //得到环境
        envs = readEnvs("F:\\project\\SQ-MCTS\\envs8.txt");
        //得到图
        ReadGraph read = new ReadGraph("F:\\project\\graph\\graph8_txt.xml", gptPath);
        Graph readGraph = read.translate("F:\\project\\graph\\graph8_txt.xml");
        resultGraph = readGraph;

        for (int i = 0; i < fileList.size(); i++) {
            trainPath = fileList.get(i).getPath();
            resultGraph.setRunCurrentNode(resultGraph.getRoot());
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

            //再判断环境是否已存在
            //包含此环境，跑图

            if (envs.contains(thisEnv)) {
                /**
                 * start 图
                 */
                // 构建环境
                SynthEnvironment environment = new SynthEnvironment(literals, 0);
                System.out.println(environment.onPrint());
                System.out.println("--------------------------------------------------------");

                // 构建智能体的belief
                ArrayList<Belief> bs = new ArrayList<>();
                for (Literal l : literals) {
                    bs.add(new Belief(l.getName(), l.getState() ? 1 : 0));
                }

                //先跑图
                AbstractAgent graphAgent = new GraphAgent("Graph-Agent", bs, resultGraph);
                environment.addAgent(graphAgent);

                boolean running = true;
                int step = 1;
                while (running) {
                    System.out.println("---------------------step图 " + step + "------------------------------");
                    running = environment.run();
                    step++;
                }
                System.out.println(graphAgent.getNumAchivedGoal());
                resultList.add(graphAgent.getNumAchivedGoal());
                //图的搜索结果有时候不一定为全部目标个数
//                if (graphAgent.getNumAchivedGoal() == tlgs.size()){
//                    resultList.add(graphAgent.getNumAchivedGoal());
//                }else {
//                    SynthEnvironment envir = new SynthEnvironment(literals, 0);
//                    System.out.println(envir.onPrint());
//                    System.out.println("--------------------------------------------------------");
//
//                    ArrayList<Belief> beliefs = new ArrayList<>();
//                    for (Literal l : literals) {
//                        beliefs.add(new Belief(l.getName(), l.getState() ? 1 : 0));
//                    }
//
//                    //在当前环境加入treeAgent
//                    AbstractAgent treeAgent = new MCTSAgent("MCTS-Agent", beliefs, tlgs);
//                    envir.addAgent(treeAgent);
//
//                    boolean running1 = true;
//                    int step1 = 1;
//                    while (running1) {
//                        System.out.println("---------------------step树 图不为tlg " + step1 + "------------------------------");
//                        running1 = envir.run();
//                        step1++;
//                    }
//                    // check the number of goals achieved
//                    System.out.println(treeAgent.getNumAchivedGoal());
//                    resultList.add(treeAgent.getNumAchivedGoal());
//                }
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
                resultList.add(treeAgent.getNumAchivedGoal());
                //如果某次实现全部目标，则加入到图中
//                if (treeAgent.getNumAchivedGoal() == tlgs.size()) {
//                    //把当前环境加入到总的环境中
//                    envs.add(thisEnv);
//                    resultList.add(treeAgent.getNumAchivedGoal());
//                    //生成单条路径，合并到图里
//                    Graph pathGraph = ReadFile.generatePathGraph(envir.getRecordActions(), gptPath);
//                    resultGraph = ReadFile.mergeGraph(resultGraph, pathGraph);
//                    break;
//                }


            }

        }

//        WriteGraph wxf = new WriteGraph();
//        wxf.CreateXML(resultGraph, "F:\\project\\graph\\graphTest1.xml");

        int x = 0;
        for (int i = 0; i < resultList.size(); i++) {
            x += resultList.get(i);
        }
        System.out.println(resultList);
        System.out.println("一共测试了" + resultList.size() + "个环境");
        double averageAchieveGoal = x / (double) resultList.size();
        System.out.println("平均实现目标数：" + averageAchieveGoal);
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
