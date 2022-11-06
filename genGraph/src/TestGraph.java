import agent.AbstractAgent;
import agent.Belief;
import agent.GraphAgent;
import environment.SynthEnvironment;
import goalplantree.Literal;
import simulation.Simulator;
import structure.Graph;
import xml.ReadGraph;
import xml2bdi.XMLReader;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对完全陌生的环境进行跑图测试
 *
 * 需要的参数为
 * 1.测试环境所在文件夹路径（遍历）  main
 * 2.图的路径 main
 * 3.原本树的路径 main
 *
 * 记得不需写入txt文件
 */

public class TestGraph {
    public static void main(String[] args) throws Exception {
        List<File> fileList = getFileList("F:\\project\\gpt\\8\\test1");
        //图的路径
        String graphPath = "F:\\project\\graph\\graph8_txt.xml";
        //原本树的路径
        String gptPath = "F:\\project\\gpt\\8.xml";
        double total = 0;
        List<Integer> resultList = new ArrayList<>();


        XMLReader reader;
        String testGptPath;

        //得到图
        ReadGraph read = new ReadGraph(graphPath,gptPath);
        Graph readGraph = read.translate(graphPath);

        for (int j = 0; j < fileList.size(); j++) {

            testGptPath = fileList.get(j).getPath();
            readGraph.setRunCurrentNode(readGraph.getRoot());

            Simulator simulator = new Simulator();

            reader = new XMLReader(testGptPath);

            // get the list of literals in the environment
            ArrayList<Literal> literals = reader.getLiterals();

            // build the environment
            SynthEnvironment environment = new SynthEnvironment(literals, 0);
            System.out.println(environment.onPrint());
            System.out.println("--------------------------------------------------------");

            // build the agent
            ArrayList<Belief> bs = new ArrayList<>();
            for (Literal l : literals) {
                bs.add(new Belief(l.getName(), l.getState() ? 1 : 0));
            }

            AbstractAgent agent = new GraphAgent("Graph-Agent", bs, readGraph);

            environment.addAgent(agent);

            boolean running = true;
            int step = 1;
            while (running) {
                System.out.println("---------------------step " + step + "------------------------------");
                running = environment.run();
                step++;
            }
            // check the number of goals achieved
            System.out.println("实现目标个数:" + agent.getNumAchivedGoal());
            resultList.add(agent.getNumAchivedGoal());
            total += agent.getNumAchivedGoal();

//            FileWriter actionPath  = new FileWriter("actions_test.txt",true);

//            actionPath.append("//");
//            actionPath.append("\n");
//            actionPath.append("\n");
//            actionPath.close();
        }
        int x = 0;
        for (int i = 0; i < resultList.size(); i++) {
            x += resultList.get(i);
        }
        System.out.println(resultList);
        System.out.println("一共测试了" + resultList.size() + "次");
        double averageAchieveGoal = x / (double)resultList.size();
        System.out.println("平均实现目标数：" + averageAchieveGoal);
    }

    /**
     * 获取文件夹下的文件列表
     *
     * @param dirStr 文件夹的路径
     * @return
     */
    public static List<File> getFileList(String dirStr) {
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
