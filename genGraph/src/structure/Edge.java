package structure;

import java.util.ArrayList;

public class Edge {

    //该边的头节点
    Node from;
    //该边的尾节点
    Node to;

//    //边的集合
//    private ArrayList<Edge> edges = new ArrayList<>();

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    //加边操作，从nodefrom->nodeto
    public void addEdge(Node from,Node to){

    }
}
