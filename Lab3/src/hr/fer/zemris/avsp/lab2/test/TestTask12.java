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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by generalic on 21/04/17.
 */
public class TestTask12 {

    private static Map<Integer, byte[]> map = new HashMap<>();

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

        Utils.zip(
            getStream1(),
            getStream2(),
            (a, b) -> a.getKey() - b.getKey() + a.getValue() - b.getValue()
        )
            .distinct()
            .forEach(System.out::println);
    }

    private static Stream<Map.Entry<Integer, Integer>> getStream1() {
        Path path = Paths.get("data/R.in");
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int n = Integer.parseInt(br.readLine());
            double s = Double.parseDouble(br.readLine());
            int b = Integer.parseInt(br.readLine());

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

                    items.compute(number, (k, v) -> Objects.isNull(v) ? 1 : ++v);
                }

                boxes.add(numbers);
            }

            System.out.println("Stream normal");
            System.out.println(boxes.size());
            System.out.println(items.size());

            return items
                //.values()
                .entrySet()
                .stream()
                //.sorted();
                .sorted((e1, e2) -> Integer.compare(e1.getKey(), e2.getKey()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Stream<Map.Entry<Integer, Integer>> getStream2() {
        Path path = Paths.get("data/R.in");
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int n = Integer.parseInt(br.readLine());
            double s = Double.parseDouble(br.readLine());
            int b = Integer.parseInt(br.readLine());

            int limit = (int) Math.floor(s * n);

            // streams solution sequential and parallel
            List<int[]> boxes2 = br.lines()
                //.parallel()
                .map(l -> Arrays.stream(l.split("\\s+")).mapToInt(Integer::parseInt).toArray())
                .collect(Collectors.toList());

            //List<int[]> boxes2 = new ArrayList<>();
            //
            //for (int i = 0; i < n; i++) {
            //    String line = br.readLine();
            //    String[] split = line.split("\\s+");
            //    int[] numbers = new int[split.length];
            //
            //    for (int j = 0; j < numbers.length; j++) {
            //        int number = Integer.parseInt(split[j]);
            //        numbers[j] = number;
            //
            //    }
            //
            //    boxes2.add(numbers);
            //}

            Map<Integer, Integer> items2 = boxes2
                .stream()
                //.parallelStream()
                .flatMapToInt(Arrays::stream)
                .boxed()
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        w -> 1,
                        Integer::sum
                    )
                );

            System.out.println("Stream parallel/streams");
            System.out.println(boxes2.size());
            System.out.println(items2.size());

            return items2
                //.values()
                .entrySet()
                .stream()
                //.sorted();
                .sorted((e1, e2) -> Integer.compare(e1.getKey(), e2.getKey()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
