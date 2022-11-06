import agent.*;
import environment.SynthEnvironment;
import generators.ReadFile;
import goalplantree.GoalNode;
import goalplantree.Literal;
import structure.Graph;
import structure.Node;
import xml.ReadGraph;
import xml.SummaryEnv;
import xml.WriteGraph;
import xml2bdi.XMLReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class test {
    //记录txt文件生成的图
    public static Graph txtGraph;
    //记录ArrayList方法生成的图
    public static Graph recordGraph;

    public static void main(String[] args) throws Exception {
        XMLReader reader;
        //用来暂时保存训练文件夹下不同文件
        String trainPath;
        //原本的树的路径
        String gptPath = "F:\\project\\gpt\\2.xml";
        //每个环境测试次数
        int testNum;
        //可以执行成功的新环境要复制到哪
        String newFilePath = "F:\\project\\gpt\\2\\envs";
        //要训练的环境所在文件夹
        List<File> fileList = TestGraph.getFileList("F:\\project\\gpt\\2\\envs");
        //图的路径
        String graphPath = "F:\\project\\graph\\graph_test.xml";

        //存放结果，方便判断是不是加入图的每个环境都为可执行成功的环境
        List<Integer> resultList = new ArrayList<>();

        testNum = 5;

        //得到完全来自于环境中的变量名
        SummaryEnv summaryEnv = new SummaryEnv(gptPath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        recordGraph = new Graph();

        ReadGraph read = new ReadGraph(graphPath, gptPath);
        recordGraph = read.translate(graphPath);
        System.out.println("读图成功");

        //遍历改变环境后的文件夹里的所有xml文件
        for (int i = 0; i < 2; i++) {
            trainPath = fileList.get(i).getPath();
            recordGraph.setRunCurrentNode(recordGraph.getRoot());

            reader = new XMLReader(trainPath);

            //树和图都用同一个literals和tlgs
            // 环境
            ArrayList<Literal> literals = reader.getLiterals();
            // 顶级目标
            ArrayList<GoalNode> tlgs = reader.getTlgs();

            //保存下来当前环境
            ArrayList<Integer> thisEnv = new ArrayList<>();
            //1.保存当前环境下为真的完全来自于环境中的变量的名字
            for (int j = 0; j < literals.size(); j++) {
                if (absolutetEnv.contains(literals.get(j).getName()) && literals.get(j).getState() == true){
                    thisEnv.add(j);
                }
            }

            if (recordGraph.getEnvs().keySet().contains(thisEnv)){
                /**
                 * 图
                 */
                System.out.println("该环境已存在于图中");
                recordGraph.setRunCurrentNode(recordGraph.getRoot());
                Integer searchRouteId = recordGraph.getEnvs().get(thisEnv);
                for (Node node : recordGraph.getRoot().getChildNode()) {
                    if (node.getId() == searchRouteId){
                        //找到对应那条路径
                        System.out.println(Node.getDifferentAction(recordGraph.getRunCurrentNode(),node).getName());
                        recordGraph.setRunCurrentNode(node);
                    }
                }

                //开始执行
                while (recordGraph.getRunCurrentNode().getChildNode().size() != 0){
                    Node node = recordGraph.getRunCurrentNode().getChildNode().get(0);
                    if (Node.getDifferentAction(recordGraph.getRunCurrentNode(),node) == null){
                        break;
                    }
                    System.out.println(Node.getDifferentAction(recordGraph.getRunCurrentNode(),node).getName());
                    recordGraph.setRunCurrentNode(node);
                }
                System.out.println("图执行成功");
                resultList.add(recordGraph.getRunCurrentNode().getAchievedGoal().size());
            }else {
                /**
                 * 树
                 */
                for (int m = 0; m < testNum; m++) {
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
                    AbstractAgent agent = new MCTSAgent("MCTS-Agent", bs, tlgs);

                    // add this agent to the environment
                    environment.addAgent(agent);

                    boolean running = true;
                    int step1 = 1;
                    while (running) {
                        System.out.println("---------------------step树 " + step1 + "------------------------------");
                        running = environment.run();
                        step1++;
                    }
                    // check the number of goals achieved
                    System.out.println(agent.getNumAchivedGoal());

                    //如果在5次测试某环境中，有一次能实现全部目标，则生成单条路径，并与之前图合并
                    if (agent.getNumAchivedGoal() == tlgs.size()) {
                        //把当前环境加入到envs文件夹中
                        LearningProcess.copyFile(fileList.get(i).getAbsolutePath(),newFilePath+"\\"+fileList.get(i).getName());

                        //生成单条路径，合并到图里
                        Graph pathGraph = ReadFile.generatePathGraph(environment.getRecordActions(), gptPath, thisEnv);

                        //合并爪形图
                        recordGraph = ReadFile.mergeClawGraph(recordGraph, pathGraph);
                        resultList.add(agent.getNumAchivedGoal());

                        FileWriter actionPath = new FileWriter("F:\\project\\SQ-MCTS\\actions_test.txt",true);
                        actionPath.append("//");
                        actionPath.append("\n");
                        actionPath.append("\n");
                        actionPath.close();
                        break;
                    }else {
                        reWriteFileEnd("F:\\project\\SQ-MCTS\\actions_test.txt");
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
        wxf.CreateXML(recordGraph,"F:\\project\\graph\\graph_test01.xml");
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
