package hr.fer.zemris.avsp.lab2.task1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by generalic on 07/04/17.
 */
public class SimHash {

    private static Map<Integer, byte[]> map = new HashMap<>();

    public static void main(String[] args) {
        long t1 = System.nanoTime();

        start();

        long t2 = System.nanoTime();
        double result = (t2 - t1) / 1e9;
        System.err.println(result);
    }

    private static void start() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            int n = Integer.parseInt(br.readLine());
            for (int i = 0; i < n; i++) {
                String text = br.readLine();
                map.put(i, simHash(text));
            }

            int q = Integer.parseInt(br.readLine());

            for (int i = 0; i < q; i++) {
                String query = br.readLine();
                int count = processQuery(query);
                System.out.println(count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int processQuery(String query) {
        String[] split = query.split("\\s");

        int textIndex = Integer.parseInt(split[0]);
        int k = Integer.parseInt(split[1]);

        byte[] textHash = map.get(textIndex);

        long count = map.entrySet()
            .parallelStream()
            .filter(e -> e.getKey() != textIndex)
            .map(Map.Entry::getValue)
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
