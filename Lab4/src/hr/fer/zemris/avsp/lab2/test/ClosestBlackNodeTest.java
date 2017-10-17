package hr.fer.zemris.avsp.lab2.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by generalic on 21/04/17.
 */
public class ClosestBlackNodeTest {

    public static final int LIMIT = 10;
    private static final String FILE_NAME = "btest2/R";

    private List<Node> nodes = new ArrayList<>();
    private Set<Node> visited = new HashSet<>();
    private List<String> out = new ArrayList<>();

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        ClosestBlackNodeTest nodeRankTest = new ClosestBlackNodeTest();
        nodeRankTest.start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.err.println(result);
    }

    private void start() {
        Path path = Paths.get("primjeriB/" + FILE_NAME + ".in");
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String[] split = br.readLine().split("\\s+");

            int numNodes = Integer.parseInt(split[0]);
            int numEdges = Integer.parseInt(split[1]);

            IntStream.range(0, numNodes).forEach(i -> storeNode(br, i));
            IntStream.range(0, numEdges).forEach(i -> storeNeighbours(br));

            bfs(0, visited);

            nodes.forEach(this::printInfo);
            out.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printInfo(Node node) {
        if (node.getClosestBlackDistance() > LIMIT) {
            out.add("-1 -1");
        } else {
            out.add(node.getClosestBlackIndex() + " " + node.getClosestBlackDistance());
        }
    }

    private void storeNeighbours(BufferedReader br) {
        try {
            String line = br.readLine();
            String[] split = line.split("\\s+");

            int src = Integer.parseInt(split[0]);
            int dest = Integer.parseInt(split[1]);

            nodes.get(src).getNeighbours().add(nodes.get(dest));
            nodes.get(dest).getNeighbours().add(nodes.get(src));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeNode(BufferedReader br, int i) {
        try {
            String line = br.readLine();
            int blackBit = Integer.parseInt(line);
            Node node = new Node(i, blackBit == 1);
            nodes.add(node);
            if (node.isBlack()) {
                visited.add(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bfs(int depth, Set<Node> opened) {
        if (depth >= LIMIT) {
            return;
        }

        Set<Node> depthOpened = new HashSet<>();
        Set<Node> depthVisited = opened.stream()
            .flatMap(openedNode -> openedNode.getNeighbours()
                .stream()
                .peek(depthOpened::add)
                .filter(n -> !visited.contains(n))
                .map(n -> analyseNode(openedNode, n)))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        visited.addAll(depthVisited);
        bfs(depth + 1, depthOpened);
    }

    private Node analyseNode(Node openedNode, Node n) {
        if (n.getClosestBlackIndex() > openedNode.getClosestBlackIndex()) {
            n.setClosestBlackIndex(openedNode.getClosestBlackIndex());
        }
        if (n.getClosestBlackDistance()
            >= openedNode.getClosestBlackDistance() + 1) {
            n.setClosestBlackDistance(openedNode.getClosestBlackDistance() + 1);
            return n;
        }
        return null;
    }

    private static class Node implements Comparable<Node> {
        private final int index;
        private final boolean black;
        private int closestBlackIndex;
        private int closestBlackDistance;
        private Set<Node> neighbours;

        Node(int index, boolean black) {
            this.index = index;
            this.black = black;
            closestBlackIndex = black ? index : Integer.MAX_VALUE;
            closestBlackDistance = black ? 0 : Integer.MAX_VALUE;
            this.neighbours = new HashSet<>();
        }

        boolean isBlack() {
            return black;
        }

        Set<Node> getNeighbours() {
            return neighbours;
        }

        int getClosestBlackIndex() {
            return closestBlackIndex;
        }

        void setClosestBlackIndex(int closestBlackIndex) {
            this.closestBlackIndex = closestBlackIndex;
        }

        int getClosestBlackDistance() {
            return closestBlackDistance;
        }

        void setClosestBlackDistance(int closestBlackDistance) {
            this.closestBlackDistance = closestBlackDistance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            return index == node.index;
        }

        @Override
        public int hashCode() {
            return index;
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(index, o.index);
        }
    }
}
