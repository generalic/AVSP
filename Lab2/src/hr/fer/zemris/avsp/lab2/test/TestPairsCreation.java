package hr.fer.zemris.avsp.lab2.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestPairsCreation {
    public static void main(String[] args) {

        int[] array1 = new int[] { 1, 2, 3, 4, 5 };

        List<int[]> boxes = Arrays.asList(array1);

        for (int[] box : boxes) {
            for (int i = 0; i < box.length; i++) {
                for (int j = i + 1; j < box.length; j++) {
                    System.out.println(box[i] + " " + box[j]);
                }
            }
        }
    }
}
