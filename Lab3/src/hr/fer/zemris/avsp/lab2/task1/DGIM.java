package hr.fer.zemris.avsp.lab2.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by generalic on 12/05/17.
 * lab3
 */
public class DGIM {

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

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        DGIM dgim = new DGIM();
        dgim.start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.err.println(result);
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            n = Integer.parseInt(reader.readLine());

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                line = line.trim();

                if (line.startsWith("q")) {
                    final int k = Integer.parseInt(line.split("\\s+")[1]);
                    resolveQuery(k);
                } else {
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

            out.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Bucket {

        private static final int DEFAULT_SIZE = 1;

        private long timestamp;
        private int size;

        public Bucket(long timestamp, int size) {
            this.timestamp = timestamp;
            this.size = size;
        }

        public Bucket(long timestamp) {
            this(timestamp, DEFAULT_SIZE);
        }

        public long getTimestamp() {
            return timestamp;
        }

        public int getSize() {
            return size;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bucket bucket = (Bucket) o;

            if (timestamp != bucket.timestamp) return false;
            return size == bucket.size;
        }

        @Override
        public int hashCode() {
            int result = (int) (timestamp ^ (timestamp >>> 32));
            result = 31 * result + size;
            return result;
        }
    }
}
