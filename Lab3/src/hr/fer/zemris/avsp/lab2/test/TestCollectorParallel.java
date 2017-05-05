package hr.fer.zemris.avsp.lab2.test;

import hr.fer.zemris.avsp.lab2.data.Pair;
import hr.fer.zemris.avsp.lab2.util.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by generalic on 22/04/17.
 */
public class TestCollectorParallel {
    public static void main(String[] args) {
        long t1 = System.nanoTime();

        start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.out.println();
        System.out.println(result);
    }

    private static void start() {
        List<Integer> out = new ArrayList<>();

        Path path = Paths.get("data/R.in");
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int n = Integer.parseInt(br.readLine());
            double s = Double.parseDouble(br.readLine());
            int numBuckets = Integer.parseInt(br.readLine());

            int limit = (int) Math.floor(s * n);

            // streams solution sequential and parallel
            List<int[]> boxes = br.lines()
                //.parallel()
                .map(l -> Arrays.stream(l.split("\\s+")).mapToInt(Integer::parseInt).toArray())
                .collect(Collectors.toList());
            
            Map<Integer, Integer> items = boxes
                //.stream()
                .parallelStream()
                .flatMapToInt(Arrays::stream)
                .boxed()
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        w -> 1,
                        Integer::sum
                    )
                );

            final int numItems = items.size();
            Map<Integer, Integer> buckets = new ConcurrentHashMap<>();

            boxes
                .parallelStream()
                .forEach(box -> {
                    for (int i = 0; i < box.length; i++) {
                        int x = box[i];
                        int xCount = items.get(x);
                        if (xCount < limit) {
                            continue;
                        }
                        for (int j = i + 1; j < box.length; j++) {
                            int y = box[j];
                            int yCount = items.get(y);

                            if (yCount >= limit) {
                                final int key = (x * numItems + y) % numBuckets;
                                buckets.compute(key, (k, v) -> Objects.isNull(v) ? 1 : v + 1);
                            }
                        }
                    }
                });

            Map<Pair, Integer> pairs = new ConcurrentHashMap<>();

            boxes
                .parallelStream()
                .forEach(box -> {
                    for (int i = 0; i < box.length; i++) {
                        int x = box[i];
                        int xCount = items.get(x);
                        if (xCount < limit) {
                            continue;
                        }
                        for (int j = i + 1; j < box.length; j++) {
                            int y = box[j];
                            int yCount = items.get(y);
                            if (yCount >= limit) {
                                final int key = (x * numItems + y) % numBuckets;
                                if (buckets.get(key) >= limit) {
                                    Pair pair = new Pair(x, y);
                                    pairs.compute(pair, (k, v) -> Objects.isNull(v) ? 1 : v + 1);
                                }
                            }
                        }
                    }
                });

            final long numFrequentItems = items.values()
                .stream()
                .filter(f -> f >= limit)
                .count();

            final int numAPrioriPairs = (int) (numFrequentItems * (numFrequentItems - 1) / 2);
            final int numPCYPairs = pairs.size();

            out.add(numAPrioriPairs);
            out.add(numPCYPairs);

            pairs.values()
                .stream()
                .sorted(Comparator.reverseOrder())
                .forEach(out::add);

            compareResult(out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compareResult(List<Integer> out) {
        System.out.println("Results are correct if there isn't any text after this line.");
        try {
            Path resultPath = Paths.get("data/R.out");
            Stream<Integer> resultList = Files.lines(resultPath)
                .map(Integer::parseInt);

            Utils.zip(out.stream(), resultList, (a, b) -> a - b)
                .distinct()
                .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
