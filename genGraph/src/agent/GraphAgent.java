package agent;

import environment.AbstractEnvironment;
import goalplantree.*;
import mcts.MCTSNode;
import structure.Graph;
import structure.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphAgent extends AbstractAgent{

    Graph graph;
    /**
     * in the current cycle选择的action
     */
    ArrayList<ActionNode> actions;

    // to record the best simulation choices
    ArrayList<ActionNode> bestChoices = new ArrayList<>();

    // to record the best simulation result
    double bestResult = -1;
    // alpha and beta are set to 100 and 50 respectively by default
    int alpha = 100;
    int beta = 10;

    /**
     * 构造器
     * @param id  agent的name
     * @param bs  初始的beliefs
     */

    public GraphAgent(String id, ArrayList<Belief> bs, Graph graph){
        super(id,bs);
        this.graph = graph;
    }
    public GraphAgent(String id, ArrayList<Belief> bs) {
        super(id, bs);
    }

    public GraphAgent(String id, ArrayList<Belief> bs, ArrayList<GoalNode> gs){
        super(id, bs, gs);
    }

    public GraphAgent(String id, ArrayList<Belief> bs, ArrayList<GoalNode> gs, HashMap<String, Double> vs){
        super(id, bs, gs, vs);
    }

    /**
     * set the iteration
     * @param alpha
     */
    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    /**
     * set the number of simulation per iteration
     * @param beta
     */
    public void setBeta(int beta) {
        this.beta = beta;
    }

    @Override
    public boolean deliberate() {
        //图每次更新自己的runcurrentNode，就是真实走了一步，比如图走到29，然后以29建立mcts的root，进行alpha，beta
        MCTSNode root = new MCTSNode(this.graph,this.bb);
        long start = System.currentTimeMillis();
        //run mcts
        root.run(alpha,beta);

        // get the best choice
        ArrayList<ActionNode> cs = root.bestActionNode();

        if(cs.size() > 0){
            this.actions = cs;
            return true;
        }

//        if (graph.getRunCurrentNode().getChildNode().size() == 1 &&
//                graph.getRunCurrentNode().getChildNode().get(0).equals(graph.getEndNode())){
//
//        }


        return false;
    }

    /**
     *
     * @return MCTS node
     */

    //获取mcts执行节点
    public MCTSNode getRoot(){

        MCTSNode root = new MCTSNode(this.graph,this.bb);
        long start = System.currentTimeMillis();
        //run mcts
        root.run(alpha,beta);

        return root;
    }

    @Override
    public void exeSucceed() {
        // get the last action
        ActionNode action = actions.remove(0);

        // update the belief base according to the action's postcondition
        Literal[] post = action.getPostc();
        for (Literal l : post) {
            this.bb.update(l);
        }

        // update the intention
        graph.success(action);

        // 如果图中的某个目标执行成功
        if (graph.achieved(graph.getRunCurrentNode()) != null) {
            ArrayList<GoalNode> achieveTlg = graph.achieved(graph.getRunCurrentNode());
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
        ActionNode action = actions.remove(0);
        System.out.println("cc: " + action.getName());

        // update the intention
        graph.fail(action);
    }

    /**
     * progress the agent's intentions and return the action to execute at the current cycle
     * @param environment the associated environment
     * @return the action to execute at the current cycle
     */
    @Override
    public ActionNode execute(AbstractEnvironment environment) {
        // check if there is a decision has been made already. If there is, then execute it
        while(this.actions.size() > 0){
            // get the immediate choice
            ActionNode action = this.actions.get(0);
            System.out.println("progress!!!");
            // activate this action
            action.setStatus(TreeNode.Status.ACTIVE);
            return action;
        }
        return null;
    }
}
