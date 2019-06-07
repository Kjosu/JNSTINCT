package de.kjosu.jnstinct.core;

import java.util.List;
import java.util.Stack;

public abstract class DefaultNeat<T extends Genome<T>> extends Neat<T> {

	public DefaultNeat(final int inputSize, final int outputSize, final int populationSize) {
		super(inputSize, outputSize, populationSize);
	}

	@Override
	public void evolve() {
		evaluateFitness();
		sort();

		final List<T> nextGeneration = new Stack<>();
		final List<T> elitists = new Stack<>();

		for (int i = 0; i < elitism; i++) {
			elitists.add(population.get(i));
		}

		for (int i = 0; i < populationSize - elitism; i++) {
			nextGeneration.add(getOffspring());
		}

		population.clear();
		population.addAll(nextGeneration);

		mutate();

		population.addAll(elitists);

		generation++;
	}

	private T getOffspring() {
		final T p1 = getParent();
		final T p2 = getParent();

		return createGenome(this, p1, p2, equal);
	}

	private T getParent() {
		return selection.select(this);
	}

}
