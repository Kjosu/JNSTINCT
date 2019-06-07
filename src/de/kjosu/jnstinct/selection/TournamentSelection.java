package de.kjosu.jnstinct.selection;

import java.util.Collections;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;

public class TournamentSelection implements Selection {

	public int size = 5;
	public double probability = .5;

	@Override
	public <T extends Genome<T>> T select(final Neat<T> neat) {
		if (size > neat.getPopulationSize()) {
			throw new IllegalStateException("Tournament size should be lower than the population size");
		}

		final ThreadLocalRandom random = ThreadLocalRandom.current();

		final Stack<T> individuals = new Stack<>();
		for (int i = 0; i < size; i++) {
			final T genome = neat.getPopulation().get(random.nextInt(neat.getPopulation().size()));
			individuals.add(genome);
		}

		Collections.sort(individuals);

		for (final T genome : individuals) {
			if (random.nextDouble() < probability) {
				return genome;
			}
		}

		return individuals.lastElement();
	}

}
