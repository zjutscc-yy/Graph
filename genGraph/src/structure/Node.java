package structure;

import java.util.ArrayList;

public class Node {
    //节点名字：先用数字
    private int name;

    private Event event;

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

//    //检查两个节点的event是否相同
//    public void checkNode(Node n1,Node n2){
//        if (n1 == n2){//相同，不需增加节点，边？
//
//        }else{
//            addNode();
//        }
//    }
//
//    //检查结果：如果相同，不加节点，如果不同，增加节点
//    public void addNode(){
//
//    }






}
