package generators;

import agent.MCTSAgent;
import structure.Graph;
import structure.Node;
import xml.XMLReader;
import xml.XMLWriter;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        //读取actions.txt
//       ReadFile read = new ReadFile();
//       Graph bigGraph = read.readFile("F:\\project\\SQ-MCTS\\actions1.txt");
//
//       System.out.println(bigGraph.getNodes().size());


//        bigGraph.traversalId();
//        bigGraph.traversalChildNode();
//        for (Node node : bigGraph.getNodes()) {
//            System.out.println(node.getId());
//        }


        //把生成的图保存为xml文件
       String path ="F:\\project\\graph\\graph1.xml";
//       XMLWriter wxf = new XMLWriter();
//       wxf.CreateXML(bigGraph,path);

       //读图的xml文件
        String gptPath = "F:\\project\\gpt\\2.xml";
        XMLReader reader = new XMLReader(path,gptPath);

        Graph readGraph = reader.translate("F:\\project\\graph\\graph1.xml");
        readGraph.traversalId();
        readGraph.traversalChildNode();


    }
}

