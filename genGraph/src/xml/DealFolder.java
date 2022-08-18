package xml;

import goalplantree.GoalNode;
import goalplantree.Literal;
import xml2bdi.XMLReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 判断文件夹中那些文件符合要求，将不符合的删掉
 *
 */
public class DealFolder {
    public static void main(String[] args) {
        ArrayList<ArrayList<Literal[]>> allGoalExe = new ArrayList<>();
        //获取保证每个目标可实现的集合
        ExecutableGoal executableGoal = new ExecutableGoal();
        XMLReader reader = new XMLReader("F:\\project\\gpt\\5.xml");
        ArrayList<GoalNode> tlgs = reader.getTlgs();
        //获取所有tlg可能集合
        for (GoalNode tlg : tlgs) {
            ArrayList<Literal[]> literals = executableGoal.checkGoal(tlg);
            allGoalExe.add(literals);
        }

        List<File> fileList = getFileList("F:\\project\\gpt\\genGraph_5_0.3");
        for (int i = 0; i < fileList.size(); i++) {
            for (ArrayList<Literal[]> singleGoal : allGoalExe) {
                //checkFile为false，说明当前目标在该文件下没有可以实现的组合,删掉该文件
                if (!checkFile(fileList.get(i).getPath(),singleGoal)){
                    //删掉该文件
                    fileList.get(i).delete();
                    break;
                }

            }
        }

    }

    /**
     * @param gptPath
     * @param literals 当前目标可执行的集合
     * @return
     */
    public static boolean checkFile(String gptPath,ArrayList<Literal[]> literals){
        XMLReader reader = new XMLReader(gptPath);
        //每种可能组合
        for (int i = 0; i < literals.size(); i++) {
            //记录每种组合中相等的元素
            int num = 0;
            //获取一个目标的可能组合 并 遍历一种可能性的每个元素
            for (Literal literal : literals.get(i)) {
                //gpt中的环境
                for (Literal env : reader.getLiterals()) {
                    if (literal.equals(env)){
                        num++;
                    }
                }
            }
            //遍历完每个组合后，判断相等的个数是否等于组合中元素个数
            if (num == literals.get(i).length){
                return true;
            }
        }
        return false;
    }
    /**
     * 获取文件夹下的文件列表
     *
     * @param dirStr 文件夹的路径
     * @return
     */
    public static List<File> getFileList(String dirStr) {
//        File dir = new File(dirStr);
//        if (!dir.exists()){
//            System.out.println("目录不存在");
//        }

        //if istxt
        File file = new File(dirStr);
        List<File> sourceList = Arrays.stream(file.listFiles()).toList();
        List<File> resultList = new ArrayList<>();

        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i).isFile()) {
                if (sourceList.get(i).getName().contains("txt")) {
                    System.out.println(sourceList.get(i).getName());
                } else {
                    resultList.add(sourceList.get(i));
                }
            }
        }
        return resultList;
    }

}


