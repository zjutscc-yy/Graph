package xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 在总的文件（环境）中随机选取一定数量的文件（环境）生成图
 *
 * 需要修改的参数：
 * 1.总的可执行文件集合
 * 2.选择过的文件集合
 * 3.新文件夹路径：存放每次所选文件
 * 以上在main中修改
 *
 * 每次选择个数 config
 *
 */
public class SelectEnv {

    public static void main(String[] args) throws Exception {
        double rate;
        //每次选择多少个
        int selectNum;

        //总的文件夹集合
        List<File> allFile = getFileList("F:\\project\\gpt\\Atest\\test");
        //选择过的文件集合
//        List<File> selectedFiles = getFileList("F:\\project\\gpt\\genGraph_3\\SelectedEnv");

        List<String> selectedFileName = new ArrayList<>();
//        for (File selectedFile : selectedFiles) {
//            selectedFileName.add(selectedFile.getName());
//        }

        //新文件夹路径：用来存放每次选择的文件
//        String newFilePath = "F:\\project\\gpt\\genGraph_3\\newSelect";

        String newFilePath = "F:\\project\\gpt\\Atest\\test50";

        selectNum = Integer.parseInt(args[0]);

//        rate = selectNum / (double) (allFile.size()-selectedFiles.size());
        rate = selectNum / (double) allFile.size();

        //判断该文件是否选择
        boolean needSelectFile = false;
        int m = 0;
        Random rd = new Random();

        //创建新的文件夹
//        File newFile = new File(newFilePath);
//        newFile.mkdir();

        //为了确保选过的不再选
//        List<File> thisSelect = new ArrayList<>();

        while (m < selectNum) {
            for (File file : allFile) {
                //随机挑选文件
                needSelectFile = rd.nextDouble() < rate;
                if (needSelectFile && !selectedFileName.contains(file.getName()) && m < selectNum) {
                    m++;
//                    thisSelect.add(file);
//                    selectedFiles.add(file);
                    selectedFileName.add(file.getName());
                    //把选择的文件复制到刚创建的文件夹中
                    copyFile(file.getAbsolutePath(),newFilePath+"\\"+file.getName());
                    //把选择的文件复制到SelectedEnv文件夹下
//                    copyFile(file.getAbsolutePath(),"F:\\project\\gpt\\genGraph_3\\SelectedEnv"+"\\"+file.getName());
                    if (m == selectNum){
                        break;
                    }
                }

            }
        }
    }

    public static void copyFile(String source,String dest) throws Exception{
        FileInputStream in = new FileInputStream(new File(source));
        FileOutputStream out = new FileOutputStream(new File(dest));
        byte[] buff = new byte[512];
        int n = 0;
        while ((n = in.read(buff)) != -1){
            out.write(buff,0,n);
        }
        out.flush();
        in.close();
        out.close();
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
