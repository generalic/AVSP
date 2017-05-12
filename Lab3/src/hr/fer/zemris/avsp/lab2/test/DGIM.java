package hr.fer.zemris.avsp.lab2.test;

import hr.fer.zemris.avsp.lab2.data.Bucket;
import hr.fer.zemris.avsp.lab2.data.Pair;
import hr.fer.zemris.avsp.lab2.util.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by generalic on 12/05/17.
 * lab3
 */
public class DGIM {

    private static final String FILE_NAME = "1";
    private static final int SIZE_MULTIPLIER = 2;
    private static final int ALLOWED_BUCKET_NUMBER = 2;
    private static final int INIT_SIZE = 1;

    private List<Bucket> buckets;
    private int n;
    private long timer;

    private List<Integer> out;

    public DGIM() {
        this.buckets = new ArrayList<>();
        this.timer = 0;
        this.out = new ArrayList<>();
    }

    private static void compareResult(List<Integer> out) {
        System.out.println("Results are correct if there isn't any text after this line.");
        try {
            Path resultPath = Paths.get("data/" + FILE_NAME + ".out");
            Stream<Integer> resultList = Files.lines(resultPath)
                .map(Integer::parseInt);

            //Utils.zip(out.stream(), resultList, (a, b) -> a - b)
            //    .distinct()
            //    .forEach(System.out::println);

            List<Pair> pairs = Utils.zip(out.stream(), resultList, Pair::new)
                .collect(Collectors.toList());

            for (Pair pair : pairs) {
                System.out.println(pair);
            }

            System.out.println();
            System.out.println();

            pairs.stream()
                .map(p -> p.getA() - p.getB())
                .distinct()
                .forEach(System.out::println);

            List<Pair> diff = pairs.stream()
                .filter(p -> p.getA() != p.getB())
                .collect(Collectors.toList());

            System.out.println(diff.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        DGIM dgim = new DGIM();
        dgim.start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.out.println();
        System.out.println(result);
    }

    private void resolveQuery(int k) {
        List<Bucket> candidates = buckets.stream()
            .filter(b -> timer - b.getTimestamp() < k)
            .collect(Collectors.toList());

        if (candidates.isEmpty() || candidates.size() == 1) {
            out.add(0);
            return;
        }

        int sum = IntStream.range(1, candidates.size())
            .map(i -> candidates.get(i).getSize())
            .sum();

        Bucket last = candidates.get(0);
        out.add(sum + last.getSize() / 2);
    }

    private void mergeBuckets() {
        final int maxSize = buckets.stream()
            .mapToInt(Bucket::getSize)
            .max()
            .getAsInt();

        for (int size = INIT_SIZE; size <= maxSize; size *= SIZE_MULTIPLIER) {
            final int currentSize = size;
            List<Bucket> layer = buckets.stream()
                .filter(b -> b.getSize() == currentSize)
                .collect(Collectors.toList());

            if (!(layer.size() > ALLOWED_BUCKET_NUMBER)) {
                break;
            }

            Bucket newestBucket = layer.get(2);

            Bucket a = layer.get(0);
            Bucket b = layer.get(1);
            long newTimestamp = b.getTimestamp();
            int newSize = a.getSize() * SIZE_MULTIPLIER;

            Bucket mergedBucket = new Bucket(newTimestamp, newSize);

            buckets.add(buckets.indexOf(newestBucket), mergedBucket);
            buckets.remove(a);
            buckets.remove(b);
        }
    }

    private void removeOldBuckets() {
        List<Bucket> dropList = buckets.stream()
            .filter(b -> timer - b.getTimestamp() == n) // timer - bittimestamp == n --> drop
            .collect(Collectors.toList());

        buckets.removeAll(dropList);
    }

    private void start() {
        Path path = Paths.get("data/" + FILE_NAME + ".in");
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            n = Integer.parseInt(reader.readLine());

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                line = line.trim();

                if (line.startsWith("q")) {
                    // TODO: 08/05/17 query
                    final int k = Integer.parseInt(line.split("\\s+")[1]);
                    resolveQuery(k);
                } else {
                    // TODO: 08/05/17 stream data
                    char[] bits = line.toCharArray();
                    for (int i = 0; i < bits.length; i++) {
                        timer++;
                        char c = bits[i];
                        removeOldBuckets();
                        if (c == '1') {
                            Bucket bucket = new Bucket(timer);
                            buckets.add(bucket);
                            mergeBuckets();
                        }
                    }
                }
            }

            //out.forEach(System.out::println);
            compareResult(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
