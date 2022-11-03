package agent;

import environment.AbstractEnvironment;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.Literal;
import goalplantree.TreeNode;
import structure.Graph;
import structure.Node;

import java.util.ArrayList;

public class ClawGraphAgent extends AbstractAgent {

    public Graph graph;
    //当前要找的环境
    public ArrayList<Integer> thisEnv;

    public ActionNode act;

    public ClawGraphAgent(String id, ArrayList<Belief> bs) {
        super(id, bs);
    }

    public ClawGraphAgent(String id, ArrayList<Belief> bs, Graph graph, ArrayList<Integer> thisEnv) {
        super(id, bs);
        this.graph = graph;
        this.thisEnv = thisEnv;
    }

    //找到当前环境对应哪条路径
    public int searchRouteId(ArrayList<Integer> thisEnv) {
        return graph.getEnvs().get(thisEnv);
    }

    @Override
    public boolean deliberate() {
        //执行图，只有在第一步需要特殊处理
        if (graph.getRunCurrentNode().getId() == graph.getRoot().getId()){
            //找到初始状态到当前环境下的第一个动作
            for (Node node : graph.getRoot().getChildNode()) {
                if (node.getId() == searchRouteId(thisEnv)) {
                    //找到对应那条路径
                    act = Node.getDifferentAction(graph.getRunCurrentNode(), node);
                    //更新图的正在执行的节点
                    graph.setRunCurrentNode(node);
                    return true;
                }
            }
        }

        if (graph.getRunCurrentNode().getChildNode().size() != 0) {
            Node node = graph.getRunCurrentNode().getChildNode().get(0);
            if (Node.getDifferentAction(graph.getRunCurrentNode(), node) == null) {
                return false;
            }
            act = Node.getDifferentAction(graph.getRunCurrentNode(), node);
            graph.setRunCurrentNode(node);
            return true;
        }
        return false;
    }

    @Override
    public void exeSucceed() {
        //需要更新环境状态
        Literal[] post = act.getPostc();
        for (Literal l : post) {
            this.bb.update(l);
        }

        // 如果图中的某个目标执行成功
        if (graph.getRunCurrentNode().getAchievedGoal().size() != 0){
            ArrayList<GoalNode> achieveTlg = graph.getRunCurrentNode().getAchievedGoal();
            for (GoalNode goalNode : achieveTlg) {
                if (!achievedGoals.contains(goalNode.getName())) {
                    achievedGoals.add(goalNode.getName());
                }
            }
        }
    }

    @Override
    public void exeFail() {
        // get the last action
        ActionNode action = act;
        System.out.println("cc: " + action.getName());

        // update the intention
        graph.fail(action);
    }

    @Override
    public ActionNode execute(AbstractEnvironment environment) {
        // check if there is a decision has been made already. If there is, then execute it
        if(act != null){
            ActionNode action = act;
            System.out.println("progress!!!");
            // activate this action
            action.setStatus(TreeNode.Status.ACTIVE);
            return action;
        }
        return null;
    }
}
