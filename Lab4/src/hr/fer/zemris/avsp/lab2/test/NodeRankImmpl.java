package hr.fer.zemris.avsp.lab2.test;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author petar
 */
public class NodeRankImmpl {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("primjeriA/ttest2/R.in");
        Scanner input = new Scanner(new FileInputStream(file));
        DecimalFormat df2 = new DecimalFormat("0.0000000000");

        String[] graphConf = new String[2];
        Integer numOfNodes = 0;
        double teleport = 0.0;
        graphConf = input.nextLine().split("\\s+");
        numOfNodes = Integer.parseInt(graphConf[0]);
        teleport = Double.parseDouble(graphConf[1]);
        Map<Integer, List<Integer>> neighbours = new HashMap<>();
        int[] vectors = new int[numOfNodes];
        int lineCount = 0;
        while (lineCount < numOfNodes) {
            String[] currentLine = input.nextLine().split("\\s+");
            Integer[] nodes = new Integer[currentLine.length];
            for (int k = 0; k < (currentLine.length); k++) {
                nodes[k] = Integer.parseInt(currentLine[k]);
            }
            for (int j = 0; j < currentLine.length; j++) {
                if (neighbours.containsKey(nodes[j])) {
                    neighbours.get(nodes[j]).add(lineCount);
                } else {
                    List<Integer> pointers = new ArrayList<>();
                    pointers.add(lineCount);
                    neighbours.put(nodes[j], pointers);
                }
            }
            vectors[lineCount] = currentLine.length;
            lineCount++;
        }
        double[][] rank = new double[101][numOfNodes];
        for (int k = 0; k < numOfNodes; k++) {
            rank[0][k] = (double) 1 / numOfNodes;
        }
        for (int k = 1; k < 101; k++) {
            double S = 0.0;
            for (Integer node : neighbours.keySet()) {
                List<Integer> list = new ArrayList();
                list = neighbours.get(node);
                for (Integer pointer : list) {
                    rank[k][node] +=
                        (double) (teleport * (rank[k - 1][pointer]) / vectors[pointer]);
                }
                S += (double) rank[k][node];
            }
            for (int j = 0; j < numOfNodes; j++) {
                rank[k][j] += (double) ((1 - S) / numOfNodes);
            }
        }
        int numberOfQueries = Integer.parseInt(input.nextLine());
        int inputNode = 0;
        int iteration = 0;
        String[] currentLine = new String[2];
        int N = 0;
        StringBuilder sb = new StringBuilder();
        while (N < numberOfQueries) {
            currentLine = input.nextLine().split("\\s+");
            inputNode = Integer.parseInt(currentLine[0]);
            iteration = Integer.parseInt(currentLine[1]);
            sb.append(df2.format(rank[iteration][inputNode]).replace(',', '.'));
            sb.append("\n");
            N++;
        }
        System.out.println(sb);
    }
}

