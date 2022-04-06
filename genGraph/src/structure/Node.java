package structure;

import java.util.ArrayList;

public class Node {
    public void setId(int id) {
        this.id = id;
    }

    //节点名字：先用数字
    private int id;

    //节点里存储的action名字
    private String actionName;

//    //点的集合
//    private ArrayList<Node> nodes = new ArrayList<>();

    //节点里存储当前两个goal-plan tree 的执行情况


    /**
     *以该节点为尾节点的边 例：1->2(尾节点)
     */
    private  ArrayList<NodeEdge> outEdges;
    /**
     * 以该节点为头节点的边
     */
    private ArrayList<NodeEdge> inEdges;

    /**
     * 该节点指向的其他node
     */
    private ArrayList<Node> reachableNodes;
    /**
     * 可达该node的其它node
     */
    private ArrayList<Node> beReachableNodes;

    public Node() {

    }

    public int getId() {
        return id;
    }

    public Node(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * 递归地更新可达的node,边的方向为：this--->n
     *
     * @param n 对于该node新增的可达node
     */
    public void updateReachable(Node n) {
        //如果e原来不可达，则添加e到可达node集合中
        if (!this.reachableNodes.contains(n)) {
            this.reachableNodes.add(n);
            this.reachableNodes.addAll(n.reachableNodes);
        }
        //对每一条入边递归地更新
        if (!inEdges.isEmpty()) {
            for (NodeEdge edge : inEdges) {
                edge.from.updateReachable(n);

            }
        }
    }

    /**
     * 递归地更新可达的node,边的方向为：n--->this
     *
     * @param n 对于该node新增的可被达node
     */
    public void updateBeReachable(Node n) {
        //如果e原来不可达，则添加e到可达node集合中
        if (!this.beReachableNodes.contains(n)) {
            this.beReachableNodes.add(n);
            this.beReachableNodes.addAll(n.beReachableNodes);
        }
        //对每一条入边递归地更新
        if (!outEdges.isEmpty()) {
            for (NodeEdge edge : outEdges) {
                edge.to.updateBeReachable(n);
            }
        }
    }

}
