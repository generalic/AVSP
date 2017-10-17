package hr.fer.zemris.avsp.lab2.test;

import hr.fer.zemris.avsp.lab2.util.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by generalic on 21/04/17.
 */
public class NodeRankTestCache {
    public static final int NUM_OUTGOING_NODES = 15;
    private static final String FILE_NAME = "ttest2/R";
    private final static int INITIAL_CAPACITY = 100;
    List<String> out = new ArrayList<>();
    private int numNodes;
    private double beta;
    private Map<Integer, List<Integer>> nodes;
    private List<Integer> outgoing;
    private Map<Integer, double[]> data;
    private int numQueries;
    private int lastCached = 0;

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        NodeRankTestCache nodeRankTest = new NodeRankTestCache();
        nodeRankTest.start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        //System.out.println();
        //System.out.println(result);
    }

    private static void compareResult(List<Integer> out) {
        System.out.println("Results are correct if there isn't any text after this line.");
        try {
            Path resultPath = Paths.get("data/" + FILE_NAME + ".out");
            Stream<Integer> resultList = Files.lines(resultPath)
                .map(Integer::parseInt);

            Utils.zip(out.stream(), resultList, (a, b) -> a - b)
                .distinct()
                .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        Path path = Paths.get("primjeriA/" + FILE_NAME + ".in");
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String[] firstSplit = br.readLine().split("\\s+");

            numNodes = Integer.parseInt(firstSplit[0]);
            beta = Double.parseDouble(firstSplit[1]);

            nodes = new HashMap<>(numNodes);
            outgoing = new ArrayList<>();

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

                        outgoing.add(i, array.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            data = new HashMap<>(INITIAL_CAPACITY);
            lastCached = 0;

            final double initValue = (1 - beta) / (double) numNodes;
            double[] initVector = IntStream.range(0, numNodes)
                .mapToDouble(i -> initValue)
                .toArray();
            data.put(lastCached, initVector);

            numQueries = Integer.parseInt(br.readLine());

            for (int queryIndex = 0; queryIndex < numQueries; queryIndex++) {
                String[] querySplit = br.readLine().split("\\s+");

                int nodeIndex = Integer.parseInt(querySplit[0]);
                int iteration = Integer.parseInt(querySplit[1]);

                double[] vector = data.get(iteration);
                if (Objects.isNull(vector)) {
                    vector = expandIteration(iteration);
                }

                double value = vector[nodeIndex];
                out.add(String.format("%.10f", value));
            }

            out.forEach(System.out::println);

            //compareResult(out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double[] expandIteration(int iteration) {
        for (; lastCached < iteration; ) {
            double[] oldVector = data.get(lastCached++);
            double[] newVector = new double[numNodes];

            for (int j = 0; j < numNodes; j++) {
                double result = oldVector[j];

                if (Objects.nonNull(nodes.get(j))) {
                    for (int k : nodes.get(j)) {
                        double otherNodeValue = beta * oldVector[k] / outgoing.get(k);
                        result += otherNodeValue;
                    }
                }

                newVector[j] = result;
            }

            data.put(lastCached, newVector);
        }

        return data.get(iteration);
    }
}
