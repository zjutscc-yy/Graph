package xml;

import java.util.ArrayList;

public class ValueCombine {

    private ArrayList<boolean[]> alltuples = new ArrayList<>();
    private boolean[] combination ;
    private long count;
    private boolean endflag;

    public ValueCombine(int n){
        if (n <= 0)
            throw new IllegalArgumentException("参数必须为正整数");
        if (combination == null) {
            combination = new boolean[n];
            count = 0;
            endflag = false;
        }
    }

    public ArrayList<boolean[]> getAlltuples() {
        return alltuples;
    }

    /**
     * 求解问题，打印所有的真值组合结果。
     *
     */
    public void solution()
    {
        System.out.println("n = " + combination.length + " ***** 所有真值组合： ");
        do {
            System.out.println(getOneTuple());
            count++;
            increOne();
        } while(!terminate());
        System.out.println("真值组合数： " + count);
    }


    /**
     * 逐次加一，生成每一个真值元组
     *
     */
    private void increOne()
    {
        int i;
        for (i=0; i < combination.length; i++) {
            // 若为 0 ，则置 1 ， 结束。
            if (combination[i] == false) {
                combination[i] = true;
                break;
            }
            else {
                // 若为 1， 则置 0， 并通过 i++ 转至次低位进行相同处理
                combination[i] = false;
            }
        }
        // 由 1..1 -> 0..0 时, 设置 endflag = true;
        if (i == combination.length) { endflag = true; }
    }

    /**
     *  根据整数数组表示生成的真值元组，转化为布尔数组表示生成的真值元组。
     *
     */
    private String getOneTuple()
    {
        boolean[] thistuple = new boolean[combination.length];
        StringBuilder tuple = new StringBuilder("(");
        for (int i=0; i < combination.length; i++) {
            thistuple[i] = combination[i];
            tuple.append(combination[i]);
            tuple.append(",");
        }
        alltuples.add(thistuple);
        // 删除 多余的 逗号
        tuple.deleteCharAt(tuple.length()-1);
        tuple.append(")");
        return tuple.toString();
    }

    /**
     * 终止条件： 结束标识符 endflag = true;
     *
     */
    private boolean terminate()
    {
        return endflag == true;
    }
}
