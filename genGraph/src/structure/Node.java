package structure;

import com.sun.source.tree.Tree;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.PlanNode;
import goalplantree.TreeNode;
import xml2bdi.XMLReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Node {
    //节点名字：先用数字
    private int id;

    //action 中 T1-A1 中的T1
    private String actionTreeName;

    //action 中 T1-A1 中的A1
    private String actionName;

    //当前执行到多棵 goal-plan tree的哪个步骤
    private HashMap currentSteps;

    //节点里所存的top-level goal
    private ArrayList<GoalNode> tlgs;

    public HashMap getCurrentStep() {
        return currentSteps;
    }

    public void setCurrentStep(HashMap currentStep) {
        this.currentSteps = currentStep;
    }

    //读取生成的树的xml文件中的 top-leve goal
    public ArrayList<GoalNode> getGoalNodes(String fileName) {
        //读取goal-plan tree的xml文件
        XMLReader reader = new XMLReader(fileName);
        //获取多个目标的 top-level goal
        ArrayList<GoalNode> tlgs = reader.getTlgs();
        return tlgs;
    }

    public Node() {}

    public Node(int id, String actionTreeName, String actionName, HashMap currentStep) {
        this.id = id;
        this.actionTreeName = actionTreeName;
        this.actionName = actionName;
        this.currentSteps = currentSteps;
    }

    //添加当前节点
    public void addNode(int i, String[] strArray) {
        //设置node属性
        this.setId(i + 1);
        this.setActionTreeName(strArray[0]);
        this.setActionName(strArray[1]);
    }

    //找到当前action属于哪颗树
    public GoalNode searchWhichGoal(ArrayList<GoalNode> tlgs) {
        for (GoalNode tlg : tlgs) {
            String searchTree;
            String[] strArray = tlg.getName().split("-");//把 T 分割出来
            searchTree = strArray[0];

            if (searchTree.equals(this.getActionTreeName())){//找到当前读取的action属于哪颗树，接下来对树进行层次遍历
                return tlg;
            }
        }
        return null;
    }

    //已经找到当前action属于哪颗树，        现对这棵树进行遍历找到对应action，然后setCurrentStep
    public TreeNode traversal(GoalNode node,String actionName) {
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(node);
        int count = 0;
        while (!queue.isEmpty()) {
            count++;
            TreeNode poll = queue.poll();

            //判断poll是否为action节点，如果是，与当前读到的action比较
            if (poll instanceof ActionNode) {
                String searchAction;
                String[] strArray = poll.getName().split("-");//把 T 分割出来
                searchAction = strArray[1];

                if (searchAction == actionName){
                    return poll;
                }
            }

            else if (poll instanceof PlanNode) {
                if (((PlanNode) poll).getPlanbody() != null) {
                    for (TreeNode treeNode : ((PlanNode) poll).getPlanbody()) {
                        queue.offer(treeNode);
                    }

                }
            }

            else if (poll instanceof GoalNode) {
                if (((GoalNode) poll).getPlans() != null) {
                    for (PlanNode plan : node.getPlans()) {
                        queue.offer((TreeNode) plan);
                    }

                }
            }

        }
        return null;
    }



    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }


    //返回当前action是哪棵树的
    public String getActionTreeName() {
        return actionTreeName;
    }

    public void setActionTreeName(String actionTreeName) {
        this.actionTreeName = actionTreeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }




//    /**
//     *以该节点为尾节点的边 例：1->2(尾节点)
//     */
//    private  ArrayList<NodeEdge> outEdges;
//    /**
//     * 以该节点为头节点的边
//     */
//    private ArrayList<NodeEdge> inEdges;
//
//    /**
//     * 该节点指向的其他node
//     */
//    private ArrayList<Node> reachableNodes;
//    /**
//     * 可达该node的其它node
//     */
//    private ArrayList<Node> beReachableNodes;


//    /**
//     * 递归地更新可达的node,边的方向为：this--->n
//     *
//     * @param n 对于该node新增的可达node
//     */
//    public void updateReachable(Node n) {
//        //如果e原来不可达，则添加e到可达node集合中
//        if (!this.reachableNodes.contains(n)) {
//            this.reachableNodes.add(n);
//            this.reachableNodes.addAll(n.reachableNodes);
//        }
//        //对每一条入边递归地更新
//        if (!inEdges.isEmpty()) {
//            for (NodeEdge edge : inEdges) {
//                edge.from.updateReachable(n);
//
//            }
//        }
//    }
//
//    /**
//     * 递归地更新可达的node,边的方向为：n--->this
//     *
//     * @param n 对于该node新增的可被达node
//     */
//    public void updateBeReachable(Node n) {
//        //如果e原来不可达，则添加e到可达node集合中
//        if (!this.beReachableNodes.contains(n)) {
//            this.beReachableNodes.add(n);
//            this.beReachableNodes.addAll(n.beReachableNodes);
//        }
//        //对每一条入边递归地更新
//        if (!outEdges.isEmpty()) {
//            for (NodeEdge edge : outEdges) {
//                edge.to.updateBeReachable(n);
//            }
//        }
//    }

}
