package de.kjosu.jnstinct.core.speciation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;

public abstract class SpeciationNeat<T extends Genome<T>> extends Neat<T> {

    public double excessCoefficient = 1D;
    public double disjointCoefficient = 1D;
    public double weightCoefficient = 1D;
    public double compatibilityThreshold = 3;
    public double weakEliminatePercentage = .2;
    public int maxStaleness = 20;

    private final List<Species<T>> species = new Stack<>();
    private final Map<Genome<T>, Species<T>> relations = new HashMap<>();

    private double highestAdjustedFitnessSum;
    private int staleness;

    public SpeciationNeat(final int inputSize, final int outputSize, final int populationSize) {
        super(inputSize, outputSize, populationSize);
    }

    @Override
    public void reset(final int inputSize, final int outputSize, final int populationSize) {
        super.reset(inputSize, outputSize, populationSize);

        if (species == null) {
            return;
        }

        species.clear();
        relations.clear();

        highestAdjustedFitnessSum = 0;
        staleness = 0;
    }

    @Override
    public void evolve() {
        evaluateFitness();
        sort();

        speciate();
        evaluateSpecies();

        final List<T> offsprings = breed();
        final List<T> elitists = new Stack<>();

        for (int i = 0; i < elitism && i < population.size(); i++) {
        	elitists.add(population.get(i));
        }

        if (offsprings.isEmpty()) {
        	for (int i = 0; i < populationSize - elitism; i++) {
        		final T g1 = selection.select(this);
        		final T g2 = selection.select(this);

        		offsprings.add(createGenome(this, g1, g2, equal));
        	}
        }

        population.clear();
        relations.clear();

        population.addAll(offsprings);

        mutate();

	for (T genome : elitists) {
		genome.setFitness(0);
	}
	    
        population.addAll(elitists);

        generation++;
    }

    private void speciate() {
        for (final Species<T> s : species) {
            s.evolve();
        }

        for (final T genome : population) {
            Species<T> chosen = null;

            for (final Species<T> s : species) {
                final double distance = distance(this, genome, s.getMascot());

                if (distance <= compatibilityThreshold) {
                    s.addMember(genome);
                    chosen = s;
                    break;
                }
            }

            if (chosen == null) {
                chosen = new Species<>(this, genome);
                species.add(chosen);
            }

            relations.put(genome, chosen);
        }
    }

    private void evaluateSpecies() {
        double sum = 0;

        for (final Species<T> s : species) {
            sum += s.evaluate();
        }

        if (sum > highestAdjustedFitnessSum) {
            highestAdjustedFitnessSum = sum;
            staleness = 0;
        } else {
            staleness++;
        }

        final int maxOffsprings = populationSize - elitism;

        if (staleness > maxStaleness) {
	        Collections.sort(species);

	        final int o1 = maxOffsprings / ((species.size() > 1) ? 2 : 1);
	        final int o2 = maxOffsprings - o1;

	        species.get(0).setOffspringSize(o1);

	        if (species.size() > 1) {
	            species.get(1).setOffspringSize(o2);

	            for (int i = 2; i < species.size(); i++) {
	                species.get(i).setOffspringSize(0);
	            }
	        }
        } else {
	        double excess = 0;
	        for (final Species<T> s : species) {
	            final double rawOffspringSize = s.getAdjustedFitnessSum() / sum * maxOffsprings;
	            int offspringSize = (int) rawOffspringSize;

	            excess += rawOffspringSize - offspringSize;

	            if (excess >= 1) {
	                offspringSize++;
	                excess--;
	            }

	            s.setOffspringSize(offspringSize);
	        }
        }
    }

    private List<T> breed() {
        final List<T> nextGeneration = new Stack<>();
        final Iterator<Species<T>> iterator = species.iterator();

    	while (iterator.hasNext()) {
    		final Species<T> s = iterator.next();

    		if (s.getMembers().isEmpty()) {
    			iterator.remove();
    		}

            s.eliminateLowest();

            for (int i = 0; i < s.getOffspringSize(); i++) {
                final T parent1 = s.randomMember();
                final T parent2 = selection.select(this);

                final T offspring = createGenome(this, parent1, parent2, equal);

                nextGeneration.add(offspring);
            }
        }

        return nextGeneration;
    }

    public static <T extends Genome<T>> double distance(final SpeciationNeat<T> neat, final T g1, final T g2) {
        if (g1.getInputSize() != g2.getInputSize() || g1.getOutputSize() != g2.getOutputSize()) {
            throw new IllegalArgumentException("Genomes don't have the same input/output size");
        }

        final int size1 = g1.highestConnectionID();
        final int size2 = g2.highestConnectionID();

        int excess = 0;
        int disjoint = 0;
        int matching = 0;
        double weightDivSum = 0;

        for (int i = 0; i <= size1 || i <= size2; i++) {
            final ConnectionGene c1 = g1.getConnection(i);
            final ConnectionGene c2 = g2.getConnection(i);

            if (Objects.equals(c1, c2)) {
                if (c1 == null) {
                    continue;
                }

                weightDivSum += Math.abs(c1.getWeight() - c2.getWeight());
                matching++;
            } else if (c1 != null) {
                excess++;
            } else if (c2 != null) {
                disjoint++;
            }
        }

        final double avgWeightDiv = (matching == 0) ? 0 : weightDivSum / matching;
        double N = Math.max(size1, size2);

        if (N < 20) {
            N = 1;
        }

        return (excess * neat.excessCoefficient / N) +
                (disjoint * neat.disjointCoefficient / N) +
                (avgWeightDiv * neat.weightCoefficient);
    }
}
