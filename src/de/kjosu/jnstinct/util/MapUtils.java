package de.kjosu.jnstinct.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MapUtils {

	public static final ThreadLocalRandom random = ThreadLocalRandom.current();

	public static <T> T randomKey(final Map<T, ?> map) {
		return randomKey(map, 0, map.size());
	}

	public static <T> T randomKey(final Map<T, ?> map, final int origin, final int bound) {
		final List<T> list = new ArrayList<>(map.keySet());
		final int index;

		if (origin >= bound) {
			index = bound - 1;
		} else {
			index = random.nextInt(origin, bound);
		}

		return list.get(index);
	}

	public static <T> T randomValue(final Map<?, T> map) {
		return randomValue(map, 0, map.size());
	}

	public static <T> T randomValue(final Map<?, T> map, final int origin, final int bound) {
		final List<T> list = new ArrayList<>(map.values());
		final int index;

		if (origin >= bound) {
			index = bound - 1;
		} else {
			index = random.nextInt(origin, bound);
		}

		return list.get(index);
	}

	public static int highestKey(final Map<Integer, ?> map) {
		int highest = 0;

		for (final int i : map.keySet()) {
			if (i > highest) {
				highest = i;
			}
		}

		return highest;
	}
}
