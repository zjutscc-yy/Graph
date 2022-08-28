package xml;

import goalplantree.*;
import xml2bdi.XMLReader;

import java.util.ArrayList;

public class SummaryEnv {

    //获取所有计划的前置条件
    ArrayList<Literal> planPre = new ArrayList<>();

    //获取所有动作的后置条件
    ArrayList<Literal> actPost = new ArrayList<>();

    //得到完全来自于环境的环境变量
    ArrayList<Literal> absoluteEnv = new ArrayList<>();


    public ArrayList<Literal> getPlanPre() {
        return planPre;
    }

    public ArrayList<Literal> getActPost() {
        return actPost;
    }

    public ArrayList<Literal> getAbsoluteEnv() {
        return absoluteEnv;
    }

    //读取树的xml文件根节点
    public SummaryEnv(String gptXMLFileName) {
        XMLReader reader = new XMLReader(gptXMLFileName);
        ArrayList<GoalNode> tlgs = reader.getTlgs();
        for (GoalNode tlg : tlgs) {
            checkGoal(tlg);
            checkAbsolutetEnv();
            planPre.clear();
            actPost.clear();
        }
    }

    //检查计划节点，如果它的孩子节点是action，把post加入set，如果是goal，进行checkGoal
    public void checkPlan(PlanNode plan){
        TreeNode[] planbody = plan.getPlanbody();
        for (TreeNode treeNode : planbody) {
            if (treeNode instanceof ActionNode){
                ActionNode actionNode = (ActionNode) treeNode;
                for (Literal literal : actionNode.getPostc()) {
                    if (!actPost.contains(literal)) {
                        actPost.add(literal);
                    }
                }
            }else {
                GoalNode goalNode = (GoalNode) treeNode;
                checkGoal(goalNode);
            }
        }
    }

    //检查目标节点，获取实现目标的计划，遍历所有计划，将计划的前置条件加入到planPre里，接着对plan进行处理
    public void checkGoal(GoalNode goal){
        PlanNode[] plans = goal.getPlans();
        for (PlanNode plan : plans) {
            for (Literal literal : plan.getPrec()) {
                if (!planPre.contains(literal)) {
                    planPre.add(literal);
                }
            }
            checkPlan(plan);
        }
    }

    //plan的pre减去action的post
    public void checkAbsolutetEnv(){
        for (Literal literal : this.getPlanPre()) {
            if (!this.getActPost().contains(literal) && !this.getAbsoluteEnv().contains(literal)){
                absoluteEnv.add(literal);
            }
        }
    }

    public ArrayList<String> checkAbsolutetEnvName(){
        //得到完全来自于环境的环境变量的名字
        ArrayList<String> absoluteEnvName = new ArrayList<>();
        for (Literal literal : this.getAbsoluteEnv()) {
            absoluteEnvName.add(literal.getName());
        }
        return absoluteEnvName;
    }
}
