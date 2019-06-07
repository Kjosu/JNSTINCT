package de.kjosu.jnstinct.core.speciation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.Genome;

public class Species<T extends Genome<T>> implements Comparable<Species<T>> {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final SpeciationNeat<T> neat;

    private T mascot;
    private final List<T> members = new Stack<>();
    private final Map<T, Double> adjustedFitnesses = new HashMap<>();

    private int offspringSize;

    private double averageAdjustedFitness;
    private double adjustedFitnessSum;

    public Species(final SpeciationNeat<T> neat, final T mascot) {
        this.neat = neat;
        this.mascot = mascot;

        addMember(mascot);
    }

    public void evolve() {
        mascot = members.get(random.nextInt(members.size()));

        adjustedFitnesses.clear();
        members.clear();
    }

    public double evaluate() {
        adjustedFitnessSum = 0;

        for (final T g1 : members) {
            int dividend = 0;

            for (final T g2 : members) {
                if (SpeciationNeat.distance(neat, g1, g2) <= neat.compatibilityThreshold) {
                    dividend++;
                }
            }

            final double adjustedFitness = g1.getFitness() / dividend;

            adjustedFitnessSum += adjustedFitness;
            adjustedFitnesses.put(g1, adjustedFitness);
        }

        averageAdjustedFitness = adjustedFitnessSum / members.size();

        return adjustedFitnessSum;
    }

    public void eliminateLowest() {
    	if (members.size() < 2) {
    		return;
    	}

        Collections.sort(members, Collections.reverseOrder());


        final int maxIndex = (int) (members.size() * neat.weakEliminatePercentage);
        int index = 0;

        final Iterator<T> iterator = members.iterator();
        while (iterator.hasNext()) {
        	if (index >= maxIndex) {
        		break;
        	}

        	iterator.next();
        	iterator.remove();
        	index++;
        }
    }

    public T randomMember() {
        return members.get(random.nextInt(members.size()));
    }

    public double getAdjustedFitness(final T member) {
        return adjustedFitnesses.get(member);
    }

    public void addMember(final T genome) {
        members.add(genome);
    }

    public void removeMember(final T genome) {
        members.remove(genome);
    }

    public List<T> getMembers() {
        return members;
    }

    public T getMascot() {
        return mascot;
    }

    public void setMascot(final T mascot) {
        this.mascot = mascot;
    }

    public int getOffspringSize() {
        return offspringSize;
    }

    public void setOffspringSize(final int offspringSize) {
        this.offspringSize = offspringSize;
    }

    public double getAdjustedFitnessSum() {
        return adjustedFitnessSum;
    }

	@Override
	public int compareTo(final Species<T> o) {
		if (getAdjustedFitnessSum() == o.getAdjustedFitnessSum()) {
            return 0;
        } else if (getAdjustedFitnessSum() > o.getAdjustedFitnessSum()) {
            return 1;
        } else {
            return -1;
        }
	}
}
