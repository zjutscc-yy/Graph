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

    private int index = 0;
    private boolean isFirstFlag = false;

    public void readFile(String fileName) throws IOException {

        Graph graph = new Graph();
        graph.setInitialState("F:\\project\\gpt\\1.xml");

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        //调用字符缓冲输入流对象的方法读数据
        String line;
        /*
        1.readLine 这里的只进行读
        2.创建多个路径List
        3.
         */
        Node graphRoot = null;
        while ((line = br.readLine()) != null) {
            Node root = generateSinglePath(graph, br);
            if (!isFirstFlag)
                index = graph.getNodes().size();
            isFirstFlag = true;
            graphRoot = mergePath(graphRoot, root);
        }
        br.close();

        writeUml(graph);


        //遍历graph的所有node的Id
        graph.traversalId();
        graph.traversalChildNode();

    }


    //已有图的根节点与新生成path的根节点，合并在一起，最后返回图的根节点
    public Node mergePath(Node graphRoot, Node pathRoot) {
        if (graphRoot == null) {
            return pathRoot;
        }
        Node curGraphPtr = graphRoot;
        Node curPathPtr = pathRoot;

        if (curPathPtr.getChildNode() != null) {
            for (Node graphChildNode : curGraphPtr.getChildNode()) {
                Node pathChildNode = curPathPtr.getChildNode().get(0);
                if (graphChildNode.equals(pathChildNode)) {
                        curGraphPtr = graphChildNode;
                        curPathPtr = pathChildNode;
                    }else {
                        //每次add修改id
                        pathChildNode.setId(++index);
                        graphChildNode.addChildNode(pathChildNode);
                        curGraphPtr = graphChildNode;
                        curPathPtr = pathChildNode;
                    }

            }
        }
        return graphRoot;
    }


//    public Graph mergeGraph(Graph graph,Graph singlePathGraph){
//        graph.setCurrentNode(graph.getRoot());
//        Node point = graph.getCurrentNode();
//
//        singlePathGraph.setCurrentNode(graph.getRoot());
//        Node pointPath = singlePathGraph.getCurrentNode();
//
//        while (pointPath.getChildNode() != null){
//            for (Node node : point.getChildNode()) {
//                for (Node node1 : pointPath.getChildNode()) {
//                   if (node.getCurrentStep().equals(node1.getCurrentStep())){
//                       point = node;
//                       pointPath = node1;
//                   }else {
//                       point.addChildNode(node1);
//                       point = node;
//                       pointPath = node1;
//                   }
//                }
//            }
//        }
//
//        return graph;
//    }

    //生成单条路径
    public Node generateSinglePath(Graph graph, BufferedReader br) throws IOException {


        //每条路径都添加一个root节点
        Node root = new Node();

        HashMap map1 = new HashMap();

        ArrayList<GoalNode> tlgs = root.getGoalNodes("F:\\project\\gpt\\1.xml");

        //创建一个新的TreeNode的ArryList，因为currentStep是Tree Node型的， 对GoalNode进行遍历，强制转成TreeNode型
        ArrayList<TreeNode> currentSteps = new ArrayList<>();
        for (GoalNode tlg : tlgs) {
            currentSteps.add((TreeNode) tlg);
            map1.put(tlg, tlg);

        }
        root.setCurrentStep(map1);
        root.setId(0);
        graph.setCurrentNode(root);
        graph.addNode(root);

        int i = 1;
        String data;
        while (!("//".equals(data = br.readLine())) && data != null && !data.equals("")) {

            String[] strArray = data.split("-");

            Node node = new Node(i, strArray[0], strArray[1]);
            HashMap map = new HashMap();

            //把当前节点的map赋值一份，方便让孩子节点在其基础上更新
            map.putAll(graph.getCurrentNode().getCurrentStep());

            GoalNode searchGoalNode = node.searchWhichGoal(tlgs);//找到当前执行的哪棵树
            TreeNode searchActionNode = node.traversal(searchGoalNode, node.getActionName());

            map.put(searchGoalNode, searchActionNode);
            node.setCurrentStep(map);


            graph.addNode(node);

            graph.getCurrentNode().addChildNode(node);

            graph.setCurrentNode(node);

            i++;
        }

        return root;
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
