package xml;

import goalplantree.Literal;
import xml2bdi.XMLReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class XMLUtils {
    String str = "";
    String sourceUrl = "";


    /**
     * 获取状态
     *
     * @return
     */
    boolean[] getBooleanList() {
        //读文件
        XMLReader xmlReader = new XMLReader(sourceUrl);
        ArrayList<Literal> literals = xmlReader.getLiterals();
        boolean[] resultBooleanList = new boolean[literals.size()];
        for (int i = 0; i < literals.size(); i++) {
            resultBooleanList[i] = literals.get(i).getState();
        }
        return resultBooleanList;
    }

    boolean[] changeBooleanListState(boolean[] sourceList) {
        boolean[] tarList = sourceList;
        for (int i = 0; i < tarList.length; i++) {
            if (new Random().nextDouble() < 0.3)
                tarList[i] = !tarList[i];
        }
        /**
         * int[1-100]
         * 3,26,67 ... total = 30
         *
         * a 0.3    b 0.5   c 0.2
         */
        Random rd = new Random();
        List<Integer> testList = new ArrayList<>();
        while (true){
            int temp = (int) (rd.nextDouble()*100);
            if (!testList.contains(temp))
                testList.add(temp);
            if (testList.size()>=30)
                break;
        }
        /**
         * ----------------------
         * a 0.3    b 0.5   c 0.2
         */
        double a = 0.156;
        double b = a+0.556;
        double c = 1-a-b;
        double temp_test = rd.nextDouble();
        if (temp_test<=a) {
            //a 的范围
            System.out.println(a);
        }else{
            if (temp_test<=b)
                //b 的范围
                System.out.println(b);
            else{
                System.out.println(c);
            }
        }
        return tarList;
    }

    ArrayList<Literal> changeLiteralState(boolean[] sourceList,ArrayList<Literal> literals){
        for (int i = 0; i < sourceList.length; i++) {
            //修改literals里面state
        }
        return literals;
    }

}
