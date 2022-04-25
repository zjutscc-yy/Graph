package mcts;

import agent.BeliefBaseImp;
import agent.Choice;
import goalplantree.*;
import structure.Graph;
import structure.Node;

import java.util.*;

public class MCTSNode extends BasicMCTSNode{

    static Graph bigGraph;
    // best simulation choices
    static ArrayList<ActionNode> bChoices;

    //从父节点到该节点选择做的一系列动作,真实选择
    ArrayList<ActionNode> selectAction;

    /**
     * children of this node
     */
    ArrayList<MCTSNode> children = new ArrayList<>();

    /**
     * 根节点的构造器
     * @param graph
     * @param bb
     */
    public MCTSNode(Graph graph,BeliefBaseImp bb){
        bigGraph = graph;
        beliefs = bb;
        selectAction = new ArrayList<>();
        bResult = -1;
    }

    /**
     * 其他节点的构造器
     * @param a 做了动作a导致了此节点
     */
    public MCTSNode(ArrayList<ActionNode> a){
        selectAction = a;
    }

    @Override
    public void run(int alpha, int beta) {

        for (int i = 0; i < alpha; i++) {

            simNum += beta;

            //选择过的节点
            List<MCTSNode> visited = new LinkedList<>();

            //记录到目前为止做的选择，模拟的选择
            ArrayList<ActionNode> ca = new ArrayList<>();

            // 复制图
            Graph cGraph = new Graph();
            cGraph = bigGraph.clone();

            BeliefBaseImp sBeliefs = beliefs.clone();

            MCTSNode current = this;

            visited.add(current);

            // selection：在叶子节点中选择UCT值最大的
            while (!current.isLeaf()) {
                // the current node is set to its child node which has the largest UCT value
                current = current.select();
                // once a node is selected, its choices are also added to the list
                ca.addAll(current.selectAction);
                // the selected node is also added to the list of visited nodes
                visited.add(current);
            }

            for (ActionNode actionNode : ca) {
                biUpdate(actionNode,cGraph,sBeliefs);
            }


            /**
             * expansion
             */
            current.expand(cGraph, sBeliefs);

            /**
             *simulation
             */
            if (current != null && !current.isLeaf()) {
                // randomly select a node for simulation
                MCTSNode sNode = null;
                double max = 0;
                for (MCTSNode n : current.children) {
                    double randomValue = rm.nextDouble();
                    if (randomValue > max) {
                        max = randomValue;
                        sNode = n;
                    }
                }

                // get the selected node and update the intention and belief base
                ArrayList<ActionNode> sChoices = sNode.selectAction;

                for (ActionNode a : sChoices) {
                    biUpdate(a, cGraph, sBeliefs); 
                }

                // add the choices of the new node to the list of choices
                ca.addAll(sChoices);

                // run beta simulations
                for (int j = 0; j < beta; j++) {
                    double sValue = sNode.rollOut(cGraph, sBeliefs, ca);
                    /**
                     * back-propagation
                     */
                    for (MCTSNode node : visited) {
                        node.statistic.addValue(sValue);
                    }
                }


            }

            // if it is a leaf node
            else if (!current.isLeaf()) {
                // check the number of goals achieved
                double sValue = current.getAchievedNum();
                /**
                 * back-propagation
                 */
                for (MCTSNode node : visited) {
                    node.statistic.addValue(sValue);
                }
            }
        }
    }


    /**
     * 扩展当前节点：对当前节点添加孩子节点
     * @param graph
     * @param sbeliefs
     */
    private void expand(Graph graph, BeliefBaseImp sbeliefs){
        //获取图现在运行的哪一步
        Node cStep = graph.getRunCurrentNode();
        for (Node node : cStep.getChildNode()) {
            HashMap<GoalNode, TreeNode> nodeCurrentStep = node.getCurrentStep();
            //在父节点与孩子节点的currentStep中找到不同的即为actionNode
            for (Map.Entry<GoalNode, TreeNode> entry : cStep.getCurrentStep().entrySet()) {
                GoalNode key = entry.getKey();
                TreeNode value = entry.getValue();
                if (nodeCurrentStep.get(key)!=value) {
                    //找到了所做action与孩子节点的对应关系
                    ActionNode act = (ActionNode) nodeCurrentStep.get(key);

                    ArrayList<ActionNode> ncs = new ArrayList<>();

                    ncs.add(act);

                    MCTSNode child = new MCTSNode(ncs);
                    this.children.add(child);
                }
            }
        }
    }

