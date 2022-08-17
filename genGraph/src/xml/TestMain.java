package xml;

import goalplantree.GoalNode;
import goalplantree.Literal;
import xml2bdi.XMLReader;

import java.util.ArrayList;

public class TestMain {
    public static void main(String[] args) {
        ExecutableGoal executableGoal = new ExecutableGoal();
        XMLReader reader = new XMLReader("F:\\project\\gpt\\te.xml");
        ArrayList<GoalNode> tlgs = reader.getTlgs();
        for (GoalNode tlg : tlgs) {
            ArrayList<Literal[]> literals = executableGoal.checkGoal(tlg);
            System.out.println("1111");
        }

    }
}
