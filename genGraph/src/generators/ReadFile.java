package generators;

import com.sun.source.tree.Tree;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.TreeNode;
import org.apache.commons.math3.ode.events.Action;
import structure.Node;

import xml2bdi.XMLReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadFile {

    public void readFile(String fileName) throws IOException{

        HashMap map1 = new HashMap();
        //创建ArrayList对象存储Node节点
        ArrayList<Node> array = new ArrayList<>();

        //创建初始节点
        Node root = new Node();

        ArrayList<GoalNode> tlgs = root.getGoalNodes("F:\\project\\gpt\\1.xml");

        //创建一个新的TreeNode的ArryList，因为currentStep是Tree Node型的， 对 GoalNode进行遍历，强制转成TreeNode型
        ArrayList<TreeNode> currentSteps = new ArrayList<>();
        for (GoalNode tlg : tlgs) {
            currentSteps.add((TreeNode) tlg);
            map1.put(tlg,tlg);

        }
        root.setCurrentStep(map1);
        array.add(root);


        BufferedReader br = new BufferedReader(new FileReader(fileName));
        //调用字符缓冲输入流对象的方法读数据
        String line;
        int i = 0;
        while ((line = br.readLine()) != null){

            String[] strArray = line.split("-");
            //每读一行创建一个节点
            Node node = new Node();
            HashMap map = new HashMap();
            node.addNode(i, strArray);

            GoalNode searchGoalNode = node.searchWhichGoal(tlgs);//找到当前执行的哪棵树
            TreeNode searchActionNode = node.traversal(searchGoalNode, node.getActionName());

            map.put(searchGoalNode,searchActionNode);
            node.setCurrentStep(map);

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
