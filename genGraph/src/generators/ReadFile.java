package generators;

import structure.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFile {

    public void readFile(String fileName) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        //创建ArrayList对象
        ArrayList<Node> array = new ArrayList<>();
        //调用字符缓冲输入流对象的方法读数据
        String line;
        int i = 0;
        while ((line = br.readLine()) != null){
            //用split分割，得到字符串数组
            String str =  line;
            //把字符串数组中每个元素取出来赋给节点
                //每读到一个action创建节点
                Node node = new Node();
                node.setId(i);
                node.setActionName(str);
                array.add(node);
            i++;
        }
        //释放资源
        br.close();
        //遍历集合
        for (Node node : array) {
            System.out.println(node.getId());
        }
    }

}
