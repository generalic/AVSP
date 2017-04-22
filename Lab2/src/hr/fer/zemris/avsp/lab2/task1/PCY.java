package hr.fer.zemris.avsp.lab2.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by generalic on 21/04/17.
 */
public class PCY {

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.err.println(result);
    }

    private static void start() {
        List<Integer> out = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int n = Integer.parseInt(br.readLine());
            double s = Double.parseDouble(br.readLine());
            int numBuckets = Integer.parseInt(br.readLine());

            int limit = (int) Math.floor(s * n);

            // streams solution sequential and parallel
            List<int[]> boxes = br.lines()
                .parallel()
                .map(l -> Arrays.stream(l.split("\\s+")).mapToInt(Integer::parseInt).toArray())
                .collect(Collectors.toList());

            Map<Integer, Integer> items = boxes
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

            System.out.println(numAPrioriPairs);
            System.out.println(numPCYPairs);

            out.add(numAPrioriPairs);
            out.add(numPCYPairs);

            pairs.values()
                .stream()
                .sorted(Comparator.reverseOrder())
                .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Pair {

        private final int a;
        private final int b;

        public Pair(final int a, final int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (a != pair.a) return false;
            return b == pair.b;
        }

        @Override
        public int hashCode() {
            int result = a;
            result = 31 * result + b;
            return result;
        }
    }
}
