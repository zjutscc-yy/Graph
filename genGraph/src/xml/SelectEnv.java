package xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 在总的文件（环境）中随机选取一定数量的文件（环境）生成图
 *
 */
public class SelectEnv {

    public static void main(String[] args) {
        //总的文件夹集合
        List<File> fileList = getFileList("F:\\project\\gpt\\genGraph_3\\ExeEnv");
        //每次选择多少个
        int selectNum;

        selectNum = Integer.parseInt(args[0]);

        //保存选择的文件
        List<File> selectFile = new ArrayList<>();
        //判断该文件是否选择
        boolean needSelectFile = false;
        int m = 0;
        Random rd = new Random();

        while (m < selectNum) {
            for (File file : fileList) {
                needSelectFile = rd.nextDouble() < 0.5;
                if (!selectFile.contains(file)) {
                    m++;
                    selectFile.add(file);
                }

            }
        }
    }

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
