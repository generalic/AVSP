package hr.fer.zemris.avsp.lab2.test;

/**
 * Created by generalic on 08/05/17.
 * lab1
 */
public class TestCharArray {
    public static void main(String[] args) {
        String line = "1000010010";

        for (char c : line.toCharArray()) {
            int number = c;

            if (c == '0') {
                System.out.println(c);
            }
            if (c == '1') {
                System.out.println(c);
            }
        }
    }
}
