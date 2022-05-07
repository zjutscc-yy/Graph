package xml;

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
import java.util.Set;


public class XMLWriter {

    public XMLWriter(){

    }

    public void CreateXML(Graph bigGraph, String path){
        try
        {
            Element graph = new Element("Graph");
            Document document = new Document(graph);

            //图的根节点
            Element rootElement = new Element("Root");
            Node root = bigGraph.getRoot();
            rootElement.setAttribute(new Attribute("Id",String.valueOf(root.getId())));
            graph.addContent(rootElement);

            //普通节点
            Element nodesElement = new Element("Nodes");

            for (Node bigGraphNode : bigGraph.getNodes()) {
                writeNode(bigGraphNode, nodesElement);
            }
            //把nodes加到图里
            graph.addContent(nodesElement);

            //节点之间的关系
            Element nodeRelation = new Element("Node_Relation");
            for (Node bigGraphNode : bigGraph.getNodes()) {
                writeNodeId(bigGraphNode,nodeRelation);
            }
            graph.addContent(nodeRelation);

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
        if (treeNode != null) {
            stepElement.setAttribute("curStep", treeNode.getName());
        }else {
            stepElement.setAttribute("curStep", "null");
        }
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

