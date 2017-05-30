package hr.fer.zemris.avsp.lab2.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by generalic on 30/05/17.
 * lab3
 */
public class MapNullTest {
    public static void main(String[] args) {
        Map<Integer, List<Integer>> nodes = new HashMap<>();

        List<Integer> test1 = new ArrayList<>();
        test1.add(0);

        nodes.put(0, test1);

        List<Integer> test2 = nodes.get(1);
        if (test2 == null) {
            test2 = new ArrayList<>();
            nodes.put(1, test2);
        }
        test2.add(5);

        nodes.get(1).forEach(System.out::println);
    }
}
