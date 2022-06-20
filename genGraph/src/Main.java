import agent.AbstractAgent;
import agent.Belief;
import agent.GraphAgent;
import environment.SynthEnvironment;
import goalplantree.GoalNode;
import goalplantree.Literal;
import simulation.Simulator;
import structure.Graph;
import xml.ReadGraph;
import xml2bdi.XMLReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 跑生成的图
 * 1.树的xml路径
 * 2.图的xml路径（记得修改GraphAgent中的alpha，beta）
 *
 * 以上在main中修改（记得不需要写入txt文件）
 *
 * 测试个数在config中修改
 */

public class Main {
    static long startTime = System.currentTimeMillis();
    public static void main(String[] args) throws Exception {
//        startTime = System.currentTimeMillis();
//        long startAll = System.currentTimeMillis();
        /**
         * 读生成的图
         */
        //读树的xml文件
        String gptPath = "F:\\project\\gpt\\gen5_Graph0.3\\5.50.xml";
//        String gptPath = "F:\\project\\gpt\\5.xml";
        //图的路径
        String path ="F:\\project\\graph\\graph5_0.3.xml";
        ReadGraph read = new ReadGraph(path,gptPath);

        Graph readGraph = read.translate(path);

        /**
         * mcts跑图
         */
        double total = 0;
        int testNum;
        List<Integer> resultList = new ArrayList<>();

        try{
            testNum = Integer.parseInt(args[0]);
        }catch (Exception e){
            testNum = 10;
        }

        XMLReader reader;
        reader = new XMLReader(gptPath);

        Simulator simulator = new Simulator();


        for (int m = 0; m < testNum; m++) {
            startTime = System.currentTimeMillis();
            readGraph.setRunCurrentNode(readGraph.getRoot());
            // get the list of literals in the environment
            ArrayList<Literal> literals = reader.getLiterals();
            // get the list of goals
            ArrayList<GoalNode> tlgs = reader.getTlgs();

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

//        startTime = System.currentTimeMillis();
//        long startAll = System.currentTimeMillis();
            int step = 1;
            while (running) {
                System.out.println("---------------------step " + step + "------------------------------");
                running = environment.run();
                step++;
            }
            // check the number of goals achieved
            System.out.println("实现目标个数:"+agent.getNumAchivedGoal());
            resultList.add(agent.getNumAchivedGoal());
            total += agent.getNumAchivedGoal();
//        long end = System.currentTimeMillis();

//        long endAll = System.currentTimeMillis();
//        System.out.println("总的程序运行时间" + (endAll - startAll));

//        System.out.println("程序运行时间" + (end - startTime));
        }
        int x = 0;
        for (int i = 0; i < resultList.size(); i++) {
            x += resultList.get(i);
        }
        System.out.println(resultList);
        System.out.println("一共测试" + testNum + "次");
        double averageAchieveGoal = x / (double)resultList.size();
        System.out.println("平均实现目标数：" + averageAchieveGoal);
    }

    static boolean isTimeEnd(){
        return (startTime+27000 > System.currentTimeMillis());
    }
}

