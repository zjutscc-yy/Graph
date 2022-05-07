import agent.Belief;
import agent.BeliefBaseImp;
import goalplantree.ActionNode;
import mcts.MCTSNode;
import structure.Graph;
import structure.Node;
import xml.XMLReader;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

//        long start = System.currentTimeMillis();


        //读图的xml文件
        String gptPath = "F:\\project\\gpt\\2.xml";
        //图的路径
        String path ="F:\\project\\graph\\graph2.xml";
        XMLReader reader = new XMLReader(path,gptPath);

        Graph readGraph = reader.translate(path);
        readGraph.traversalId();
        readGraph.traversalChildNode();

        //test
//        List<Belief> beliefs = new ArrayList<>();
//        for (int i = 0; i < 32; i++) {
//            String name_temp = "node_"+i+"_"+Math.random()*10;
//            double degree_temp = Math.random();
//            int degree_ = degree_temp >0.5?1:0;
//            Belief beliefTemp = new Belief(name_temp,degree_);
//            beliefs.add(beliefTemp);
//        }
//
//        readGraph.setRunCurrentNode(readGraph.getRoot());
//
//        BeliefBaseImp beliefBaseImp = new BeliefBaseImp((ArrayList<Belief>) beliefs);
//        MCTSNode mctsNode = new MCTSNode(readGraph,beliefBaseImp);
//
//        int i = 1;
//        mctsNode.run(100,10);
//        while (readGraph.getRunCurrentNode().getChildNode().size() != 0) {
//            ActionNode select = null;
//            System.out.println("***************step" + i++ + "**************");
//            for (ActionNode lead : mctsNode.bestActionNode()) {
//                System.out.println(lead.getName());
//                select = lead;
//            }
//
//
//            for (Node node : readGraph.getRunCurrentNode().getChildNode()) {
//                ActionNode act = Node.getDifferentAction(readGraph.getRunCurrentNode(),node);
//                if (act.getName().equals(select.getName())){
//                    readGraph.setRunCurrentNode(node);
//                    mctsNode = new MCTSNode(readGraph,beliefBaseImp);
//                    mctsNode.run(100,10);
//                }
//            }
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("程序运行时间" + (end - start));


    }
}
