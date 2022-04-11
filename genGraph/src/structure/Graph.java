package structure;

import java.util.ArrayList;

public class Graph {

    //表示当前指向哪个节点
    private Node currentNode;

    //存储Node节点
    private ArrayList<Node> nodes = new ArrayList<Node>();

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

    //遍历所有节点的孩子节点
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

