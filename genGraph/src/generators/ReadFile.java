package generators;

import goalplantree.GoalNode;
import goalplantree.TreeNode;
import structure.Graph;
import structure.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadFile {
    public static Graph bigGraph;
    public static int ID = 0;

    private int index = 0;

    /**
     * 第0条路径里面T15对应的最后一个节点的index为 AllPathTIndex[0][14]
     */
    private int AllPathTIndex[][] = new int[1500][10];
    private int TFlag = 0;//作用域有限
    private Long fileSize = 0l;


    public Graph readFile(String fileName,String gptPath) throws IOException {
        /**
         * 先设置每条路径的最后一个T
         */
        fileSize = new File(fileName).length();
        BufferedReader brTemp = new BufferedReader(new FileReader(fileName));
        String lineTemp = "";
        List<String> findEndIndexTempList = new ArrayList<>();
        int pathIndex = 0;
        while ((lineTemp = brTemp.readLine()) != null) {
            //如果文件还有内容
            if (lineTemp.equals(""))
                continue;               //如果当前行为空，跳过这一行
            if (lineTemp.equals("//")) {//到了单条路径的末尾
                //这里更新当前路径的某一个T的最后节点的index
                for (int iTemp = 0; iTemp < AllPathTIndex[pathIndex].length; iTemp++)
                    AllPathTIndex[pathIndex][iTemp] = findEndIndexTempList.lastIndexOf("" + iTemp);

                pathIndex++;
                findEndIndexTempList.clear();// 清空当前路径的T数据缓存内容
                continue;               //跳过这一行
            }
            findEndIndexTempList.add(lineTemp.split("-")[0].replace("T", ""));
        }
        for (int iTemp = 0; iTemp < AllPathTIndex[pathIndex].length; iTemp++)
            AllPathTIndex[pathIndex][iTemp] = findEndIndexTempList.lastIndexOf("" + iTemp);
        brTemp.close();

        BufferedReader br = new BufferedReader(new FileReader(fileName));


        //调用字符缓冲输入流对象的方法读数据
        String line;
        /*
        1.readLine 这里的只进行读
        2.创建多个路径List
        3.
         */
        int currentPathIndex = 0;
        bigGraph = new Graph();
        bigGraph.setInitialState(gptPath);
        while ((line = br.readLine()) != null) {
            Graph singlePathGraph = generatePathGraph(br, currentPathIndex);
            currentPathIndex++;
            index = singlePathGraph.getNodes().size();
            bigGraph = mergeGraph(bigGraph, singlePathGraph);
        }
        br.close();

        writeUml(bigGraph);

        return bigGraph;
    }


    //两个图合成一个图
    public Graph mergeGraph(Graph graph, Graph pathGraph) {
        if (graph.getNodes().size() == 0) {
            return pathGraph;
        }
        boolean isAdd = false;//判断curGraphPtr是否为新加节点

        Node curGraphPtr = graph.getRoot();
        Node curPathPtr = pathGraph.getRoot().getChildNode().get(0);

        out:
        while (curPathPtr.getChildNode().size() != 0) {

            ArrayList<Node> curNodes = graph.getNodes();
            inner:
            for (int i = 0; i < curNodes.size(); i++) {
                Node graphNode = curNodes.get(i);
                if (graphNode.equals(curPathPtr)) {
                    if (!isAdd) {//curGraphPtr在图里，curPathPtr在图里
                        if (!curGraphPtr.getChildNode().contains(graphNode)) {
                            curGraphPtr.addChildNode(graphNode);//可能会加重复，连着都不等，加个判断孩子节点里是否已经存在curPathPtr
                        }
                    } else {
                        curGraphPtr.addChildNode(graphNode);//这有问题
                        curGraphPtr.removeChildNode(curPathPtr);
                    }
                    curGraphPtr = graphNode;
                    if (curPathPtr.getChildNode().size() != 0) {
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
            if (graphNode.equals(curPathPtr)) {
                if (!isAdd) {
                    if (!curGraphPtr.getChildNode().contains(graphNode)) {
                        curGraphPtr.addChildNode(graphNode);//可能会加重复，连着都不等，加个判断孩子节点里是否已经存在curPathPtr
                    }
                } else {
                    curGraphPtr.addChildNode(graphNode);
                    curGraphPtr.removeChildNode(curPathPtr);
                    break;
                }
            }
        }

        return graph;
    }

    //生成单条路径
    public Graph generatePathGraph(BufferedReader br, int currentPathIndex) throws IOException {

        boolean isPathEnd = false;
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
        int indexOfSingle = -1;

        while (!isPathEnd && !("//".equals(data = br.readLine())) && data != null && !data.equals("")) {
            indexOfSingle++;
            String[] strArray = data.split("-");

            Node node = new Node(ID++, strArray[0], strArray[1]);
            HashMap map = new HashMap();

            //把当前节点的map赋值一份，方便让孩子节点在其基础上更新
            map.putAll(pathGraph.getCurrentNode().getCurrentStep());

            ArrayList<GoalNode> achievedTlg = new ArrayList<>();
            achievedTlg.addAll(pathGraph.getCurrentNode().getAchievedGoal());
            node.setAchievedGoal(achievedTlg);

            GoalNode searchGoalNode = node.searchWhichGoal(tlgs, strArray);//找到当前执行的哪棵树
            TreeNode searchActionNode = node.traversal(searchGoalNode, node.getActionName());

            map.put(searchGoalNode, searchActionNode);
            node.setCurrentStep(map);

            pathGraph.addNode(node);

            pathGraph.getCurrentNode().addChildNode(node);

            pathGraph.setCurrentNode(node);


            /**
             * 如果当前节点某个T的最后一个节点，在其后添加null并把下一行的状态更新
             */
            String nextLine = "";
            while (isCurrentNodeEnd(currentPathIndex, indexOfSingle)) {
                String TEnd = "";
                String GEnd = "";

                TEnd += "T" + TFlag;
                GEnd += "G" + 0;
                Node insertNode = new Node();
                HashMap insertMap = new HashMap();
                insertMap.putAll(pathGraph.getCurrentNode().getCurrentStep());

                for (GoalNode tlg : tlgs) {
                    if (tlg.getName().equals(TEnd + "-" + GEnd)) {
                        insertMap.put(tlg, null);
                        pathGraph.getCurrentNode().addAchievedGoal(tlg);
                    }
                }

                ArrayList<GoalNode> insertTlg = new ArrayList<>();
                insertTlg.addAll(pathGraph.getCurrentNode().getAchievedGoal());
                insertNode.setAchievedGoal(insertTlg);

                //读下一条
                br.mark(fileSize.intValue());
                nextLine = br.readLine();
                //判断条件
                if (nextLine == null || nextLine.equals("//") || nextLine.equals("")) {
                    // 这里表示到了单条路径的末尾
                    isPathEnd = true;
                    break;
                }
                String[] nextArray = nextLine.split("-");
                //更新
                GoalNode searchGoal = node.searchWhichGoal(tlgs, nextArray);//找到当前执行的哪棵树
                TreeNode searchAction = node.traversal(searchGoal, nextArray[1]);

                insertMap.put(searchGoal, searchAction);
                insertNode.setCurrentStep(insertMap);
                insertNode.setId(ID++);
                pathGraph.getCurrentNode().addChildNode(insertNode);
                pathGraph.addNode(insertNode);
                pathGraph.setCurrentNode(insertNode);
                //更新结束
//                br.reset();
                if (isCurrentNodeEnd(currentPathIndex, indexOfSingle + 1)) {//判断下一个action是否为end
                    indexOfSingle++;
                    continue;
                } else
                    indexOfSingle++;
                break;
            }
        }

        //添加最后节点
        Node lastNode = new Node();
        HashMap lastMap = new HashMap();
        lastMap.putAll(pathGraph.getCurrentNode().getCurrentStep());
        for (GoalNode tlg : tlgs) {
            lastMap.put(tlg,null);
            lastNode.addAchievedGoal(tlg);
        }
        lastNode.setCurrentStep(lastMap);
        lastNode.setId(ID++);
        pathGraph.addNode(lastNode);
        pathGraph.getCurrentNode().addChildNode(lastNode);

        pathGraph.setEndNode(lastNode);
        return pathGraph;
    }

    /**
     * 判断当前action是否为最后一个action
     * a@param currentIndex
     *
     * @return
     */
    private boolean isCurrentNodeEnd(int currentPathIndex, int currentIndex) {
        for (int i = 0; i < AllPathTIndex[currentPathIndex].length && AllPathTIndex[currentPathIndex][i] != -1; i++) {
            if (currentIndex == AllPathTIndex[currentPathIndex][i]) {
                TFlag = i;
                return true;
            }
        }
        return false;
    }

    //把生成的图写成.txt，生成uml文件
    public void writeUml(Graph graph) throws IOException {

        //把节点和边保存到txt文件中
        File graphFile = new File("F:\\project\\SQ-MCTS\\genGraph\\graphView120_0.05.txt");

        FileWriter newFile = new FileWriter("graphView120_0.05.txt", true);

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
