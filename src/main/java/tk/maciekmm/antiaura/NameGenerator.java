package tk.maciekmm.antiaura;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NameGenerator {
    private static final List<Alphabet> letters = Collections.unmodifiableList(Arrays.asList(Alphabet.values()));

    public static String newName() {
	Random rand = new Random();
	int size = 3 + rand.nextInt(4);

	StringBuilder stringBuilder = new StringBuilder();
	while (size > 0) {
	    size--;
	    stringBuilder.append(getRandomLetter());
	}
	stringBuilder.append(rand.nextInt(999999));

	return stringBuilder.toString();
    }

    private static String getRandomLetter() {
	Random rand = new Random();
	return ((Alphabet) letters.get(rand.nextInt(letters.size()))).name();
    }

    private static enum Alphabet {
	a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z;
    }
}