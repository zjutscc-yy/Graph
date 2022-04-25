package generators;

import agent.MCTSAgent;
import structure.Graph;
import structure.Node;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args)throws IOException {
        //读取actions.txt
       ReadFile read = new ReadFile();
       Graph bigGraph = read.readFile("F:\\project\\SQ-MCTS\\actions1.txt");

       System.out.println(bigGraph.getNodes().size());


//        bigGraph.traversalId();
//        bigGraph.traversalChildNode();
//        for (Node node : bigGraph.getNodes()) {
//            System.out.println(node.getId());
//        }


        //把生成的图保存为xml文件
//       String path ="F:\\project\\graph\\graph2.xml";
//       XMLWriter wxf = new XMLWriter();
//       wxf.CreateXML(bigGraph,path);

    }
}

