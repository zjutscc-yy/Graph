package generators;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GraphGenerator {




//    /**
//     * 给定一个DAG，生成其对应的UML图
//     *
//     * @param graphName UML图的名字
//     * @param DAG       给定的DAG
//     * @return
//     */
//    public static String convertToDotE(String graphName, List<Event> DAG) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("@startuml\n\n")
//                .append("digraph ").append(graphName).append(" {\n");
//        DAG.forEach(event -> sb.append("    ").append(event.getName()).append(";\n"));
//        sb.append("\n");
//        DAG.forEach(from -> from.getOutEdges().forEach(outEdge -> {
//            sb.append("    ").append(from.getName()).append(" -> ").append(outEdge.getTo().getName()).append(";\n");
//        }));
//        sb.append("}\n")
//                .append("\n@enduml\n");
//        return sb.toString();
//    }
//
//    public static void graphWrite(String s) {
//        FileWriter fw = null;
//        try {
//            fw = new FileWriter("graphView.txt");
//            fw.write(s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fw != null) fw.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


}
