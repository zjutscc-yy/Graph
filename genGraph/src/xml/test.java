package xml;

import goalplantree.GoalNode;
import goalplantree.Literal;
import xml2bdi.XMLReader;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) {

//        ValueCombine c = new ValueCombine(2);
//        c.solution();
//        c.getAlltuples();
//        System.out.println("111");

        String gptFilePath = "F:\\project\\gpt\\1.xml";
        XMLReader reader = new XMLReader(gptFilePath);

        SummaryEnv summaryEnv = new SummaryEnv(gptFilePath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        genAllCase gens = new genAllCase(absolutetEnv);

        ArrayList<GoalNode> tlgs = reader.getTlgs();
        for (GoalNode tlg : tlgs) {
            ArrayList<Literal[]> literals = gens.checkGoal(tlg);
            System.out.println("111");
        }
    }
}
