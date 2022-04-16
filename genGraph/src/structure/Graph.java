package structure;

import goalplantree.GoalNode;
import goalplantree.TreeNode;
import xml2bdi.XMLReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Graph {

    public ArrayList<GoalNode> initialState;

    //图的根节点
    private Node root;

    //图中存储哪些Node
    private ArrayList<Node> nodes = new ArrayList<>();

    //表示当前指向哪个节点
    private Node currentNode;



    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public ArrayList<GoalNode> getInitialState() {
        return initialState;
    }

    /**
     * 读取初始状态（用GPTs表示）
     * @param gptXMLFileName GPTs的xml文件url
     */
    public void setInitialState(String gptXMLFileName) {
        XMLReader reader = new XMLReader(gptXMLFileName);
        initialState = reader.getTlgs();
    }

    //在图中添加节点
    public void addNode(Node node){
        nodes.add(node);
    }


    //遍历节点Id
    public void traversalId(){
        for(Node node : nodes){
            System.out.println(node.getId() + ";");
        }
    }

    //遍历出所有路径
    public void traversalChildNode(){
        for(Node node : nodes){
            for (Node node1 : node.getChildNode()) {
                System.out.println(node.getId() + "->" + node1.getId() + ";");
            }
        }
    }


    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }
}

