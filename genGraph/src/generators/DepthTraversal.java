package generators;

import structure.Graph;
import structure.Node;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 图的深度优先遍历
 */
public class DepthTraversal {

    Stack<Node> stack = new Stack<Node>();

    public void findPathByNode(Node root){
        stack.push(root);

        if(root.getChildNode() != null) {
            for (Node node : root.getChildNode()) {
                findPathByNode(node);
            }
            stack.pop();
        }else {
            printPath(stack);
            if(!stack.isEmpty()) {
                stack.pop();
            }
            return;
        }

    }

    //打印节点之间的路径
    public void printPath(Stack<Node> s) {

        String path = "[";
        for(Node sNode:s) {
            path = path  + sNode.getId()+ "->";
        }

        System.out.println(path.substring(0, path.length()-2)+"]");
    }

    public void setVisitedFalse(ArrayList<Node> nodes) {
        for (Node setnode : nodes) {
            setnode.setVisited(false);
        }
    }
}

