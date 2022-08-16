package xml;

import goalplantree.GoalNode;
import goalplantree.Literal;
import goalplantree.PlanNode;
import goalplantree.TreeNode;

import java.util.ArrayList;

public class ExecutableGoal {

    //如果是目标，孩子节点则是or的关系，将两个ArrayList合并成一个ArrayList
    public ArrayList<Literal[]> checkGoal(GoalNode goalNode){
        ArrayList<Literal[]> goalLiteral = new ArrayList<>();
        int i = 0;
        for (PlanNode plan : goalNode.getPlans()) {
            //获取该goal的每个plan的前置条件
            Literal[] prec = plan.getPrec();
            ArrayList<Literal[]> literals = checkPlan(plan);
            for (Literal literal : plan.getPrec()) {
                //每获得计划的一个前置条件，就把他加到后面

            }
        }
        return goalLiteral;
    }

    //如果是plan，孩子节点则是and的关系，进行排列组合
    public ArrayList<Literal[]> checkPlan(PlanNode plan){
        //用于存储该plan节点下goal的集合
        ArrayList<Literal[]> planLiteral = new ArrayList<>();
        TreeNode[] planbody = plan.getPlanbody();
        for (TreeNode treeNode : planbody) {//遍历该节点的planbody
            //当plan的孩子节点是Goal的时候
            if (treeNode instanceof GoalNode){
                GoalNode goal = (GoalNode) treeNode;
                ArrayList<Literal[]> literals = checkGoal(goal);
                planLiteral.addAll(literals);
            }
        }
        //对plan有可能的多个子目标进行排列组合
        ArrayList<Literal[]> combine = ExecutableGoal.combination(planLiteral,0,null);
        return combine;
    }

    public static ArrayList<Literal[]> combination(ArrayList<Literal[]> literals,int index,ArrayList<Literal[]> resultList){

        if(index == literals.size()){
            return resultList;
        }

        ArrayList<Literal[]> resultList0=new ArrayList<Literal[]>();
        if(index == 0){
            Literal[] literalArr = literals.get(0);
            for (Literal literal : literalArr) {
                resultList0.add(new Literal[]{literal});
            }
        }else{
            Literal[] literalArr = literals.get(index);
            for (Literal[] literalArr0 : resultList) {
                for (Literal literal : literalArr) {
                    //复制数组并扩充新元素
                    Literal[] literalArrCopy = new Literal[literalArr0.length+1];
                    System.arraycopy(literalArr0,0,literalArrCopy,0,literalArr0.length);
                    literalArrCopy[literalArrCopy.length-1] = literal;

                    //追加到结果集
                    resultList0.add(literalArrCopy);
                }
            }
        }
        return combination(literals,++index,resultList0);
    }

}
