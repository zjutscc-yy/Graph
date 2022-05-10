package generators;

import structure.Graph;
import xml.WriteGraph;

public class Main {
    public static void main(String[] args) throws Exception {
//        读取actions.txt
       ReadFile read = new ReadFile();
       Graph bigGraph = read.readFile("F:\\project\\SQ-MCTS\\actions1.txt");

       System.out.println(bigGraph.getNodes().size());

       bigGraph.traversalId();
       bigGraph.traversalChildNode();

       //把生成的图保存为xml文件
         String path ="F:\\project\\graph\\graph1.xml";
       WriteGraph wxf = new WriteGraph();
       wxf.CreateXML(bigGraph,path);
//       XMLWriter wxf = new XMLWriter();
//       wxf.CreateXML(bigGraph,path);
    }
}

