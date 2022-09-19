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
            System.out.println("检查完" + goalNode.getName());
            return p1;
        }

        //获取第一个计划的所有key值：因为计划P1里的key都是一样的，只是真假值的区别
        Set<String> p1KeySet = p1.get(0).keySet();
        //获取第二个计划的所有key值：因为计划P2里的key都是一样的，只是真假值的区别
        Set<String> p2KeySet = p2.get(0).keySet();

        //保存不在第一个计划里的Literal名字
        ArrayList<String> needEdit1 = new ArrayList<>();
        for (Map.Entry<String, String> p22Entry : p2.get(0).entrySet()) {
            if (!p1KeySet.contains(p22Entry.getKey())){
                needEdit1.add(p22Entry.getKey());
            }
        }

        //保存不在第二个计划里的Literal名字
        ArrayList<String> needEdit2 = new ArrayList<>();
        for (Map.Entry<String, String> p11Entry : p1.get(0).entrySet()) {
            if (!p2KeySet.contains(p11Entry.getKey())){
                needEdit2.add(p11Entry.getKey());
            }
        }

        if (needEdit1.size() == 0 && needEdit2.size() == 0){
            for (HashMap<String, String> p11 : p1) {
                if (!result.contains(p11)) {
                    result.add(p11);
                }
            }
            for (HashMap<String, String> p22 : p2) {
                if (!result.contains(p22)){
                    result.add(p22);
                }
            }
            System.out.println("检查完" + goalNode.getName());
            return result;
        }

        if (needEdit1.size() == 0 && needEdit2.size() != 0){
            ArrayList<HashMap<String, String>> hashMaps2 = genValues(needEdit2);
            for (HashMap<String, String> p11 : p1) {
                if (!result.contains(p11)) {
                    result.add(p11);
                }
            }
            for (HashMap<String, String> p22 : p2) {
                for (int i = 0; i < hashMaps2.size(); i++) {
                    //保存每种组合
                    HashMap<String, String> everResult = new HashMap<>();
                    everResult.putAll(p22);
                    everResult.putAll(hashMaps2.get(i));
                    if (!result.contains(everResult)) {
                        result.add(everResult);
                    }
                }
            }
            System.out.println("检查完" + goalNode.getName());
            return result;
        }

        if (needEdit1.size() != 0 && needEdit2.size() == 0){
            ArrayList<HashMap<String, String>> hashMaps1 = genValues(needEdit1);
            for (HashMap<String, String> p22 : p2) {
                if (!result.contains(p22)) {
                    result.add(p22);
                }
            }
            for (HashMap<String, String> p11 : p1) {
                for (int i = 0; i < hashMaps1.size(); i++) {
                    //保存每种组合
                    HashMap<String, String> everResult = new HashMap<>();
                    everResult.putAll(p11);
                    everResult.putAll(hashMaps1.get(i));
                    if (!result.contains(everResult)) {
                        result.add(everResult);
                    }
                }
            }
            System.out.println("检查完" + goalNode.getName());
            return result;
        }

        ArrayList<HashMap<String, String>> hashMaps1 = genValues(needEdit1);
        ArrayList<HashMap<String, String>> hashMaps2 = genValues(needEdit2);

        int m = 0;
        //第一个计划的一种可能
        for (HashMap<String, String> p11 : p1) {
            m++;
            if (needEdit1.size() != 0) {
                //得到第二个计划中,与第一个计划中的第一种可能合并
                for (int i = 0; i < hashMaps1.size(); i++) {
                    //保存每种组合
                    HashMap<String, String> everResult = new HashMap<>();
                    everResult.putAll(p11);
                    everResult.putAll(hashMaps1.get(i));
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

        int k = 0;
        //第二个计划的一种可能
        for (HashMap<String, String> p22 : p2) {
            k++;
            if (needEdit2.size() != 0) {
                //得到第二个计划中,与第一个计划中的第一种可能合并
                for (int i = 0; i < hashMaps2.size(); i++) {
                    //保存每种组合
                    HashMap<String, String> everResult = new HashMap<>();
                    everResult.putAll(p22);
                    everResult.putAll(hashMaps2.get(i));
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
//            System.out.println("检查完" + plan.getName());
            return planLiteral;
        } else {
            //说明该计划有孩子节点为目标,把该计划的pre加到每个hashmap（也就是每种可能）的后面
            for (HashMap<String, String> enableTuple : planLiteral) {
                if (checkMap(planPre,enableTuple)){
                    enableTuple.putAll(planPre);
                    result.add(enableTuple);
                }
            }
//            System.out.println("检查完" + plan.getName());
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

    public static ArrayList<HashMap<String, String>> mergeGoal(ArrayList<HashMap<String, String>> A,ArrayList<HashMap<String, String>> B){
        if (A.size() == 0){
            return B;
        }
        if (B.size() == 0){
            return A;
        }
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        //获得A所有的haspMap
        int i = 0;
        for (HashMap<String, String> aHashMap : A) {
            i++;
            //每次把A的hashmap保存起来
//            HashMap<String, String> retainA = new HashMap<>(aHashMap);
            //获得B所有的haspMap
            int j = 0;
            for (HashMap<String, String> bHashMap : B) {
                j++;
                if (checkMap(aHashMap,bHashMap)) {
                    HashMap<String,String> retainA = new HashMap<>();
                    retainA.putAll(aHashMap);
                    retainA.putAll(bHashMap);
                    result.add(retainA);
//                    aHashMap = retainA;
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
        }
        return true;
    }
}