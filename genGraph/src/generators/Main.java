package generators;

import agent.Belief;
import agent.BeliefBaseImp;
import agent.MCTSAgent;
import goalplantree.ActionNode;
import mcts.MCTSNode;
import structure.Graph;
import structure.Node;
import xml.XMLReader;
import xml.XMLWriter;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception {
//        读取actions.txt
//       ReadFile read = new ReadFile();
//       Graph bigGraph = read.readFile("F:\\project\\SQ-MCTS\\actions1.txt");
//
//       System.out.println(bigGraph.getNodes().size());


//        bigGraph.traversalId();
//        bigGraph.traversalChildNode();

//        for (Node node : bigGraph.getNodes()) {
//            for (Node node1 : node.getChildNode()) {
//                ActionNode act = Node.getDifferentAction(node,node1);
//                System.out.println(act.getName());
//            }
//        }


        //把生成的图保存为xml文件
       String path ="F:\\project\\graph\\graph2.xml";
//       XMLWriter wxf = new XMLWriter();
//       wxf.CreateXML(bigGraph,path);

       //读图的xml文件
        String gptPath = "F:\\project\\gpt\\2.xml";
        XMLReader reader = new XMLReader(path,gptPath);

        Graph readGraph = reader.translate(path);


//        readGraph.traversalId();
//        readGraph.traversalChildNode();

        //test
        List<Belief> beliefs = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            String name_temp = "node_"+i+"_"+Math.random()*10;
            double degree_temp = Math.random();
            int degree_ = degree_temp >0.5?1:0;
            Belief beliefTemp = new Belief(name_temp,degree_);
            beliefs.add(beliefTemp);
        }

        readGraph.setRunCurrentNode(readGraph.getRoot());

        BeliefBaseImp beliefBaseImp = new BeliefBaseImp((ArrayList<Belief>) beliefs);
        MCTSNode mctsNode = new MCTSNode(readGraph,beliefBaseImp);

        int i = 1;
        mctsNode.run(10,5);
        while (readGraph.getRunCurrentNode().getChildNode().size() != 0) {
            ActionNode select = null;
            System.out.println("***************step" + i++ + "**************");
            for (ActionNode lead : mctsNode.bestActionNode()) {
                System.out.println(lead.getName());
                select = lead;
            }


            for (Node node : readGraph.getRunCurrentNode().getChildNode()) {
                ActionNode act = Node.getDifferentAction(readGraph.getRunCurrentNode(),node);
                if (act.getName().equals(select.getName())){
                    readGraph.setRunCurrentNode(node);
                    mctsNode = new MCTSNode(readGraph,beliefBaseImp);
                    mctsNode.run(10,5);
                }
            }
        }


    }
}

