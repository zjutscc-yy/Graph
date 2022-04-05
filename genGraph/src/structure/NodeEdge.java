package structure;

public class NodeEdge {
    //该边的头节点
    Node from;
    //该边的尾节点
    Node to;

    public NodeEdge(Node from,Node to){
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
}
