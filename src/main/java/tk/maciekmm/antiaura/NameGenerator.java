package tk.maciekmm.antiaura;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Random;

public class NameGenerator {
    private static Random random = new Random();

    public static String newName() {
        int size = 3 + random.nextInt(6);
        return RandomStringUtils.randomAlphabetic(size);
    }
}
