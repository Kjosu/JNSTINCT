package de.kjosu.jnstinct.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.NodeGene.NodeType;
import de.kjosu.jnstinct.mutation.*;
import de.kjosu.jnstinct.selection.FitnessProportionateSelection;
import de.kjosu.jnstinct.selection.Selection;

public abstract class Neat<T extends Genome<T>> {

	protected final ThreadLocalRandom random = ThreadLocalRandom.current();

	public final AddConnectionMutation addConnection = new AddConnectionMutation();
	public final AddGateMutation addGate = new AddGateMutation();
	public final AddNodeMutation addNode = new AddNodeMutation();
	public final AddSelfConnectionMutation addSelfConnection = new AddSelfConnectionMutation();
	public final ModifyBiasMutation modifyBias = new ModifyBiasMutation();
	public final ModifySquashMutation modifySquash = new ModifySquashMutation();
	public final ModifyWeightMutation modifyWeight = new ModifyWeightMutation();
	public final SubConnectionMutation subConnection = new SubConnectionMutation();
	public final SubGateMutation subGate = new SubGateMutation();
	public final SubNodeMutation subNode = new SubNodeMutation();
	public final SubSelfConnectionMutation subSelfConnection = new SubSelfConnectionMutation();
	public final SwapNodesMutation swapNodes = new SwapNodesMutation();
	public final ToggleConnectionMutation toggleConnection = new ToggleConnectionMutation();

	public List<Mutation> mutations = new ArrayList<>(Arrays.asList(new Mutation[] {
		addNode,
		subNode,
		addConnection,
		subConnection,
		addGate,
		subGate,
		addSelfConnection,
		subSelfConnection,
		modifyWeight,
		modifyBias,
		modifySquash,
		swapNodes,
		toggleConnection
	}));

	public Selection selection = new FitnessProportionateSelection();

	public boolean equal = false;
	public boolean clear = false;
	public int elitism = 0;
	public double mutationRate = .3;
	public double mutationAmount = 1;

	public double growth = 1;

	public int maxNodes = 0;
	public int maxConnections = 0;
	public int maxGates = 0;

	protected int inputSize;
	protected int outputSize;

	protected int populationSize;

	protected final List<T> population = new Stack<>();
	protected T fittestGenome;

	protected final Map<Integer, NodeGene> globalNodes = new HashMap<>();

	protected int generation;

	public Neat(final int inputSize, final int outputSize, final int populationSize) {
		reset(inputSize, outputSize, populationSize);
	}

	public void reset(final int inputSize, final int outputSize, final int populationSize) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.populationSize = populationSize;

		population.clear();
		globalNodes.clear();

		for (int i = 0; i < inputSize + outputSize; i++) {
			final NodeGene node = new NodeGene(i, (i < inputSize) ? NodeType.Input : NodeType.Output);
			globalNodes.put(i, node);
		}

		for (int i = 0; i < populationSize; i++) {
			population.add(createGenome(this, inputSize, outputSize, true));
		}
	}

	public abstract void evolve();

	public void evaluateFitness() {
		fittestGenome = null;

		for (final T genome : population) {
			if (clear) {
				genome.clear();
			}

			final double fitness = evaluateFitness(genome) - genome.getHiddenSize() * growth;
			genome.setFitness(fitness);

			if (fittestGenome == null || fitness > fittestGenome.getFitness()) {
				fittestGenome = genome;
			}
		}
	}

	public Mutation selectMutationMethod(final T genome) {
		final Mutation method = mutations.get(random.nextInt(mutations.size()));

		if (maxNodes > 0 && method instanceof AddNodeMutation && genome.getNodeSize() >= maxNodes) {
			return null;
		}

		if (maxConnections > 0 && method instanceof AddConnectionMutation && genome.getConnections().size() >= maxConnections) {
			return null;
		}

		if (maxGates > 0 && method instanceof AddGateMutation && genome.getGates().size() >= maxGates) {
			return null;
		}

		return method;
	}

	public void mutate() {
		for (final T genome : population) {
			if (random.nextDouble() >= mutationRate) {
				continue;
			}

			for (int i = 0; i < mutationAmount; i++) {
				final Mutation method = selectMutationMethod(genome);

				genome.mutate(method);
			}
		}
	}

	/**
	 * Creates a new NodeGene with an globally incremented id
	 *
	 * @return a new NodeGene
	 */
	public NodeGene createNode() {
		final NodeGene node = new NodeGene(globalNodes.size(), NodeType.Hidden);
		globalNodes.put(node.getId(), node);

		return node.clone();
	}

	/**
	 * Returns a NodeGene with the given id, if a NodeGene with this id already existed.<br/>
	 * If no NodeGene with this id already existed, the result of the createNode() function is returned.
	 *
	 * @param id
	 * @return
	 *
	 * @see de.kjosu.jnstinct.core.Neat#createNode()
	 */
	public NodeGene createNode(final int id) {
		final NodeGene node = globalNodes.get(id);

		return (node == null) ? createNode() : node;
	}

	public void registerNode(NodeGene node) {
		globalNodes.put(node.getId(), node);
	}

	public void sort() {
		Collections.sort(population);
	}

	public int getInputSize() {
		return inputSize;
	}

	public int getOutputSize() {
		return outputSize;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public List<T> getPopulation() {
		return population;
	}

	public int getGeneration() {
		return generation;
	}

	public T getFittestGenome() {
		return fittestGenome;
	}

	public abstract T createGenome(Neat<T> neat, int inputSize, int outputSize, boolean createGenes);

	public abstract T createGenome(Neat<T> neat, T father, T mother, boolean equal);

	public abstract double evaluateFitness(T genome);
}
