package generators;

import agent.Belief;
import agent.BeliefBaseImp;
import agent.MCTSAgent;
import goalplantree.ActionNode;
import mcts.MCTSNode;
import structure.Graph;
import structure.Node;
import xml.WriteGraph;
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
       ReadFile read = new ReadFile();
       Graph bigGraph = read.readFile("F:\\project\\SQ-MCTS\\actions5.txt");

       System.out.println(bigGraph.getNodes().size());

       bigGraph.traversalId();
       bigGraph.traversalChildNode();

       //把生成的图保存为xml文件
         String path ="F:\\project\\graph\\graph5.xml";
       WriteGraph wxf = new WriteGraph();
       wxf.CreateXML(bigGraph,path);
//       XMLWriter wxf = new XMLWriter();
//       wxf.CreateXML(bigGraph,path);
    }
}

