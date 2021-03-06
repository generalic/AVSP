package hr.fer.zemris.avsp.lab2.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Created by generalic on 21/04/17.
 */
public class NodeRank {

    public static final int NUM_OUTGOING_NODES = 15;
    private final static int INITIAL_CAPACITY = 100;

    private List<String> out = new ArrayList<>();
    private int numNodes;
    private double beta;
    private Map<Integer, List<Integer>> nodes;
    private List<Integer> outgoings;
    private double[][] data;
    private int numQueries;

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        NodeRank nodeRankTest = new NodeRank();
        nodeRankTest.start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.err.println(result);
    }

    private void start() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String[] firstSplit = br.readLine().split("\\s+");

            numNodes = Integer.parseInt(firstSplit[0]);
            beta = Double.parseDouble(firstSplit[1]);

            nodes = new HashMap<>(numNodes);
            outgoings = new ArrayList<>();

            IntStream.range(0, numNodes)
                .forEach(i -> {
                    try {
                        String line = br.readLine();
                        int[] array = Arrays.stream(line.split("\\s+"))
                            .mapToInt(Integer::parseInt)
                            .toArray();

                        for (int nodeIndex : array) {
                            List<Integer> list = nodes.get(nodeIndex);
                            if (Objects.isNull(list)) {
                                list = new ArrayList<>();
                                nodes.put(nodeIndex, list);
                            }
                            list.add(i);
                        }

                        outgoings.add(i, array.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            data = new double[INITIAL_CAPACITY + 1][numNodes];

            final double initValue = 1 / (double) numNodes;

            double[] initVector = IntStream.range(0, numNodes)
                .mapToDouble(i -> initValue)
                .toArray();
            data[0] = initVector;

            for (int i = 1; i <= INITIAL_CAPACITY; i++) {
                double S = 0.0;
                for (int node : nodes.keySet()) {
                    List<Integer> incoming = nodes.get(node);
                    for (int n : incoming) {
                        data[i][node] += beta * (data[i - 1][n] / outgoings.get(n));
                    }
                    S += data[i][node];
                }
                for (int n = 0; n < numNodes; n++) {
                    data[i][n] += (1 - S) / numNodes;
                }
            }

            numQueries = Integer.parseInt(br.readLine());

            for (int queryIndex = 0; queryIndex < numQueries; queryIndex++) {
                String[] querySplit = br.readLine().split("\\s+");

                int nodeIndex = Integer.parseInt(querySplit[0]);
                int iteration = Integer.parseInt(querySplit[1]);

                double value = data[iteration][nodeIndex];
                out.add(String.format("%.10f", value));
            }

            out.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
