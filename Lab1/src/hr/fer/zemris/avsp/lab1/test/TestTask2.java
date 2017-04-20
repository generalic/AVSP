package hr.fer.zemris.avsp.lab1.test;

import hr.fer.zemris.avsp.lab1.util.Utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by generalic on 07/04/17.
 */
public class TestTask2 {

    private static final int NUMBER_OF_SLICES = 8;

    private static Map<Integer, byte[]> map = new HashMap<>();
    private static Map<Integer, Set<Integer>> candidates = new HashMap<>();

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.out.println();
        System.out.println(result);
    }

    private static void start() {
        Path path = Paths.get("data/sprutB.in");

        List<Integer> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int n = Integer.parseInt(br.readLine());
            for (int i = 0; i < n; i++) {
                String text = br.readLine();
                map.put(i, simHash(text));
            }

            localSensitiveHashing(n);

            int q = Integer.parseInt(br.readLine());
            for (int i = 0; i < q; i++) {
                String query = br.readLine();
                int count = processQuery(query);
                out.add(count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path resultPath = Paths.get("data/sprutB.out");

        try {
            Stream<Integer> resultList = Files.readAllLines(resultPath)
                .stream()
                .map(Integer::parseInt);

            Utils.zip(out.stream(), resultList, (a, b) -> a - b)
                .distinct()
                .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void localSensitiveHashing(int n) {
        for (int slice = 0; slice < NUMBER_OF_SLICES; slice++) {

            Map<Integer, Set<Integer>> buckets = new HashMap<>();
            for (int textId = 0; textId < n; textId++) {
                byte[] hash = map.get(textId);

                int value = hashToIntValue(hash, slice);
                Set<Integer> texts = buckets.get(value);

                if (Objects.nonNull(texts)) {
                    for (int id : texts) {
                        putInList(candidates, textId, id);
                        putInList(candidates, id, textId);
                    }
                } else {
                    texts = new HashSet<>();
                }

                texts.add(textId);
                buckets.put(value, texts);
            }
        }
    }

    private static void putInList(Map<Integer, Set<Integer>> candidates, int textId, int id) {
        Set<Integer> ids = candidates.get(textId);
        if (Objects.isNull(ids)) {
            ids = new HashSet<>();
        }
        ids.add(id);
        candidates.put(textId, ids);
    }

    private static int hashToIntValue(byte[] hash, int slice) {
        int from = slice * 16;
        int to = (slice + 1) * 16;

        StringBuilder sb = new StringBuilder();
        IntStream.range(from, to)
            .map(i -> hash[i])
            .forEach(sb::append);

        return Integer.valueOf(sb.toString(), 2);
    }

    private static int processQuery(String query) {
        String[] split = query.split("\\s");

        int textIndex = Integer.parseInt(split[0]);
        int k = Integer.parseInt(split[1]);

        byte[] textHash = map.get(textIndex);

        long count = candidates.get(textIndex)
            .stream()
            //.parallelStream()
            .map(map::get)
            .mapToInt(h -> getDistance(textHash, h))
            .filter(c -> c <= k)
            .count();

        return (int) count;
    }

    private static int getDistance(byte[] text1, byte[] text2) {
        return (int) IntStream.range(0, text1.length)
            .filter(i -> text1[i] != text2[i])
            .count();
    }

    private static byte[] simHash(String text) {
        int[] sh = new int[128];

        for (String token : text.split("\\s+")) {
            byte[] bytes = DigestUtils.md5(token);

            int index = 0;
            for (byte b : bytes) {
                for (int j = 7; j >= 0; j--) {
                    sh[index++] += (b >> j & 0x1) == 1 ? 1 : -1;
                }
            }
        }

        byte[] simHash = new byte[128];
        for (int i = 0; i < sh.length; i++) {
            simHash[i] = (byte) (sh[i] >= 0 ? 1 : 0);
        }

        return simHash;
    }
}
