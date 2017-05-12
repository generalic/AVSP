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
import java.util.stream.Stream;

/**
 * Created by generalic on 05/05/17.
 */
public class TestImplementation {

    private static final String FILE_NAME = "1";
    private static final int SIZE_MULTIPLIER = 2;
    private static final int ALLOWED_BUCKET_NUMBER = 2;

    private static List<Bucket> buckets = new ArrayList<>();
    private static int n;
    private static long timer;
    private static long maxSize = 1;
    private static List<Integer> out = new ArrayList<>();

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.out.println();
        System.out.println(result);
    }

    private static void start() {
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

    private static void resolveQuery(int k) {
        List<Bucket> candidates = buckets.stream()
            .filter(b -> b.getTimestamp() > (timer - k))
            .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            out.add(0);
            return;
        }

        Bucket last = candidates.get(0);
        int sum = last.getSize() / 2;

        if (candidates.size() == 1) {
            return;
        }

        for (int i = 1, n = candidates.size(); i < n; i++) {
            sum += candidates.get(i).getSize();
        }

        out.add(sum);
    }

    private static void mergeBuckets() {
        for (int size = 1; size <= maxSize; size *= SIZE_MULTIPLIER) {
            final int currentSize = size;
            List<Bucket> layer = buckets.stream()
                .filter(b -> b.getSize() == currentSize)
                .collect(Collectors.toList());

            if (layer.size() > ALLOWED_BUCKET_NUMBER) {
                Bucket newestBucket = layer.get(2);

                Bucket oldBucket = layer.get(1);
                long newTimestamp = oldBucket.getTimestamp();
                int newSize = oldBucket.getSize() * SIZE_MULTIPLIER;
                maxSize = newSize;

                Bucket mergedBucket = new Bucket(newTimestamp, newSize);

                buckets.add(buckets.indexOf(newestBucket), mergedBucket);
                buckets.remove(layer.get(0));
                buckets.remove(layer.get(1));
            }
        }
    }

    private static void removeOldBuckets() {
        List<Bucket> dropList = buckets.stream()
            .filter(b -> timer - b.getTimestamp() == n) // timer - bittimestamp == n --> drop
        .collect(Collectors.toList());

        buckets.removeAll(dropList);
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

            //List<Pair> diff = pairs.stream()
            //    .filter(p -> p.getA() != p.getB())
            //    .collect(Collectors.toList());
            //
            //System.out.println(diff.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
