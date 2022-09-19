import agent.AbstractAgent;
import agent.Belief;
import agent.GraphAgent;
import agent.MCTSAgent;
import environment.SynthEnvironment;
import generators.ReadFile;
import goalplantree.GoalNode;
import goalplantree.Literal;
import structure.Graph;
import xml.WriteGraph;
import xml2bdi.XMLReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 原本图的路径
 * 文件夹路径
 *
 * 以上在main
 * mcts测试次数 config
 *
 */
public class GraphTree {
    //记录图
    public static Graph resultGraph;

    public static void main(String[] args) throws IOException {
        XMLReader reader;
        //训练生成图的路径，由文件里xml组成
        String trainPath;
        //原本树的路径
        String gptPath = "F:\\project\\gpt\\5.xml";
        //保存执行结果
        List<Integer> resultList = new ArrayList<>();
        int testNum;
        //不同环境,之后可以不用读文件
        List<File> fileList = TestGraph.getFileList("F:\\project\\gpt\\5\\zonggen");

        resultGraph = new Graph();

        try {
            testNum = Integer.parseInt(args[0]);
        } catch (Exception e) {
            testNum = 10;
        }

        for (int i = 0; i < fileList.size(); i++) {

            trainPath = fileList.get(i).getPath();
            resultGraph.setRunCurrentNode(resultGraph.getRoot());
            reader = new XMLReader(trainPath);

            //树和图都用同一个literals和tlgs
            // 环境
            ArrayList<Literal> literals = reader.getLiterals();
            // 顶级目标
            ArrayList<GoalNode> tlgs = reader.getTlgs();

            /**
             * start 图
             */
            if (resultGraph.getNodes().size() != 0) {
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
                    System.out.println("---------------------step " + step + "------------------------------");
                    running = environment.run();
                    step++;
                }

                System.out.println(graphAgent.getNumAchivedGoal());
                //图中环境已存在，不需进行处理，只把结果加入即可,并执行下一环境
                if (graphAgent.getNumAchivedGoal() == tlgs.size()) {
                    resultList.add(graphAgent.getNumAchivedGoal());
                    continue;
                }

            }

            /**
             * start 树
             */
            //到这，说明没有进if，即当前环境不在 图里，需要进行mcts跑树,合并图

            for (int j = 0; j < testNum; j++) {
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
                    System.out.println("---------------------step " + step1 + "------------------------------");
                    running1 = envir.run();
                    step1++;
                }
                // check the number of goals achieved
                System.out.println(treeAgent.getNumAchivedGoal());
                //如果某次实现全部目标，则加入到图中
                if (treeAgent.getNumAchivedGoal() == tlgs.size()){
                    resultList.add(treeAgent.getNumAchivedGoal());
                    //生成单条路径，合并到图里
                    Graph pathGraph = ReadFile.generatePathGraph(envir.getRecordActions(),gptPath);
                    resultGraph = ReadFile.mergeGraph(resultGraph,pathGraph);
                    break;
                }

            }

        }
        WriteGraph wxf = new WriteGraph();
        wxf.CreateXML(resultGraph,"F:\\project\\graph\\graph5.xml");
    }
}
