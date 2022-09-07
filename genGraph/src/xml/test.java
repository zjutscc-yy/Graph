package xml;

import goalplantree.GoalNode;
import xml2bdi.XMLReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static ArrayList<HashMap<String, String>> goalAchieve;
    public static void main(String[] args) {

//        HashMap<String,String> A = new HashMap<>();
//        HashMap<String,String> B = new HashMap<>();
//
//        A.put("1","false");
//        A.put("2","false");
//        A.put("3","true");
//
//        B.put("4","true");
//        B.put("1","false");
//        B.put("3","false");
//
//        boolean b = genAllCase.checkMap(A, B);
//        ValueCombine c = new ValueCombine(2);
//        c.solution();
//        c.getAlltuples();


        String gptFilePath = "F:\\project\\gpt\\1.3.xml";
        XMLReader reader = new XMLReader(gptFilePath);

        SummaryEnv summaryEnv = new SummaryEnv(gptFilePath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        genAllCase gens = new genAllCase(absolutetEnv);

        //保存所有目标的可能计划
        goalAchieve = new ArrayList<>();
        ArrayList<GoalNode> tlgs = reader.getTlgs();

        for (GoalNode tlg : tlgs) {
            ArrayList<HashMap<String, String>> hashMaps = gens.checkGoal(tlg);
            goalAchieve = genAllCase.mergeGoal(goalAchieve,hashMaps);
            System.out.println("1111");
        }
        System.out.println("没有问题");
    }


}
