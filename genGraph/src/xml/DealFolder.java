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
 * 树的路径 config
 * 文件夹路径 main
 *
 */
public class DealFolder {
    public static void main(String[] args) {
        //树的存储路径
        String gptFilePath;
        if (args.length == 0) {
            System.out.println("ERROR: no GPT file specified!");
            return;
        }
        gptFilePath = args[0];
        //获取到完全来自于环境的环境变量
        SummaryEnv summaryEnv = new SummaryEnv(gptFilePath);
        ArrayList<String> absolutetEnv = summaryEnv.checkAbsolutetEnvName();

        ArrayList<ArrayList<Literal[]>> allGoalExe = new ArrayList<>();
        //获取保证每个目标可实现的集合
        ExecutableGoal executableGoal = new ExecutableGoal(absolutetEnv);
        XMLReader reader = new XMLReader(gptFilePath);
        ArrayList<GoalNode> tlgs = reader.getTlgs();
        //获取所有tlg可能集合
        for (GoalNode tlg : tlgs) {
            ArrayList<Literal[]> literals = executableGoal.checkGoal(tlg);
            allGoalExe.add(literals);
        }
//        Graph_5_0.3
        List<File> fileList = getFileList("F:\\project\\gpt\\8\\8test");
        //遍历每个文件
        for (int i = 0; i < fileList.size(); i++) {
            int m = 0;
            //检查每个文件是否符合所有goal
            for (ArrayList<Literal[]> singleGoal : allGoalExe) {
                //checkFile为false，说明当前目标在该文件下没有可以实现的组合,删掉该文件
                if (!checkFile(fileList.get(i).getPath(),singleGoal,absolutetEnv)){
                    m++;
                    //删掉该文件
//                    fileList.get(i).delete();
//                    break;
                }

            }
            //删除一个目标都不能实现的环境
            if (m == tlgs.size()){
                fileList.get(i).delete();
            }
        }

    }

    /**
     * @param gptPath
     * @param literals 当前目标可执行的集合
     * @return
     */
    public static boolean checkFile(String gptPath,ArrayList<Literal[]> literals,ArrayList<String> absoluteEnv){
        XMLReader reader = new XMLReader(gptPath);
        //遍历每种可能性
        for (Literal[] possibleLiterals : literals) {
            //用于统计完全来自于环境中的变量与每种可能性中名字相同的交集的个数
            int nameNum = 0;
            int valueNum = 0;
            //遍历每种可能性里包含的元素
            for (Literal literal : possibleLiterals) {
                if (absoluteEnv.contains(literal.getName())){
                    nameNum++;
                    for (Literal readerLiteral : reader.getLiterals()) {
                        if (readerLiteral.equals(literal)) {
                            valueNum++;
                        }

                    }
                }
            }
            if (nameNum == valueNum){
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


