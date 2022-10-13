package xml;

import goalplantree.GoalNode;
import xml2bdi.XMLReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static ArrayList<HashMap<String, String>> goalAchieve;
    public static void main(String[] args) throws IOException {
        ArrayList<ArrayList<Integer>> num = new ArrayList<>();
        ArrayList<Integer> n1= new ArrayList<>();
        n1.add(1);
        n1.add(3);
        n1.add(5);
        ArrayList<Integer> n2= new ArrayList<>();
        n2.add(2);
        n2.add(4);
        n2.add(6);
        ArrayList<Integer> n3= new ArrayList<>();
        n3.add(7);
        n3.add(8);
        n3.add(10);
        num.add(n1);
        num.add(n2);
        num.add(n3);

        // 创建文件保存已存在的环境
//        File envPath1 = new File("F:\\project\\SQ-MCTS\\envs.txt");

        FileWriter envPath  = new FileWriter("envs.txt",true);

        for (ArrayList<Integer> integers : num) {
            for (int i = 0; i < integers.size(); i++) {
                envPath.append(integers.get(i) + " ");
            }
            envPath.append("\n");
        }

        envPath.close();
    }


}
