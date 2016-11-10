package tk.maciekmm.antiaura;

import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;

public class NameGenerator {
    private static Random random = new Random();

    public static String newName() {
        int size = 3 + random.nextInt(6);
        return RandomStringUtils.randomAlphabetic(size);
    }
}
