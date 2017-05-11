package hr.fer.zemris.avsp.lab2.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by generalic on 09/05/17.
 * lab1
 */
public class ListinsertTest {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        list.add(2, 55);

        for (int i : list) {
            System.out.print(i + ",");
        }
    }
}
