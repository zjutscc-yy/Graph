package generators;

import structure.Graph;
import xml.WriteGraph;

/**
 * 生成图需要的参数
 * 1.读取action文件的路径
 * 2.原本gpt的路径
 * 3.要把生成的图的xml保存到哪
 *
 * 上面参数都是在main中修改
 *
 * 此外：修改ReadFile类中writeUml方法中txt文件的名字
 */

public class Main {
    public static void main(String[] args) throws Exception {
//        读取actions.txt
       ReadFile read = new ReadFile();
       Graph bigGraph = read.readFile("F:\\project\\SQ-MCTS\\actions_8.txt","F:\\project\\gpt\\8.xml");

       System.out.println(bigGraph.getNodes().size());

       bigGraph.traversalId();
       bigGraph.traversalChildNode();

       //把生成的图保存为xml文件
        String path ="F:\\project\\graph\\graph8_txt.xml";
       WriteGraph wxf = new WriteGraph();
       wxf.CreateXML(bigGraph,path);
    }
}

