package generators;

import com.sun.source.tree.Tree;
import goalplantree.ActionNode;
import goalplantree.GoalNode;
import goalplantree.TreeNode;
import org.apache.commons.math3.ode.events.Action;
import structure.Graph;
import structure.Node;

import xml2bdi.XMLReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadFile {

    public void readFile(String fileName) throws IOException{

        Graph graph = new Graph();
        HashMap map1 = new HashMap();

        //创建初始状态节点
        Node root = new Node();

        ArrayList<GoalNode> tlgs = root.getGoalNodes("F:\\project\\gpt\\1.xml");

        //创建一个新的TreeNode的ArryList，因为currentStep是Tree Node型的， 对 GoalNode进行遍历，强制转成TreeNode型
        ArrayList<TreeNode> currentSteps = new ArrayList<>();
        for (GoalNode tlg : tlgs) {
            currentSteps.add((TreeNode) tlg);
            map1.put(tlg,tlg);

        }
        root.setCurrentStep(map1);
        root.setId(0);
        graph.addNode(root);
        //开始currentNode指向root即根节点
        graph.setCurrentNode(root);


        BufferedReader br = new BufferedReader(new FileReader(fileName));
        //调用字符缓冲输入流对象的方法读数据
        String line;
        int i = 1;
        while ((line = br.readLine()) != null){

            String[] strArray = line.split("-");

            //每读一行创建一个节点
            Node node = new Node(i,strArray[0],strArray[1]);
            HashMap map = new HashMap();

            //把当前节点的map赋值一份，方便让孩子节点在其基础上更新
            map.putAll(graph.getCurrentNode().getCurrentStep());


            GoalNode searchGoalNode = node.searchWhichGoal(tlgs);//找到当前执行的哪棵树
            TreeNode searchActionNode = node.traversal(searchGoalNode, node.getActionName());

            map.put(searchGoalNode,searchActionNode);
            node.setCurrentStep(map);

            graph.addNode(node);

            graph.getCurrentNode().addChildNode(node);

            graph.setCurrentNode(node);

            i++;
        }
        //释放资源
        br.close();

//        writeUml(graph);


        //遍历graph的所有node的Id
        graph.traversalId();
        graph.traversalChildNode();

    }

    //把生成的图写成.txt，生成uml文件
    public void writeUml(Graph graph) throws IOException {

        //把节点和边保存到txt文件中
        //File graphFile = new File("F:\\project\\SQ-MCTS\\genGraph\\graphView.txt");

        FileWriter newFile  = new FileWriter("graphView.txt",true);

        newFile.append("@startuml\n\n")
                .append("digraph ").append("graph1").append(" {\n");

        for(Node node : graph.getNodes()){
            newFile.append(node.getId() + ";");
            newFile.append("\n");
        }

        for(Node node : graph.getNodes()){
            for (Node node1 : node.getChildNode()) {
                newFile.append(node.getId() + "->" + node1.getId() + ";");
                newFile.append("\n");
            }
        }

        newFile.append("}\n")
                .append("\n@enduml\n");
        newFile.close();

    }
}
