package hr.fer.zemris.avsp.lab2.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestCounting2 {
    public static void main(String[] args) {

        int[] array1 = new int[] { 1, 1, 2, 3, 4, 4 };
        int[] array2 = new int[] { 1, 1, 3, 3, 4, 4 };

        List<int[]> list = Arrays.asList(array1, array2);

        Map<Integer, Integer> counts = list
            .parallelStream()
            .flatMapToInt(Arrays::stream)
            .boxed()
            .collect(
                Collectors.toConcurrentMap(
                    Function.identity(),
                    w -> 1,
                    Integer::sum
                ));
        System.out.println(counts);
    }
}
