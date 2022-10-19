import agent.AbstractAgent;
import agent.Belief;
import agent.GraphAgent;
import agent.MCTSAgent;
import environment.SynthEnvironment;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.Literal;
import structure.Graph;
import structure.Node;
import xml.ReadGraph;
import xml.SummaryEnv;
import xml2bdi.XMLReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphTreeTest {
    //记录图
    public static Graph resultGraph;

    public static void main(String[] args) throws Exception {
        XMLReader reader;
        //测试时环境
        String trainPath;
        //原本树的路径
        String gptPath = "F:\\project\\gpt\\8.xml";
        //不同环境,之后可以不用读文件
        List<File> fileList = TestGraph.getFileList("F:\\project\\gpt\\8\\test4");
        //图的路径
        String graphPath = "F:\\project\\graph\\graph8.xml";

        //保存执行结果
        List<Integer> resultList = new ArrayList<>();
        //重复环境个数
        int num = 0;

        //得到完全来自于环境中的变量名
        SummaryEnv summaryEnv = new SummaryEnv(gptPath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        resultGraph = new Graph();
        //得到图
        ReadGraph read = new ReadGraph(graphPath, gptPath);
        resultGraph = read.translate(graphPath);

        System.out.println("开始测试");
        long start = System.currentTimeMillis();
        for (int i = 0; i < fileList.size(); i++) {
            trainPath = fileList.get(i).getPath();
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

            if (resultGraph.getEnvs().keySet().contains(thisEnv)) {
                /**
                 * start 图
                 */
                num++;
                System.out.println("该环境已存在于图中");
                System.out.println(fileList.get(i).getName());
                resultGraph.setRunCurrentNode(resultGraph.getRoot());
                Integer searchRouteId = resultGraph.getEnvs().get(thisEnv);
                for (Node node : resultGraph.getRoot().getChildNode()) {
                    if (node.getId() == searchRouteId){
                        //找到对应那条路径
                        ActionNode act = Node.getDifferentAction(resultGraph.getRunCurrentNode(), node);
                        System.out.println(act.getName());
                        resultGraph.setRunCurrentNode(node);
                        break;
                    }
                }

                //开始执行
                while (resultGraph.getRunCurrentNode().getChildNode().size() != 0){
                    Node node = resultGraph.getRunCurrentNode().getChildNode().get(0);
                    if (Node.getDifferentAction(resultGraph.getRunCurrentNode(),node) == null){
                        break;
                    }
                    ActionNode act = Node.getDifferentAction(resultGraph.getRunCurrentNode(), node);
                    System.out.println(act.getName());
                    resultGraph.setRunCurrentNode(node);
                }
                System.out.println("图执行成功");
                System.out.println(resultGraph.getRunCurrentNode().getAchievedGoal().size());
                resultList.add(resultGraph.getRunCurrentNode().getAchievedGoal().size());
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
            }

        }
        long end = System.currentTimeMillis();
        System.out.println("程序运行时间" + (end - start));

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
        System.out.println("重复环境数为" + num);
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
