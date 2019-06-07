package de.kjosu.jnstinct.core;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.activation.Squash;
import de.kjosu.jnstinct.mutation.ModifyBiasMutation;
import de.kjosu.jnstinct.mutation.ModifySquashMutation;
import de.kjosu.jnstinct.mutation.Mutation;

public class NodeGene extends Gene {

	private final ThreadLocalRandom random = ThreadLocalRandom.current();

	public enum NodeType {
		Input, Hidden, Output
	}

	/**
	 * Nodes bias
	 */
	private double bias;

	/**
	 * Nodes Activation function
	 */
	private Squash squash = Squash.Logistic;

	/**
	 * Nodes type
	 */
	private final NodeType type;

	/**
	 * Last activation value
	 */
	private double activation;

	/**
	 * Last derivative value
	 */
	private double derivative;

	/**
	 * Current state
	 */
	private double state;

	/**
	 * Old state
	 */
	private double old;

	/**
	 * List of reference ids of incoming connections
	 */
	private final List<Integer> incoming = new Stack<>();

	/**
	 * List of reference ids of outgoing connections
	 */
	private final List<Integer> outgoing = new Stack<>();

	/**
	 * List of reference ids of gated connections
	 */
	private final List<Integer> gates = new Stack<>();

	/**
	 * Reference id of nodes self connection
	 */
	private int self = -1;

	public NodeGene(final int id, final NodeType type) {
		super(id);

		this.type = type;
	}

	public void activate() {
		activation = squash.activate(state, false);
		derivative = squash.activate(state, true);
	}

	public void mutate(final Mutation method) {
		if (method instanceof ModifySquashMutation) {
			final ModifySquashMutation m = (ModifySquashMutation) method;
			squash = m.allowed[random.nextInt(m.allowed.length)];
		} else if (method instanceof ModifyBiasMutation) {
			final ModifyBiasMutation m = (ModifyBiasMutation) method;
			bias += random.nextDouble(m.min, m.max);
		}
	}

	public void clear() {
		old = state = activation = 0;
	}

	public double getBias() {
		return bias;
	}

	public void setBias(final double bias) {
		this.bias = bias;
	}

	public Squash getSquash() {
		return squash;
	}

	public void setSquash(final Squash squash) {
		this.squash = squash;
	}

	public double getActivation() {
		return activation;
	}

	public void setActivation(final double activation) {
		this.activation = activation;
	}

	public double getState() {
		return state;
	}

	public void setState(final double state) {
		this.state = state;
	}

	public double getOld() {
		return old;
	}

	public void setOld(final double old) {
		this.old = old;
	}

	public int getSelf() {
		return self;
	}

	public void setSelf(final int self) {
		this.self = self;
	}

	public NodeType getType() {
		return type;
	}

	public void addIncoming(final int id) {
		if (!incoming.contains(id)) {
			incoming.add(id);
		}
	}

	public void removeIncoming(final int id) {
		incoming.remove((Object) id);
	}

	public List<Integer> getIncoming() {
		return incoming;
	}

	public void addOutgoing(final int id) {
		if (!outgoing.contains(id)) {
			outgoing.add(id);
		}
	}

	public void removeOutgoing(final int id) {
		outgoing.remove((Object) id);
	}

	public List<Integer> getOutgoing() {
		return outgoing;
	}

	public void addGate(final int id) {
		if (!gates.contains(id)) {
			gates.add(id);
		}
	}

	public void removeGate(final int id) {
		gates.remove((Object) id);
	}

	public List<Integer> getGates() {
		return gates;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof NodeGene)) {
			return false;
		}

		return ((NodeGene) o).getId() == getId();
	}

	/**
	 * Creates and returns a copy of this NodeGene, without any connections. Including incoming, outgoing, gated and self connection/s
	 */
	@Override
	public NodeGene clone() {
		final NodeGene clone = new NodeGene(id, type);
		clone.setBias(bias);
		clone.setSquash(squash);

		clone.setActivation(activation);
		clone.setState(state);
		clone.setOld(old);

		return clone;
	}

}
