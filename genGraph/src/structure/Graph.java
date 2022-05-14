package structure;

import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.TreeNode;
import org.apache.commons.math3.ode.events.Action;
import xml2bdi.XMLReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.channels.AcceptPendingException;
import java.util.*;

public class Graph {

    public ArrayList<GoalNode> initialState;

    //图的根节点
    private Node root;

    //图中存储哪些Node
    private ArrayList<Node> nodes = new ArrayList<>();

    //表示当前指向哪个节点
    private Node currentNode;

    //运行graph时的当前节点
    private Node runCurrentNode;

    //所有目标都实现的节点
    private Node endNode;

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public Node getRunCurrentNode() {
        return runCurrentNode;
    }

    public void setRunCurrentNode(Node runCurrentNode) {
        this.runCurrentNode = runCurrentNode;
    }

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

    public Graph clone(){
        Graph cGraph = new Graph();
        for (Node node : this.getNodes()) {
            cGraph.addNode(node);
        }
        cGraph.setRoot(this.getRoot());
        cGraph.setRunCurrentNode(this.getRunCurrentNode());
        cGraph.setEndNode(this.getEndNode());
        return cGraph;
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

    /**
     * 如果选择的这个action执行成功，更新图
     */
    public void success(ActionNode action){
        Node runCurrentNode = this.getRunCurrentNode();
        action.setStatus(TreeNode.Status.SUCCESS);
        for (Node node : runCurrentNode.getChildNode()) {
            if (Node.getDifferentAction(runCurrentNode, node) != null) {
                if (Node.getDifferentAction(runCurrentNode, node).equals(action)) {
                    this.setRunCurrentNode(node);
                }
            }else {//也就是到了图的最终状态（getDifferentAction为空）
                this.setRunCurrentNode(node);
            }
        }
    }

    /**
     * graph里某个目标成功
     */
    public ArrayList<GoalNode> achieved(Node node){
        HashMap<GoalNode, TreeNode> nodeCurrentStep = node.getCurrentStep();
        ArrayList<GoalNode> achievegoal = new ArrayList<>();
        for (Map.Entry<GoalNode, TreeNode> entry : nodeCurrentStep.entrySet()) {
            GoalNode key = entry.getKey();
            if (nodeCurrentStep.get(key) == null){
                achievegoal.add(key);
            }
        }
        return achievegoal;
    }

    public void fail(ActionNode action){
        action.setStatus(TreeNode.Status.FAILURE);
    }
//    public ActionNode progress(){
//        System.out.println("progress!!!");
//        // if the top-level goal has not been achieved already
//        if(runCurrentNode != null && !runCurrentNode.equals(endNode)){
//            // get the action
//            ActionNode act = ;
//            // activate this action
//            act.setStatus(TreeNode.Status.ACTIVE);
//            return act;
//
//        }
//        return null;
//    }
}

