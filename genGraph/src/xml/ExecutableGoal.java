package xml;

import goalplantree.GoalNode;
import goalplantree.Literal;
import goalplantree.PlanNode;
import goalplantree.TreeNode;

import java.util.ArrayList;

public class ExecutableGoal {

    public ArrayList<Literal[]> checkGoal(GoalNode goalNode){
        ArrayList<Literal[]> goalLiteral = new ArrayList<>();
        for (PlanNode plan : goalNode.getPlans()) {
            //获取该goal的每个plan的前置条件
            ArrayList<Literal[]> literals = checkPlan(plan);
            for (Literal[] literal : literals) {
                goalLiteral.add(literal);
            }
        }
        ArrayList<Literal[]> result = ExecutableGoal.deDuplication(goalLiteral);
        return result;
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
                planLiteral = combination(planLiteral,literals);
            }
        }
        if (i == 0){
            planLiteral.add(plan.getPrec());
            return planLiteral;
        }
        else {
            //每获得计划的一个前置条件，就把他加到check该计划的结果后面
            for (Literal[] literal : planLiteral) {
                Literal[] c = new Literal[literal.length + prec.length];
                System.arraycopy(literal,0,c,0,literal.length);
                System.arraycopy(prec,0,c,literal.length,prec.length);
                Literal[] result = ExecutableGoal.deDuplication(c);
                resultLiteral.add(result);
            }
            return resultLiteral;
        }
    }

    public static ArrayList<Literal[]> combination(ArrayList<Literal[]> A,ArrayList<Literal[]> B){
        ArrayList<Literal[]> resultList = new ArrayList<Literal[]>();
        if (A.size() == 0){
            return B;
        }
        for (int i = 0; i < A.size(); i++) {
            for (int j = 0; j < B.size(); j++) {
                Literal[] literalArrCopy = new Literal[A.get(i).length + B.get(j).length];
                //合并数组
                System.arraycopy(A.get(i),0,literalArrCopy,0,A.get(i).length);
                System.arraycopy(B.get(j),0,literalArrCopy,A.get(i).length,B.get(j).length);
                Literal[] delSame = deDuplication(literalArrCopy);
                resultList.add(delSame);
            }
        }
        return deDuplication(resultList);
    }
//    public static ArrayList<Literal[]> combination(ArrayList<Literal[]> literals,int index,ArrayList<Literal[]> resultList){
//
//        if(index == literals.size()){
//            return resultList;
//        }
//
//        ArrayList<Literal[]> resultList0 = new ArrayList<Literal[]>();
//        if(index == 0){
//            Literal[] literalArr = literals.get(0);
//            for (Literal literal : literalArr) {
//                resultList0.add(new Literal[]{literal});
//            }
//        }else{
//            Literal[] literalArr = literals.get(index);
//            for (Literal[] literalArr0 : resultList) {
//                for (Literal literal : literalArr) {
//                    //复制数组并扩充新元素
//                    Literal[] literalArrCopy = new Literal[literalArr0.length+1];
//                    System.arraycopy(literalArr0,0,literalArrCopy,0,literalArr0.length);
//                    literalArrCopy[literalArrCopy.length-1] = literal;
//
//                    //追加到结果集
//                    resultList0.add(literalArrCopy);
//                }
//            }
//        }
//        return combination(literals,++index,resultList0);
//    }

    //如果是目标，孩子节点则是or的关系，将两个ArrayList合并成一个ArrayList
//    public ArrayList<Literal[]> checkGoal(GoalNode goalNode){
//        ArrayList<Literal[]> goalLiteral = new ArrayList<>();
//        for (PlanNode plan : goalNode.getPlans()) {
//            //获取该goal的每个plan的前置条件
//            Literal[] prec = plan.getPrec();
//            ArrayList<Literal[]> literals = checkPlan(plan);
//            //每获得计划的一个前置条件，就把他加到check该计划的结果后面
//            for (Literal[] literal : literals) {
//                Literal[] c = new Literal[literal.length+prec.length];
//                System.arraycopy(literal,0,c,0,literal.length);
//                System.arraycopy(prec,0,c,literal.length,prec.length);
//                Literal[] result = ExecutableGoal.deDuplication(c);
//                goalLiteral.add(result);
//            }
//        }
//        return goalLiteral;
//    }

    //如果是plan，孩子节点则是and的关系，进行排列组合
//    public ArrayList<Literal[]> checkPlan(PlanNode plan){
//        //用于存储该plan节点下goal的集合
//        ArrayList<Literal[]> planLiteral = new ArrayList<>();
//        TreeNode[] planbody = plan.getPlanbody();
//        //用于判断plan还有没有孩子节点是goal类型的
//        int i = 0;
//        for (TreeNode treeNode : planbody) {//遍历该节点的planbody
//            //当plan的孩子节点是Goal的时候
//            if (treeNode instanceof GoalNode){
//                i++;
//                GoalNode goal = (GoalNode) treeNode;
//                ArrayList<Literal[]> literals = checkGoal(goal);
//                planLiteral.addAll(literals);
//            }
//        }
//        if (i == 0){
//            planLiteral.add(plan.getPrec());
//            return planLiteral;
//        }else {
//            if (planLiteral.size() != 1) {
//                //对plan有可能的多个子目标进行排列组合
//                ArrayList<Literal[]> combine = ExecutableGoal.combination(planLiteral, 0, null);
//                return combine;
//            }else {
//                return planLiteral;
//            }
//        }
//    }

    //去掉数组中重复的Literal
    public static Literal[] deDuplication(Literal[] srcLiteral){
        ArrayList<Literal> result = new ArrayList<>();
        int num = 0;
        boolean flag;
        for (int i = 0; i < srcLiteral.length; i++) {
            flag = false;
            for (int j = 0; j < result.size(); j++) {
                if (srcLiteral[i].equals(result.get(j))){
                    num++;
                    flag = true;
                    break;
                }
            }
            if (!flag){
                result.add(srcLiteral[i]);
            }
        }

        Literal[] reLiteral = new Literal[result.size()];
        int m = 0;
        for (int j = 0; j < result.size(); j++) {
            reLiteral[m] = result.get(j);
            m++;
        }
        return reLiteral;
    }

    //去掉ArrayList中相同的Literal数组
    public static ArrayList<Literal[]> deDuplication(ArrayList<Literal[]> srcLiteral){
        ArrayList<Literal[]> result = new ArrayList<>();
        boolean flag;
        for (int i = 0; i < srcLiteral.size(); i++) {
            flag = false;
            for (int j = 0; j < result.size(); j++) {
                if (checkArray(srcLiteral.get(i),result.get(j))) {
                    flag = true;
                    break;
                }
            }
            if (!flag){
                result.add(srcLiteral.get(i));
            }
        }

        return result;
    }

    //比较两个Literal数组是否相等
    public static boolean checkArray(Literal[] A,Literal[] B){
        if (A.length != B.length){
            return false;
        }
        int c = 0;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                if (A[i].equals(B[j])){
                    c++;
                }
            }
        }

        if (c == A.length){
            return true;
        }else {
            return false;
        }
    }

}
