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
    public static Graph bigGraph;
    public static int ID = 0;

    private int index = 0;
    private boolean isFirstFlag = false;


    public Graph readFile(String fileName) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        //调用字符缓冲输入流对象的方法读数据
        String line;
        /*
        1.readLine 这里的只进行读
        2.创建多个路径List
        3.
         */
        bigGraph = new Graph();
        bigGraph.setInitialState("F:\\project\\gpt\\2.xml");
        while ((line = br.readLine()) != null) {
            Graph singlePathGraph = generatePathGraph(br);
            if (!isFirstFlag)
                index = singlePathGraph.getNodes().size();
            isFirstFlag = true;
            bigGraph = mergeGraph(bigGraph, singlePathGraph);
        }
        br.close();

        writeUml(bigGraph);

        return bigGraph;
    }


    //两个图合成一个图
    public Graph mergeGraph(Graph graph,Graph pathGraph){
        if (graph.getNodes().size() == 0){
            return pathGraph;
        }
        boolean isAdd = false;//判断curGraphPtr是否为新加节点

        Node curGraphPtr = graph.getRoot();
        Node curPathPtr = pathGraph.getRoot().getChildNode().get(0);

        out: while (curPathPtr.getChildNode().size() != 0) {

            ArrayList<Node> curNodes = graph.getNodes();
            inner: for (int i = 0; i < curNodes.size(); i++) {
                Node graphNode = curNodes.get(i);
                if (graphNode.equals(curPathPtr)){
                    if (!isAdd){//curGraphPtr在图里，curPathPtr在图里
                        if (!curGraphPtr.getChildNode().contains(graphNode)) {
                            curGraphPtr.addChildNode(graphNode);//可能会加重复，连着都不等，加个判断孩子节点里是否已经存在curPathPtr
                        }
                    }else{
                        curGraphPtr.addChildNode(graphNode);//这有问题
                        curGraphPtr.removeChildNode(curPathPtr);
                    }
                    curGraphPtr = graphNode;
                    if (curPathPtr.getChildNode().size() != 0){
                        curPathPtr = curPathPtr.getChildNode().get(0);
                    }
                    isAdd = false;

                    continue out;
                }

            }
            //找不到
            if (!isAdd) {//curGraphPtr在图里
                curGraphPtr.addChildNode(curPathPtr);//可能会加重复，连着都不等，加个判断孩子节点里是否已经存在curPathPtr
            }
            graph.addNode(curPathPtr);
            curGraphPtr = curPathPtr;
            if (curPathPtr.getChildNode().size() != 0) {
                curPathPtr = curPathPtr.getChildNode().get(0);
            }
            isAdd = true;
        }

        //curPathPtr指向最后一个节点，最后一个节点一定已经在图中
        ArrayList<Node> curNodes = graph.getNodes();
        for (int i = 0; i < curNodes.size(); i++) {
            Node graphNode = curNodes.get(i);
            if(graphNode.equals(curPathPtr)){
                if (!isAdd) {
                    if (!curGraphPtr.getChildNode().contains(graphNode)) {
                        curGraphPtr.addChildNode(graphNode);//可能会加重复，连着都不等，加个判断孩子节点里是否已经存在curPathPtr
                    }
                }else {
                    curGraphPtr.addChildNode(graphNode);
                    curGraphPtr.removeChildNode(curPathPtr);
                    break;
                }
            }
        }

        return graph;
    }

    //生成单条路径
    public Graph generatePathGraph(BufferedReader br) throws IOException {

        Graph pathGraph = new Graph();
        pathGraph.initialState = bigGraph.initialState;
        //每条路径都添加一个root节点
        Node root = new Node();

        HashMap map1 = new HashMap();

        ArrayList<GoalNode> tlgs = pathGraph.getInitialState();

        //创建一个新的TreeNode的ArryList，因为currentStep是Tree Node型的， 对GoalNode进行遍历，强制转成TreeNode型
        ArrayList<TreeNode> currentSteps = new ArrayList<>();
        for (GoalNode tlg : tlgs) {
            currentSteps.add((TreeNode) tlg);
            map1.put(tlg, tlg);

        }
        root.setCurrentStep(map1);
        root.setId(ID++);
        pathGraph.setCurrentNode(root);
        pathGraph.setRoot(root);
        pathGraph.addNode(root);

        String data;
        while (!("//".equals(data = br.readLine())) && data != null && !data.equals("")) {

            String[] strArray = data.split("-");

            Node node = new Node(ID++, strArray[0], strArray[1]);
            HashMap map = new HashMap();

            //把当前节点的map赋值一份，方便让孩子节点在其基础上更新
            map.putAll(pathGraph.getCurrentNode().getCurrentStep());

            GoalNode searchGoalNode = node.searchWhichGoal(tlgs);//找到当前执行的哪棵树
            TreeNode searchActionNode = node.traversal(searchGoalNode, node.getActionName());

            map.put(searchGoalNode, searchActionNode);
            node.setCurrentStep(map);


            pathGraph.addNode(node);

            pathGraph.getCurrentNode().addChildNode(node);

            pathGraph.setCurrentNode(node);

        }
        return pathGraph;
    }

    //把生成的图写成.txt，生成uml文件
    public void writeUml(Graph graph) throws IOException {

        //把节点和边保存到txt文件中
        //File graphFile = new File("F:\\project\\SQ-MCTS\\genGraph\\graphView.txt");

        FileWriter newFile = new FileWriter("graphView.txt", true);

        newFile.append("@startuml\n\n")
                .append("digraph ").append("graph1").append(" {\n");

        for (Node node : graph.getNodes()) {
            newFile.append(node.getId() + ";");
            newFile.append("\n");
        }

        for (Node node : graph.getNodes()) {
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
