import agent.MCTSAgent;
import goalplantree.GoalPlanTree;
import mcts.BasicMCTSNode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WriterNode {

    //把root转化成xml文件
    WriterNode(){

    }

    void CreateXML(BasicMCTSNode node,String path) {
        try
        {
            Element rootElement = new Element("MCTSNode");
            Document document = new Document(rootElement);

//            for (){
//                writeMCTSNode();
//            }

            XMLOutputter xmlOutputer = new XMLOutputter();
            xmlOutputer.setFormat(Format.getPrettyFormat());
            xmlOutputer.output(document, new FileWriter(path));
            System.out.println("XML File was created successfully!");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }


//    private void writeMCTSNode(BasicMCTSNode node){
//            Element var = new Element("MCTSNode");
//        for (GoalPlanTree gpt : BasicMCTSNode.gpts) {
//            String nodes = gpt.toString();
//            var.setAttribute("goal-plan trees of the initial state",nodes);
//            var.setAttribute();
//        }


//    }
}
