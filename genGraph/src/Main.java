import agent.AbstractAgent;
import agent.Belief;
import agent.BeliefBaseImp;
import agent.GraphAgent;
import environment.SynthEnvironment;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.Literal;
import mcts.MCTSNode;
import simulation.Simulator;
import structure.Graph;
import structure.Node;
import xml.ReadGraph;
import xml2bdi.XMLReader;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        /**
         * 读生成的图
         */
        long start = System.currentTimeMillis();

        //读图的xml文件
        String gptPath = "F:\\project\\gpt\\2.xml";
        //图的路径
        String path ="F:\\project\\graph\\graph2.xml";
        ReadGraph read = new ReadGraph(path,gptPath);

        Graph readGraph = read.translate(path);
        readGraph.setRunCurrentNode(readGraph.getRoot());

        //测试生成的图对不对
//        readGraph.traversalId();
//        readGraph.traversalChildNode();

        /**
         * mcts跑图
         */
        double total = 0;

        XMLReader reader;
        reader = new XMLReader(gptPath);

        Simulator simulator = new Simulator();


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

//        GraphAgent graphAgent = new GraphAgent("Graph-Agent", bs, readGraph);
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
        System.out.println(agent.getNumAchivedGoal());
        total += agent.getNumAchivedGoal();

        long end = System.currentTimeMillis();
        System.out.println("程序运行时间" + (end - start));
    }
}
