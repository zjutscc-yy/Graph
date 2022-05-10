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
        bChoices = new ArrayList<>();
        bResult = -1;
    }

    /**
     * 其他节点的构造器
     * @param a    做了动作a导致了此节点
     */
    public MCTSNode(ArrayList<ActionNode> a){
        selectAction = a;
    }

    /**
     * 最后状态节点的构造器，因为没有做任何动作
     * @return
     */
    public MCTSNode(){

    }

    public boolean isLeaf(){
        return children.size() == 0;
    }

    @Override
    public void run(int alpha, int beta) {

        for (int i = 0; i < alpha; i++) {
            simNum += beta;

            //每次迭代 选择过的节点
            List<MCTSNode> visited = new LinkedList<>();

            //记录到目前为止做的选择，模拟的选择
            ArrayList<ActionNode> ca = new ArrayList<>();

            // 复制图
            Graph cGraph = new Graph();
            cGraph = bigGraph.clone();

            BeliefBaseImp sBeliefs = beliefs.clone();

            // start from the root node
            MCTSNode current = this;

            // add the root node to the list of visited node
            visited.add(current);
            // the root node does not contain any choices, thus we ignore this process

            // selection：在叶子节点中选择UCT值最大的   current不是叶子节点
            while (!current.isLeaf()) {
                // the current node is set to its child node which has the largest UCT value
                current = current.select();
                // once a node is selected, its choices are also added to the list
                if (current.selectAction != null) {//current为最后一个时，倒数第二到最后没有做动作，不需要添加动作，只需要标记已被访问过
                    ca.addAll(current.selectAction);
                }
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
             * 这里面包含了选择
             *simulation
             */
            if (current != null && !current.isLeaf()) {
                // randomly select a node for simulation
                MCTSNode sNode = null;
                double max = 0;
                //扩展后的随机选择
                for (MCTSNode n : current.children) {
                    double randomValue = rm.nextDouble();
                    if (randomValue > max) {
                        max = randomValue;
                        sNode = n;
                    }
                }

                // get the selected node and update the intention and belief base
                ArrayList<ActionNode> sChoices = sNode.selectAction;

                if (sChoices != null) {
                    for (ActionNode a : sChoices) {
                        biUpdate(a, cGraph, sBeliefs);
                    }
                    // add the choices of the new node to the list of choices
                    ca.addAll(sChoices);
                }


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
            else if (current.isLeaf()) {
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

    /**
     * 扩展当前节点：对当前节点添加孩子节点
     * @param graph
     * @param sbeliefs
     */
    private void expand(Graph graph, BeliefBaseImp sbeliefs){
        //获取图现在运行的哪一步
        Node cStep = graph.getRunCurrentNode();

        //得到每个孩子对应做了哪个 动作
        for (Node node : cStep.getChildNode()) {
            if (Node.getDifferentAction(cStep,node) != null) {
                ActionNode act = Node.getDifferentAction(cStep, node);

                ArrayList<ActionNode> ncs = new ArrayList<>();

                ncs.add(act);
                // create new MCTS node
                MCTSNode child = new MCTSNode(ncs);
                // add it as the child of this node
                this.children.add(child);
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
    private double rollOut(Graph graph, BeliefBaseImp beliefs, ArrayList<ActionNode> sChoices) {
        //保存模拟所做的选择
        ArrayList<ActionNode> ass = new ArrayList<>();

        //还可以执行的节点:即当前正在运行的节点的孩子节点
        ArrayList<Node> backNode = new ArrayList<>();

        //复制图
        Graph sGraph = new Graph();
        sGraph = graph.clone();
        if (sGraph.getRunCurrentNode().getChildNode().size() != 0){
            for (Node node : sGraph.getRunCurrentNode().getChildNode()) {
                backNode.add(node);
            }
        }

        // copy the belief base
        BeliefBaseImp sbb = beliefs.clone();

        // simulation starts
        // 模拟停止：当graph.getRunCurrentNode没有孩子节点时
        while (backNode.size() > 0) {
            // the list of choices in the current iteration
            ArrayList<ActionNode> cx = new ArrayList<>();

            // 随机选择一个正在运行节点的孩子节点
            int rc = rm.nextInt(backNode.size());
            //获得所选择的那个节点
            Node indexNode = backNode.remove(rc);//这是图的节点类型的

            //把当前运行节点到随机选择的孩子节点所做的actionNode加到cx里
            if (Node.getDifferentAction(sGraph.getRunCurrentNode(),indexNode) != null) {
                ActionNode toActionNode = Node.getDifferentAction(sGraph.getRunCurrentNode(), indexNode);
                cx.add(toActionNode);
                for (ActionNode a : cx) {
                    biUpdate(a, sGraph, sbb);
                }
                //把正在运行的节点设置为所选的孩子节点
                sGraph.setRunCurrentNode(indexNode);

                // add the choices to the list
                ass.addAll(cx);

                // 重置还可以执行的节点:
                backNode.clear();
                if (sGraph.getRunCurrentNode().getChildNode().size() != 0) {
                    for (Node node : sGraph.getRunCurrentNode().getChildNode()) {
                        backNode.add(node);
                    }
                }
            }else {
                sGraph.setRunCurrentNode(indexNode);
                break;
            }

        }

        // when there is no intention can be executed further, return the simulation score according to the utility function
        double uResult = utility(sGraph);

        ArrayList<ActionNode> temp = new ArrayList<>();
        temp.addAll(sChoices);
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

    private double utility(Graph graph){
        double num = 0;
        //节点没有孩子节点
        if(graph.getRunCurrentNode().getChildNode().size() == 0) {
            num = graph.getRunCurrentNode().getCurrentStep().size();
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

    /**
     * @return the best choices
     */
    public ArrayList<ActionNode> bestActionNode() {
        // if the root node cannot be expanded any further
        if(this.children.size() == 0){
            return new ArrayList<>();
        }
        // otherwise, find the child node that has been visited most
        else {
            int maxVisit = this.children.get(0).statistic.nVisits;
            double best = this.children.get(0).statistic.best;
            double total = this.children.get(0).statistic.totValue;
            double average = this.children.get(0).statistic.totValue / this.children.get(0).statistic.nVisits;
            MCTSNode bestChild = this.children.get(0);
            for(MCTSNode child: children){
                if(child.statistic.totValue / child.statistic.nVisits > average){
                    //if(child.statistic.totValue > total){
                    //if(child.statistic.nVisits > maxVisit){
                    //if(child.statistic.best > best){
                    maxVisit = child.statistic.nVisits;
                    best = child.statistic.best;
                    total = child.statistic.totValue;
                    average = child.statistic.totValue / child.statistic.nVisits;
                    bestChild = child;
                }
            }

            System.out.println("best: " + best);
            return bestChild.selectAction;
        }
    }

    public ArrayList<ActionNode> getAllActions(){
        return bChoices;
    }

}
