package hr.fer.zemris.avsp.lab2.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        Path path = Paths.get("data/R.in");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int n = Integer.parseInt(br.readLine());
            double s = Double.parseDouble(br.readLine());
            int numBuckets = Integer.parseInt(br.readLine());

            int limit = (int) Math.floor(s * n);

            // normal sequential solution
            List<int[]> boxes = new ArrayList<>(n);
            Map<Integer, Integer> items = new HashMap<>();

            for (int i = 0; i < n; i++) {
                String line = br.readLine();
                String[] split = line.split("\\s+");
                int[] numbers = new int[split.length];

                for (int j = 0; j < numbers.length; j++) {
                    int number = Integer.parseInt(split[j]);
                    numbers[j] = number;

                    items.compute(number, (k, v) -> Objects.isNull(v) ? 1 : v + 1);
                }

                boxes.add(numbers);
            }

            final int numItems = items.size();
            Map<Integer, Integer> buckets = new HashMap<>();

            for (int[] box : boxes) {
                for (int i = 0; i < box.length; i++) {
                    int x = box[i];
                    int xCount = items.get(x);
                    // TODO: 21/04/17 gettas si predmet x i onda provjeri dal je van limita
                    // TODO: 21/04/17 ako je continue s tim si bus ustedil da ides po polju ako si na pocetku npr7
                    if (xCount < limit) {
                        continue;
                    }
                    for (int j = i + 1; j < box.length; j++) {
                        int y = box[j];
                        int yCount = items.get(y);
                        if (xCount >= limit && yCount >= limit) {
                            final int key = (x * numItems + y) % numBuckets;
                            buckets.compute(key, (k, v) -> Objects.isNull(v) ? 1 : v + 1);
                        }
                    }
                }
            }

            Map<Pair, Integer> pairs = new HashMap<>();

            for (int[] box : boxes) {
                for (int i = 0; i < box.length; i++) {
                    int x = box[i];
                    int xCount = items.get(x);
                    // TODO: 21/04/17 gettas si predmet x i onda provjeri dal je van limita
                    // TODO: 21/04/17 ako je continue s tim si bus ustedil da ides po polju ako si na pocetku npr7
                    if (xCount < limit) {
                        continue;
                    }
                    for (int j = i + 1; j < box.length; j++) {
                        int y = box[j];
                        int yCount = items.get(y);
                        if (xCount >= limit && yCount >= limit) {
                            final int key = (x * numItems + y) % numBuckets;
                            if (buckets.get(key) >= limit) {
                                Pair pair = new Pair(x, y);
                                pairs.compute(pair, (k, v) -> Objects.isNull(v) ? 1 : v + 1);
                            }
                        }
                    }
                }
            }

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

    public static class Pair {

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
