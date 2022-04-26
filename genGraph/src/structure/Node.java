package structure;

import com.sun.source.tree.Tree;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.PlanNode;
import goalplantree.TreeNode;
import xml2bdi.XMLReader;

import java.util.*;

public class Node {
    //节点名字：先用数字
    private int id;

    //action 中 T1-A1 中的T1
    private String actionTreeName;

    //action 中 T1-A1 中的A1
    private String actionName;

    //当前执行到多棵 goal-plan tree的哪个步骤  如<G0,Action A0>  <G1,Action A5>
    private HashMap<GoalNode, TreeNode> currentSteps;

    //节点的孩子节点
    private ArrayList<Node> childNode = new ArrayList<>();



    //在currentNode下添加子节点
    public void addChildNode(Node node){
        childNode.add(node);
    }

    public void removeChildNode(Node node){
        childNode.remove(node);
    }

    //判断两个节点是否相等(currentsteps相等即认为节点相等)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node otherNode = (Node) o;
        HashMap otherCurrentSteps = otherNode.currentSteps;
        if (currentSteps.size() != otherCurrentSteps.size()) {
            return false;
        }
        for (Map.Entry<GoalNode, TreeNode> goalNodeActionNodeEntry : currentSteps.entrySet()) {
            // 当前的key （tlg）
            GoalNode tlg = goalNodeActionNodeEntry.getKey();
            // 当前的value （current step）
            TreeNode curStep = goalNodeActionNodeEntry.getValue();
            // 如果比较的对象不含有当前key，则说明一定不相等
            if (!otherCurrentSteps.containsKey(tlg)) {
                return false;
            }
            // 如果当前tlg的current step与比较对象的current step不相等，返回false
            if (!otherCurrentSteps.get(tlg).equals(curStep)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentSteps);
    }

    public ArrayList<Node> getChildNode() {
        return childNode;
    }

    //读取生成的树的xml文件中的 top-leve goal
//    public ArrayList<GoalNode> getGoalNodes(String fileName) {
//        //读取goal-plan tree的xml文件
//        XMLReader reader = new XMLReader(fileName);
//        //获取多个目标的 top-level goal
//        ArrayList<GoalNode> tlgs = reader.getTlgs();
//        return tlgs;
//    }

    public Node() {}

    public Node(int id, HashMap<GoalNode, TreeNode> currentSteps) {
        this.id = id;
        this.currentSteps = currentSteps;
    }

    public Node(int id, String actionTreeName, String actionName) {
        this.id = id;
        this.actionTreeName = actionTreeName;
        this.actionName = actionName;
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
    public static TreeNode traversal(GoalNode node,String actionName) {
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(node);
        while (!queue.isEmpty()) {

            TreeNode poll = queue.poll();

            //判断poll是否为action节点，如果是，与当前读到的action比较
            if (poll instanceof ActionNode) {
                String searchAction;
                String[] strArray = poll.getName().split("-");//把 T 分割出来
                searchAction = strArray[1];

                if (searchAction.equals(actionName)){
                    return poll;
                }
            }

            else if (poll instanceof PlanNode) {
                PlanNode planNode = (PlanNode) poll;
                if (planNode.getPlanbody() != null) {
                    for (TreeNode treeNode : planNode.getPlanbody()) {
                        queue.offer(treeNode);
                    }

                }
            }

            else if (poll instanceof GoalNode) {
                GoalNode goalNode = (GoalNode) poll;
                if (goalNode.getPlans() != null) {
                    for (PlanNode plan : goalNode.getPlans()) {
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

    public HashMap<GoalNode, TreeNode> getCurrentStep() {
        return currentSteps;
    }

    public void setCurrentStep(HashMap currentStep) {
        this.currentSteps = currentStep;
    }
}
