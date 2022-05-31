package xml;

public class Main {
    public static void main(String[] args) {
        XMLUtils xmlUtils = new XMLUtils();
        xmlUtils.sourceUrl = "F:\\project\\gpt\\2.xml";

        boolean[] envList = xmlUtils.getBooleanList();
        xmlUtils.changeBooleanListState(envList);

    }
}
