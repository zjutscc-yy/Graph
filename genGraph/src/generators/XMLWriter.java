package generators;

import goalplantree.GoalNode;
import goalplantree.TreeNode;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import structure.Graph;
import structure.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class XMLWriter {

    XMLWriter(){

    }

    void CreateXML(Graph bigGraph,String path){
        try
        {
            Element graph = new Element("Graph");
            Document document = new Document(graph);

            for (Node bigGraphNode : bigGraph.getNodes()) {
                writeNode(bigGraphNode,graph);
            }

            for (Node bigGraphNode : bigGraph.getNodes()) {
                writeNodeId(bigGraphNode,graph);
            }

            XMLOutputter xmlOutputer = new XMLOutputter();
            xmlOutputer.setFormat(Format.getPrettyFormat());
            xmlOutputer.output(document, new FileWriter(path));
            System.out.println("XML File was created successfully!");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }

    private void writeNode(Node node,Element parent) {
        Element nodeElement = new Element("Node");
        nodeElement.setAttribute(new Attribute("Id",String.valueOf(node.getId())));

        //node的子标签
        writeCurSteps(node,nodeElement);

        //把node加到图里
        parent.addContent(nodeElement);
    }

    private void writeCurSteps(Node node,Element parent){
        Element currentStepsElement = new Element("currentSteps");

        //currentSteps的子标签
        HashMap<GoalNode, TreeNode> stepMap = node.getCurrentStep();
        for (Map.Entry<GoalNode, TreeNode> goalNodeTreeNodeEntry :stepMap.entrySet()) {
            GoalNode tlg = goalNodeTreeNodeEntry.getKey();
            TreeNode curStep = goalNodeTreeNodeEntry.getValue();
            writeStep(tlg,curStep,currentStepsElement);
        }

        //把currentStep作为子标签加到node里
        parent.addContent(currentStepsElement);
    }

    private void writeStep(GoalNode goalNode, TreeNode treeNode,Element parent){

        Element stepElement = new Element("Step");

        stepElement.setAttribute("Tlg_name", goalNode.getName());
        stepElement.setAttribute("curStep", treeNode.getName());
        parent.addContent(stepElement);
    }

    private void writeChildId(Node childNode,Element parent){
        Element childNodeElement = new Element("childNode_Id");

        childNodeElement.setAttribute("Id",String.valueOf(childNode.getId()));

        parent.addContent(childNodeElement);
    }

    private void writeNodeId(Node node,Element parent){
        Element nodeIdElement = new Element("Node_Id");
        nodeIdElement.setAttribute("Id",String.valueOf(node.getId()));

        for (Node childNode : node.getChildNode()) {
            writeChildId(childNode,nodeIdElement);
        }

        parent.addContent(nodeIdElement);
    }
}