    /**
     * 根据a更新belief,更新的是图
     * @param a 选择a行动做
     * @param graph
     * @param sBeliefs
     */
    private void biUpdate(ActionNode a, Graph graph, BeliefBaseImp sBeliefs){
        //通过a知道由该节点指向哪个孩子节点,更新图的现在运行的节点
        for (Node node : graph.getRunCurrentNode().getChildNode()) {
            for (TreeNode value : node.getCurrentStep().values()) {
                if (value instanceof ActionNode){
                    ActionNode actionNode = ((ActionNode) value);
                    if(actionNode.equals(a)){
                        graph.setRunCurrentNode(node);
                        break;
                    }
                }
            }
            break;
        }

        // get its postcondition
        Literal[] postc = a.getPostc();
        // apply changes to the simulation belief base
        for(Literal l : postc){
            sBeliefs.update(l);
        }
    }

    /**
     * @return the simulation rollouts
     */
    private double rollOut(Graph graph, BeliefBaseImp beliefs, ArrayList<ActionNode> sActionNode) {
        // to store the choices made in the simulation
        ArrayList<ActionNode> ass = new ArrayList<>();

        // the list of available intentions
        ArrayList<Integer> indexes = new ArrayList<>();

        //复制图
        Graph sGraph = new Graph();
        sGraph = graph.clone();

        // copy the belief base
        BeliefBaseImp sbb = beliefs.clone();

        // simulation starts
        // the simulation stops only when all intention becomes non-progressable
        intentionloop:
        while (indexes.size() > 0) {
            // the list of choices in the current iteration
            ArrayList<ActionNode> cx = new ArrayList<>();

            // randomly pick a choice
            int rc = rm.nextInt(indexes.size());
            int index = indexes.remove(rc);

            Node currentStep = sGraph.getRunCurrentNode();

        }

        // when there is no intention can be executed further, return the simulation score according to the utility function
        double uResult = utility(sGraph);

        ArrayList<ActionNode> temp = new ArrayList<>();
        temp.addAll(sActionNode);
        temp.addAll(ass);


        // if this simulation performs better than previous run
        if(uResult > bResult){
            bResult = uResult;
            bChoices = temp;
        }
        return uResult;
    }

    @Override
    public double getAchievedNum() {
        return utility(bigGraph);
    }

    /**
     * @return a child node with maximum UCT value
     */
    protected MCTSNode select(){
        // initialisation
        MCTSNode selected = null;
        double bestUCT = Double.MIN_VALUE;
        // calculate the uct value for each of its selected nodes
        for(int i = 0; i < children.size(); i++){
            // UCT calculation
            double uctValue = children.get(i).statistic.totValue/ (children.get(i).statistic.nVisits + epsilon)+
                    Math.sqrt(Math.log(statistic.nVisits + 1)/(children.get(i).statistic.nVisits + epsilon))+ rm.nextDouble() * epsilon;
            // compare the uct value with the current maximum value
            if(uctValue > bestUCT){
                selected = children.get(i);
                bestUCT = uctValue;
            }
        }
        // return the nodes with maximum UCT value, null if current node is a leaf node (contains no child nodes)
        return selected;
    }

    private double utility(Graph graph){
        double num = 0;
        //节点没有孩子节点
        if(graph.getRunCurrentNode().getChildNode().size() == 0) {
            num++;
        }
        return num;
    }

    private ArrayList<ArrayList<Integer>> getPosChoices(GoalNode sg, BeliefBaseImp bb){
        // initialise the list
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        // get its associated plans
        PlanNode[] pls = sg.getPlans();
        // check each plan
        for(int i = 0; i < pls.length; i++){
            // get the precondition of the plan
            Literal[] context = pls[i].getPrec();
            // if its precondition holds
            if(bb.evaluate(context) == 1){
                // get the first step of this plan
                TreeNode first = pls[i].getPlanbody()[0];
                // if the first step is an action
                if(first instanceof ActionNode){
                    // initialise the list
                    ArrayList<Integer> cs = new ArrayList<>();
                    // add the plan choice
                    cs.add(i);
                    result.add(cs);
                }
                // if the first step is a subgoal
                else {
                    // cast it to a subgoal
                    GoalNode g = (GoalNode) first;
                    // get the list of choice lists
                    ArrayList<ArrayList<Integer>> css = getPosChoices(g, bb);
                    // for each of these lists
                    for(ArrayList<Integer> s : css){
                        ArrayList<Integer> cs = new ArrayList<>();
                        // Add the plan choice
                        cs.add(i);
                        // then add all the choices in the list
                        cs.addAll(s);
                        result.add(cs);
                    }
                }
            }
        }
        return result;
    }

}
