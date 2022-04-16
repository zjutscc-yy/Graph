package generators;

import agent.MCTSAgent;
import structure.Node;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args)throws IOException {
        //读取actions.txt
       ReadFile read = new ReadFile();
       read.readFile("F:\\project\\SQ-MCTS\\actions1.txt");

    }
}

