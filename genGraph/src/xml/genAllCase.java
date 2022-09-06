package xml;

import goalplantree.GoalNode;
import goalplantree.Literal;
import goalplantree.PlanNode;
import goalplantree.TreeNode;

import java.util.*;

//总结一个目标所有可能的情况

public class genAllCase {

    private ArrayList<String> envs;

    public genAllCase(ArrayList<String> envs) {
        this.envs = envs;
    }

    public ArrayList<HashMap<String, String>> checkGoal(GoalNode goalNode) {
        //要返回的最终结果
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        //TODO 只考虑了每个目标只有两个计划的情况
        //每次选取
        PlanNode[] plans = goalNode.getPlans();

        ArrayList<HashMap<String, String>> p1 = checkPlan(plans[0]);
        ArrayList<HashMap<String, String>> p2 = checkPlan(plans[1]);
        if (p1.equals(p2)){
            return p1;
        }
        int m = 0;
        //第一个计划的一种可能
        for (HashMap<String, String> p11 : p1) {
            m++;
            //获取该可能的所有key值
            Set<String> keySet = p11.keySet();
            int n = 0;
            //第二个计划的一种可能
            for (HashMap<String, String> p22 : p2) {
                n++;
                //保存第二个计划中不在第一个计划中的Literal名字
                ArrayList<String> needEdit = new ArrayList<>();
                //获取需要修改的literal的名字
                for (Map.Entry<String, String> entry : p22.entrySet()) {
                    if (!keySet.contains(entry.getKey())){
                        needEdit.add(entry.getKey());
                    }
                }
                if (needEdit.size() != 0) {
                    int p = 0;
                    //得到第二个计划中,与第一个计划中的第一种可能合并
                    for (HashMap<String, String> genValue : genValues(needEdit)) {
                        p++;
                        //保存每种组合
                        HashMap<String, String> everResult = new HashMap<>();
                        everResult.putAll(p11);
                        everResult.putAll(genValue);
                        if (!result.contains(everResult)) {
                            result.add(everResult);
                        }
                    }
                }else {
                    HashMap<String, String> everResult = new HashMap<>();
                    everResult.putAll(p11);
                    if (!result.contains(everResult)) {
                        result.add(everResult);
                    }
                }
            }
        }
        int k = 0;
        //第二个计划的一种可能
        for (HashMap<String, String> p22 : p2) {
            k++;
            //获取该可能的所有key值
            Set<String> keySet = p22.keySet();
            int l = 0;
            //第二个计划的一种可能
            for (HashMap<String, String> p11 : p1) {
                l++;
                //保存第二个计划中不在第一个计划中的Literal名字
                ArrayList<String> needEdit = new ArrayList<>();
                //获取需要修改的literal的名字
                for (Map.Entry<String, String> entry : p11.entrySet()) {
                    if (!keySet.contains(entry.getKey())){
                        needEdit.add(entry.getKey());
                    }
                }
                if (needEdit.size() != 0) {
                    int p = 0;
                    //得到第二个计划中,与第一个计划中的第一种可能合并
                    for (HashMap<String, String> genValue : genValues(needEdit)) {
                        p++;
                        //保存每种组合
                        HashMap<String, String> everResult = new HashMap<>();
                        everResult.putAll(p22);
                        everResult.putAll(genValue);
                        if (!result.contains(everResult)) {
                            result.add(everResult);
                        }
                    }
                }else {
                    HashMap<String, String> everResult = new HashMap<>();
                    everResult.putAll(p22);
                    if (!result.contains(everResult)) {
                        result.add(everResult);
                    }
                }
            }
        }
        System.out.println("检查完" + goalNode.getName());

        return result;
    }

