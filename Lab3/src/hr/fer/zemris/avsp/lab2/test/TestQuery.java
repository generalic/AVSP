package hr.fer.zemris.avsp.lab2.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by generalic on 10/05/17.
 * lab3
 */
public class TestQuery {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("testfile.txt");

        long count = Files.lines(path, StandardCharsets.UTF_8)
            .filter(l -> !l.startsWith("q"))
            .mapToInt(l -> {
                char[] array = l.toCharArray();
                return array.length;
            })
            .sum();

        System.out.println(count);
    }
}
