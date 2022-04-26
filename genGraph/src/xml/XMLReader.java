package xml;

import goalplantree.GoalNode;
import goalplantree.TreeNode;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import structure.Graph;
import structure.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class XMLReader {

    /**
     * 所有节点
     */
    private Graph bigGraph;
    xml2bdi.XMLReader reader;



    public XMLReader(String url,String gptFilePath){
        try{
            reader = new xml2bdi.XMLReader(gptFilePath);
            translate(url);
        }catch (Exception e){
            System.out.println("Read XML file error! " + " url: " + url);
        }
    }

    public Graph translate(String url) throws Exception{

        bigGraph = new Graph();
        SAXBuilder builder = new SAXBuilder();
        Document read_doc = builder.build(new File(url));
        Element graph = read_doc.getRootElement();

        //获得graph里的所有信息
        List<Element> graphInfo = graph.getChildren();

        //获得nodes标签里的所有信息，即所有nodes
        Element nodesElement = graphInfo.get(0);
        List<Element> node = nodesElement.getChildren();

        for (int i = 0; i < node.size(); i++){
            bigGraph.addNode(readNode(node.get(i)));
        }

        //获取Node_Relation标签里的所有信息
        Element Relation = graphInfo.get(1);
        List<Element> nodeRelation = Relation.getChildren();
        //每条node_id
        for (int i = 0; i < nodeRelation.size(); i++){
            readChild(nodeRelation.get(i),bigGraph);
        }

        return bigGraph;
    }

    private Node readNode(Element element){
        int id = Integer.parseInt(element.getAttributeValue("Id"));
        //获得currentStep标签
        Element currentStepsElement = element.getChildren().get(0);
        //获得currentStep标签下的所有step标签
        List<Element> steps = currentStepsElement.getChildren();

        HashMap<GoalNode, TreeNode> curSteps = new HashMap();
        for (int i = 0; i < steps.size(); i++){
            curSteps = readSteps(steps.get(i),curSteps);
        }

        //创建一个新的node
        Node graphNode = new Node(id,curSteps);
        return graphNode;
    }


    private HashMap<GoalNode, TreeNode> readSteps(Element element,HashMap map){

        for (GoalNode tlg : reader.getTlgs()) {
            if (tlg.getName().equals(element.getAttributeValue("Tlg_name"))){
                TreeNode actionNode = Node.traversal(tlg,element.getAttributeValue("curStep"));
                map.put(tlg,actionNode);
            }
        }

        return map;
    }

    private void readChild(Element element,Graph graph){
        int parentId = Integer.parseInt(element.getAttributeValue("Id"));

        List<Element> childNode = element.getChildren();

        ArrayList<Node> childNodes = new ArrayList<>();
        //每条childNode
        for (int i = 0; i < childNode.size(); i++){
            int childId = Integer.parseInt(childNode.get(i).getAttributeValue("Id"));
            for (Node node : graph.getNodes()) {
                if (node.getId() == childId){
                    childNodes.add(node);
                }
            }

        }

        //找到父节点
        for (Node node : graph.getNodes()) {
            if (node.getId() == parentId){
                for (Node childNode1 : childNodes) {
                    node.addChildNode(childNode1);
                }
            }
        }

    }
}