    public ArrayList<HashMap<String, String>> checkPlan(PlanNode plan) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        //用于存储该plan节点下goal的集合
        ArrayList<HashMap<String, String>> planLiteral = new ArrayList<>();
        TreeNode[] planbody = plan.getPlanbody();
        //用于判断plan孩子节点是goal类型的有几个，若没有返回该plan的pre-，若是多个，则要进行排列组合
        int i = 0;
        Literal[] prec = plan.getPrec();
        for (TreeNode treeNode : planbody) {//遍历该节点的planbody
            //当plan的孩子节点是Goal的时候
            if (treeNode instanceof GoalNode) {
                i++;
                GoalNode goal = (GoalNode) treeNode;
                //先获取goal的Literal[],再进行排列组合，最后再加入当前plan的pre-
                planLiteral = checkGoal(goal);
                //TODO 只考虑了每个计划只有一个子目标的情况
            }
        }

        //当前计划的前置条件的hashmap
        HashMap<String, String> planPre = new HashMap<>();
        for (Literal literal : prec) {
            if(envs.contains(literal.getName())) {
                planPre.put(literal.getName(), literal.toStateString());
            }
        }

        if (i == 0) {
            //说明该计划没有孩子节点为目标，所以只需将该计划的pre加入
            planLiteral.add(planPre);
            return planLiteral;
        } else {
            //说明该计划有孩子节点为目标,把该计划的pre加到每个hashmap（也就是每种可能）的后面
            for (HashMap<String, String> enableTuple : planLiteral) {
                if (checkMap(planPre,enableTuple)){
                    enableTuple.putAll(planPre);
                    result.add(enableTuple);
                }
            }
            return result;
        }
    }

    /**
     * 假设checkGoal结果为 （50F、36F） （50F、36T） （50T、36F）
     * 该目标的上一层计划的前置条件为36F ,所以上面第二种可能必须删除
     * 判断checkGoal的结果中有无与plan的pre冲突的，若有，则删除
     */

    //获取所有可能的真值的排列组合
    public static ArrayList<HashMap<String, String>> genValues(ArrayList<String> A) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        ValueCombine c = new ValueCombine(A.size());
        c.solution();
        for (boolean[] booleans : c.getAlltuples()) {
            HashMap<String, String> tupleSet = new HashMap<>();
            for (int i = 0; i < booleans.length; i++) {
                tupleSet.put(A.get(i),getString(booleans[i]));
            }
            result.add(tupleSet);
        }
        return result;
    }

    public static String getString(boolean A){
        if (A == true){
            return "true";
        }
        return "false";
    }


    public static Literal[] listToArray(ArrayList<Literal> A) {
        Literal[] B = new Literal[A.size()];
        for (int i = 0; i < A.size(); i++) {
            B[i] = A.get(i);
        }
        return B;

    }

    //合并两个目标的可能
    public static ArrayList<HashMap<String, String>> mergeGoal(ArrayList<HashMap<String, String>> A,ArrayList<HashMap<String, String>> B){
        if (A.size() == 0){
            return B;
        }
        if (B.size() == 0){
            return A;
        }
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        //获得A所有的haspMap
        for (HashMap<String, String> aHashMap : A) {
            //获得B所有的haspMap
            for (HashMap<String, String> bHashMap : B) {
                HashMap<String, String> and = new HashMap<>();
                if (checkMap(aHashMap,bHashMap)) {
                    and.putAll(aHashMap);
                    and.putAll(bHashMap);
                    result.add(and);
                }
            }
        }
        return result;
    }
    
    //判断两个hashmap是否含有相反环境
    public static boolean checkMap(HashMap<String, String> A,HashMap<String, String>B){
        //只有两个key对应的value相等或key值并不等，才会加入
        for (Map.Entry<String, String> aEntry : A.entrySet()) {
            String key = aEntry.getKey();
            String value = aEntry.getValue();
            //有相反环境
            if (B.get(key) != null && !B.get(key).equals(value)){
                return false;
            }
//            for (Map.Entry<String, String> bEntry : B.entrySet()) {
//                //说明有相反的环境，删除
//                if (!bEntry.getKey().equals(value)){
//                    return false;
//                }
//            }
        }
        return true;
    }
}