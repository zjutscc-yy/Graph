package xml;

import goalplantree.GoalNode;
import goalplantree.Literal;
import goalplantree.PlanNode;
import goalplantree.TreeNode;

import java.util.ArrayList;

public class genAllCase {

    private ArrayList<String> envs;

    public genAllCase(ArrayList<String> envs){
        this.envs = envs;
    }

    public ArrayList<Literal[]> checkGoal(GoalNode goalNode){
        //要返回的最终结果
        ArrayList<Literal[]> result = new ArrayList<>();
        //TODO 保存该目标下所有plan  check后的结果
//        ArrayList<ArrayList<Literal[]>> allPlanCheck = new ArrayList<>();
//        //把该目标下所有plan  check后的结果加到同一个ArrayList里面
//        for (PlanNode plan : goalNode.getPlans()) {
//            allPlanCheck.add(checkPlan(plan));
//        }
        //每次选取
        PlanNode[] plans = goalNode.getPlans();

        ArrayList<Literal[]> p1 = checkPlan(plans[0]);
        ArrayList<Literal[]> p2 = checkPlan(plans[1]);
        //对第一个计划的可能进行处理
        for (Literal[] ls1 : p1) {
            ArrayList<String> literalName = new ArrayList<>();
            //遍历第一个计划的第一种可能，保存下来涉及的变量名，让第二个计划包含此变量名的不做修改
            for (Literal literal : ls1) {
                if (!literalName.contains(literal.getName()) && envs.contains(literal.getName())){
                    literalName.add(literal.getName());
                }
            }
            //遍历第二个计划的所有可能
            for (Literal[] ls2 : p2) {
                //需要进行修改真假的literal
                ArrayList<Literal> needEdit = new ArrayList<>();
                //每种可能的每个元素
                for (Literal literal : ls2) {
                    if (!literalName.contains(literal.getName()) && envs.contains(literal.getName())){
                        needEdit.add(literal);
                    }
                }
                //把需要修改真假的Literal转变成数组形式
                Literal[] needEditLiteral = listToArray(needEdit);
                //得到第二个计划中第一种可能的所有真值组合,遍历，与第一个计划中的第一种可能合并
                for (Literal[] genValue : genValues(needEditLiteral)) {
                    Literal[] combination = ExecutableGoal.combination(ls1, genValue);
                    result.add(combination);
                }
            }
        }
        //对第二个计划的可能进行处理
        for (Literal[] ls2 : p2) {
            ArrayList<String> literalName = new ArrayList<>();
            //遍历第一个计划的第一种可能，保存下来涉及的变量名，让第二个计划包含此变量名的不做修改
            for (Literal literal : ls2) {
                if (!literalName.contains(literal.getName()) && envs.contains(literal.getName())){
                    literalName.add(literal.getName());
                }
            }
            //遍历第二个计划的所有可能
            for (Literal[] ls1 : p1) {
                //需要进行修改真假的literal
                ArrayList<Literal> needEdit = new ArrayList<>();
                //每种可能的每个元素
                for (Literal literal : ls1) {
                    if (!literalName.contains(literal.getName()) && envs.contains(literal.getName())){
                        needEdit.add(literal);
                    }
                }
                //把需要修改真假的Literal转变成数组形式
                Literal[] needEditLiteral = listToArray(needEdit);
                //得到第二个计划中第一种可能的所有真值组合,遍历，与第一个计划中的第一种可能合并
                if (needEditLiteral.length != 0) {
                    for (Literal[] genValue : genValues(needEditLiteral)) {
                        Literal[] combination = ExecutableGoal.combination(ls2, genValue);
                        result.add(combination);
                    }
                }
            }
        }
        return ExecutableGoal.deDuplication(result);
    }

    public ArrayList<Literal[]> checkPlan(PlanNode plan){
        ArrayList<Literal[]> resultLiteral = new ArrayList<>();
        //用于存储该plan节点下goal的集合
        ArrayList<Literal[]> planLiteral = new ArrayList<>();
        TreeNode[] planbody = plan.getPlanbody();
        //用于判断plan孩子节点是goal类型的有几个，若没有返回该plan的pre-，若是多个，则要进行排列组合
        int i = 0;
        Literal[] prec = plan.getPrec();
        for (TreeNode treeNode : planbody) {//遍历该节点的planbody
            //当plan的孩子节点是Goal的时候
            if (treeNode instanceof GoalNode){
                i++;
                GoalNode goal = (GoalNode) treeNode;
                //先获取goal的Literal[],再进行排列组合，最后再加入当前plan的pre-
                ArrayList<Literal[]> literals = checkGoal(goal);
                //每得到一个goal就与之前得到的进行排列组合
                planLiteral = ExecutableGoal.combination(planLiteral,literals);
            }
        }
        if (i == 0){
            ArrayList<Literal> envLiteral = new ArrayList<>();
            //保存pre中为完全来自于环境中的遍量
            for (Literal literal : prec) {
                if (envs.contains(literal.getName())){
                    envLiteral.add(literal);
                }
            }
            Literal[] literals = listToArray(envLiteral);
            planLiteral.add(literals);
            return planLiteral;
        }
        else {
            //每获得计划的一个前置条件，就把他加到check该计划的结果后面
            for (Literal[] literal : planLiteral) {
                //需要进行判断是不是完全来自于环境
                Literal[] c = ExecutableGoal.combination(literal, prec);
                Literal[] result = ExecutableGoal.deDuplication(c);
                resultLiteral.add(result);
            }
            return resultLiteral;
        }
    }

    public static Literal[] listToArray(ArrayList<Literal> A){
        Literal[] B = new Literal[A.size()];
        for (int i = 0; i < A.size(); i++) {
            B[i] = A.get(i);
        }
        return B;

    }

    //获取所有可能的真值的排列组合
    public static ArrayList<Literal[]> genValues(Literal[] A){
        ArrayList<Literal[]> result = new ArrayList<>();
        ValueCombine c = new ValueCombine(A.length);
        c.solution();
        for (boolean[] booleans : c.getAlltuples()) {
            Literal[] newLiteral = new Literal[A.length];
            for (int i = 0; i < booleans.length; i++) {
                newLiteral[i] = A[i];
                newLiteral[i].setState(booleans[i]);
            }
            result.add(newLiteral);
        }

        return result;
    }
}
