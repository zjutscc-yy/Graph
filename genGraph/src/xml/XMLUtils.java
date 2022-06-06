package xml;

import goalplantree.Literal;
import xml2bdi.XMLReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 这里的几个方法，因为修改了文件读写的逻辑，这里的方法得重新写，或者改改

public class XMLUtils {
    /**
     * 获取状态
     *
     * @return
     */
    boolean[] getBooleanList(ArrayList<Literal> literals) {
        //读文件
        boolean[] resultBooleanList = new boolean[literals.size()];
        for (int i = 0; i < literals.size(); i++) {
            resultBooleanList[i] = literals.get(i).getState();
        }
        return resultBooleanList;
    }

    boolean[] changeBooleanListState(boolean[] sourceList, double percent) {
        boolean[] tarList = sourceList;
        for (int i = 0; i < tarList.length; i++) {
            if (new Random().nextDouble() < percent)
                tarList[i] = !tarList[i];
        }
        return tarList;
    }

    ArrayList<Literal> changeLiteralState(boolean[] sourceList,ArrayList<Literal> literals){
        for (int i = 0; i < sourceList.length; i++) {
            //修改literals里面state
            literals.get(i).setState(sourceList[i]);
        }
        return literals;
    }

}
