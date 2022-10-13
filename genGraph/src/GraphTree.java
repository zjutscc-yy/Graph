import agent.AbstractAgent;
import agent.Belief;
import agent.GraphAgent;
import agent.MCTSAgent;
import environment.SynthEnvironment;
import generators.ReadFile;
import goalplantree.GoalNode;
import goalplantree.Literal;
import structure.Graph;
import xml.SelectEnv;
import xml.SummaryEnv;
import xml.WriteGraph;
import xml2bdi.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 原本树的路径
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
    //记录环境
    public static ArrayList<ArrayList<Integer>> envs;
    static ArrayList<String> unExeXML = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        XMLReader reader;
        //训练生成图的路径，由文件里xml组成
        String trainPath;
        //原本树的路径
        String gptPath = "F:\\project\\gpt\\8.xml";
        int testNum;
        String newFilePath = "F:\\project\\gpt\\8\\envs";
        //不同环境,之后可以不用读文件
        List<File> fileList = TestGraph.getFileList("F:\\project\\gpt\\8\\8gen");

        //得到完全来自于环境中的变量名
        SummaryEnv summaryEnv = new SummaryEnv(gptPath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        resultGraph = new Graph();
        envs = new ArrayList<>();

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

            //判断环境是否已存在
            ArrayList<Integer> thisEnv = new ArrayList<>();
            //1.保存当前环境下为真的完全来自于环境中的变量的名字
            for (int j = 0; j < literals.size(); j++) {
                if (absolutetEnv.contains(literals.get(j).getName()) && literals.get(j).getState() == true){
                    thisEnv.add(j);
                }
            }
//            for (Literal literal : literals) {
//                if (absolutetEnv.contains(literal.getName()) && literal.getState() == true){
//                    thisEnv.add(literal.getName());
//                }
//            }

            //再判断环境是否已存在
            //包含此环境，跑图
            if (envs.contains(thisEnv)){
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
                //计算程序运行时间
//                long start = System.currentTimeMillis();
                while (running) {
                    System.out.println("---------------------step图 " + step + "------------------------------");
                    running = environment.run();
                    step++;
                }
//                long end = System.currentTimeMillis();
//                System.out.println("程序运行时间" + (end - start));
                System.out.println(graphAgent.getNumAchivedGoal());
                //图中环境已存在，不需进行处理，只把结果加入即可,并执行下一环境
//                if (graphAgent.getNumAchivedGoal() == tlgs.size()) {
//                    resultList.add(graphAgent.getNumAchivedGoal());
//                    continue;
//                }
            }else {
                /**
                 * start 树
                 */
                //到这，说明没有进if，即当前环境不在 图里，需要进行mcts跑树,合并图

                //记录5次结果
                ArrayList<Integer> n5 = new ArrayList<>();
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
                        System.out.println("---------------------step树 " + step1 + "------------------------------");
                        running1 = envir.run();
                        step1++;
                    }
                    // check the number of goals achieved
                    System.out.println(treeAgent.getNumAchivedGoal());
                    n5.add(treeAgent.getNumAchivedGoal());
                    //如果某次实现全部目标，则加入到图中
                    if (treeAgent.getNumAchivedGoal() == tlgs.size()) {
                        //把当前环境加入到envs文件夹中
                        copyFile(fileList.get(i).getAbsolutePath(),newFilePath+"\\"+fileList.get(i).getName());
                        //把当前环境加入到总的环境中
                        envs.add(thisEnv);
                        //生成单条路径，合并到图里
                        Graph pathGraph = ReadFile.generatePathGraph(envir.getRecordActions(), gptPath);
                        resultGraph = ReadFile.mergeGraph(resultGraph, pathGraph);
                        break;
                    }


                }
                if (!n5.contains(tlgs.size())) {
                    unExeXML.add(fileList.get(i).getName());
                }
            }

        }

        WriteGraph wxf = new WriteGraph();
        wxf.CreateXML(resultGraph,"F:\\project\\graph\\graph8.xml");
        System.out.println(unExeXML);
        System.out.println(unExeXML.size());

        File actionPath1 = new File("F:\\project\\SQ-MCTS\\envs8.txt");
        FileWriter envPath  = new FileWriter("envs8.txt",true);

        for (ArrayList<Integer> integers : envs) {
            for (int i = 0; i < integers.size(); i++) {
                envPath.append(integers.get(i) + " ");
            }
            envPath.append("\n");
        }

        envPath.close();
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
