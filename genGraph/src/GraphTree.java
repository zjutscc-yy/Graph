import agent.AbstractAgent;
import agent.Belief;
import agent.GraphAgent;
import agent.MCTSAgent;
import environment.SynthEnvironment;
import generators.ReadFile;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.Literal;
import structure.Graph;
import structure.Node;
import xml.ReadGraph;
import xml.SelectEnv;
import xml.SummaryEnv;
import xml.WriteGraph;
import xml2bdi.XMLReader;

import java.io.*;
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

    public static void main(String[] args) throws Exception {
        XMLReader reader;
        //用来暂时保存训练文件夹下不同文件
        String trainPath;
        //原本树的路径
        String gptPath = "F:\\project\\gpt\\8.xml";
        //每个环境测试次数
        int testNum;
        //可以执行成功的新环境要复制到哪
        String newFilePath = "F:\\project\\gpt\\8\\envs";
        //要训练的环境所在文件夹
        List<File> fileList = TestGraph.getFileList("F:\\project\\gpt\\8\\8gen");
        //图的路径
//        String graphPath = "F:\\project\\graph\\graph8.xml";

        //存放结果，方便判断是不是加入图的每个环境都为可执行成功的环境
        List<Integer> resultList = new ArrayList<>();

        //得到完全来自于环境中的变量名
        SummaryEnv summaryEnv = new SummaryEnv(gptPath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        resultGraph = new Graph();
//        ReadGraph read = new ReadGraph(graphPath, gptPath);
//        resultGraph = read.translate(graphPath);
        System.out.println("读图成功");

        try {
            testNum = Integer.parseInt(args[0]);
        } catch (Exception e) {
            testNum = 10;
        }

        System.out.println("开始学习环境");
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
                if (absolutetEnv.contains(literals.get(j).getName()) && literals.get(j).getState() == true){
                    thisEnv.add(j);
                }
            }

            //再判断环境是否已存在
            //包含此环境，跑图
            if (resultGraph.getEnvs().keySet().contains(thisEnv)){
                /**
                 * start 图
                 */
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
                resultList.add(resultGraph.getRunCurrentNode().getAchievedGoal().size());
            }else {
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
                        System.out.println("---------------------step树 " + step1 + "------------------------------");
                        running1 = envir.run();
                        step1++;
                    }
                    // check the number of goals achieved
                    System.out.println(treeAgent.getNumAchivedGoal());
                    //如果某次实现全部目标，则加入到图中
                    if (treeAgent.getNumAchivedGoal() == tlgs.size()) {
                        //把当前环境加入到envs文件夹中
                        copyFile(fileList.get(i).getAbsolutePath(),newFilePath+"\\"+fileList.get(i).getName());

                        //生成单条路径，合并到图里
                        Graph pathGraph = ReadFile.generatePathGraph(envir.getRecordActions(), gptPath, thisEnv);

                        //合并爪形图
                        resultGraph = ReadFile.mergeClawGraph(resultGraph, pathGraph);
                        resultList.add(treeAgent.getNumAchivedGoal());

                        FileWriter actionPath = new FileWriter("F:\\project\\SQ-MCTS\\actions_8.txt",true);
                        actionPath.append("//");
                        actionPath.append("\n");
                        actionPath.append("\n");
                        actionPath.close();
                        break;
                    }else {
                        reWriteFileEnd("F:\\project\\SQ-MCTS\\actions_8.txt");
                    }

                }
            }

        }

        int x = 0;
        for (int i = 0; i < resultList.size(); i++) {
            x += resultList.get(i);
        }
        System.out.println(resultList);
        System.out.println("一共有" + resultList.size() + "个可执行环境");
        double averageAchieveGoal = x / (double) resultList.size();
        System.out.println("平均实现目标数：" + averageAchieveGoal);

        WriteGraph wxf = new WriteGraph();
        wxf.CreateXML(resultGraph,"F:\\project\\graph\\graph8.xml");
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

    static void reWriteFileEnd(String sourceFilePath){
        List<String> filecon = new ArrayList<>();
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
}
