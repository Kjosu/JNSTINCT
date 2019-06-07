package de.kjosu.jnstinct.selection;

import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;

public class PowerSelection implements Selection {

	public double power = 4;

	@Override
	public <T extends Genome<T>> T select(final Neat<T> neat) {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		neat.sort();

		final int index = (int) (Math.pow(random.nextDouble(), power) * neat.getPopulation().size());
		return neat.getPopulation().get(index);
	}

}
