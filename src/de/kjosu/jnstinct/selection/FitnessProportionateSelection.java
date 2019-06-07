package de.kjosu.jnstinct.selection;

import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;

public class FitnessProportionateSelection implements Selection {

	@Override
	public <T extends Genome<T>> T select(final Neat<T> neat) {
		final ThreadLocalRandom random = ThreadLocalRandom.current();

		double totalFitness = 0;
		double minimalFitness = 0;

		for (final T genome : neat.getPopulation()) {
			if (genome.getFitness() < minimalFitness) {
				minimalFitness = genome.getFitness();
			}

			totalFitness += genome.getFitness();
		}

		minimalFitness = Math.abs(minimalFitness);
		totalFitness += minimalFitness * neat.getPopulation().size();

		final double r = random.nextDouble() * totalFitness;
		double value = 0;

		for (final T genome : neat.getPopulation()) {
			value += genome.getFitness() + minimalFitness;

			if (r < value) {
				return genome;
			}
		}

		return neat.getPopulation().get(random.nextInt(neat.getPopulation().size()));
	}

}
