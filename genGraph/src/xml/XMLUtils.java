package xml;

import goalplantree.Literal;
import xml2bdi.XMLReader;

import java.util.ArrayList;
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
        return tarList;
    }

    ArrayList<Literal> changeLiteralState(boolean[] sourceList,ArrayList<Literal> literals){
        for (int i = 0; i < sourceList.length; i++) {
            //修改literals里面state
        }
        return literals;
    }

}
