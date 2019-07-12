package de.kjosu.jnstinct.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.NodeGene.NodeType;
import de.kjosu.jnstinct.mutation.Mutation;
import de.kjosu.jnstinct.util.MapUtils;

public abstract class Genome<T extends Genome<T>> implements Comparable<T> {

	private final ThreadLocalRandom random = ThreadLocalRandom.current();
	private final Neat<T> neat;

	private final int inputSize;
	private final int outputSize;

	private final Map<Integer, NodeGene> nodes = new TreeMap<>();
	private final Map<Integer, ConnectionGene> connections = new HashMap<>();
	private final Map<ConnectionGene, Integer> gates = new HashMap<>();
	private final Map<Integer, ConnectionGene> selfs = new HashMap<>();

	private int staleness;
	private double fitness;

	private final T father;
	private final T mother;

	public Genome(final Neat<T> neat, final int inputSize, final int outputSize, final boolean createGenes) {
		this.neat = neat;

		this.inputSize = inputSize;
		this.outputSize = outputSize;

		father = null;
		mother = null;

		if (!createGenes) {
			return;
		}

		for (int i = 0; i < inputSize + outputSize; i++) {
			final NodeGene node = neat.createNode(i);
			nodes.put(node.getId(), node);
		}

		for (int i = 0; i < inputSize; i++) {
			for (int j = 0; j < outputSize; j++) {
				final int oid = inputSize + j;
				final double weight = random.nextDouble() * inputSize * Math.sqrt(2D / inputSize);

				connect(i, oid, weight);
			}
		}
	}

	public Genome(final Neat<T> neat, final T father, final T mother, final boolean equal) {
		if (father.getInputSize() != mother.getInputSize() || father.getOutputSize() != mother.getOutputSize()) {
			throw new IllegalArgumentException("Genomes don't have the same input/output size");
		}

		this.neat = neat;
		this.inputSize = father.getInputSize();
		this.outputSize = father.getOutputSize();

		this.father = father;
		this.mother = mother;

		final double fitness1 = father.getFitness();
		final double fitness2 = mother.getFitness();

		int size;
		if (equal || fitness1 == fitness2) {
			final int min = Math.min(father.highestNodeID(), mother.highestNodeID());
			final int max = Math.max(father.highestNodeID(), mother.highestNodeID());

			if (min == max) {
				size = min;
			} else {
				size = random.nextInt(min, max);
			}
		} else if (fitness1 > fitness2) {
			size = father.highestNodeID();
		} else {
			size = mother.highestNodeID();
		}

		final Map<Integer, Integer> nodeIds = MapUtils.mergeKeys(father.getNodes(), mother.getNodes());
		final Map<Integer, Integer> connectionIds = MapUtils.mergeKeys(father.getConnections(), mother.getConnections());
		final Map<Integer, Integer> selfConnectionIds = MapUtils.mergeKeys(father.getSelfs(), mother.getSelfs());

		for (final int i : nodeIds.keySet()) {
			if (i > size) {
				continue;
			}

			final NodeGene node1 = father.getNode(i);
			final NodeGene node2 = mother.getNode(i);

			NodeGene chosen = null;
			if (Objects.equals(node1, node2)) {
				chosen = (random.nextBoolean()) ? node1 : node2;
			} else if (node1 != null) {
				chosen = node1;
			} else if (node2 != null) {
				chosen = node2;
			}

			if (chosen == null) {
				continue;
			}

			final NodeGene node = new NodeGene(chosen.getId(), chosen.getType());
			node.setBias(chosen.getBias());
			node.setSquash(chosen.getSquash());

			getNodes().put(node.getId(), node);
		}

		for (final int i : connectionIds.keySet()) {
			final ConnectionGene c1 = father.getConnection(i);
			final ConnectionGene c2 = mother.getConnection(i);

			ConnectionGene chosen;
			if (Objects.equals(c1, c2)) {
				chosen = (random.nextBoolean()) ? c1 : c2;
			} else if ((equal || fitness1 >= fitness2) && c1 != null) {
				chosen = c1;
			} else {
				chosen = c2;
			}

			if (chosen == null || !containsNode(chosen.getFromNode())
					|| !containsNode(chosen.getToNode())) {
				continue;
			}

			final ConnectionGene c = connect(chosen.getFromNode(), chosen.getToNode(), chosen.getWeight());

			if (chosen.getGaterNode() != -1 && containsNode(chosen.getGaterNode())) {
				gate(chosen.getGaterNode(), c);
			}
		}

		for (final int i : selfConnectionIds.keySet()) {
			final ConnectionGene c1 = father.getSelf(i);
			final ConnectionGene c2 = mother.getSelf(i);

			ConnectionGene chosen;
			if (Objects.equals(c1, c2)) {
				chosen = (random.nextBoolean()) ? c1 : c2;
			} else if ((equal || fitness1 >= fitness2) && c1 != null) {
				chosen = c1;
			} else {
				chosen = c2;
			}

			if (chosen == null || !containsNode(chosen.getFromNode())) {
				continue;
			}

			connect(chosen.getFromNode(), chosen.getToNode(), chosen.getWeight());
		}
	}

	public double[] activate(final double[] input) {
		if (input.length != inputSize) {
			throw new IllegalArgumentException("Invalid input array size");
		}

		final double[] output = new double[outputSize];

		for (int i = 0; i < inputSize; i++) {
			activateNode(nodes.get(i), input[i]);
		}

		for (final NodeGene node : nodes.values()) {
			if (node.getType() != NodeType.Hidden) {
				continue;
			}

			activateNode(node);
		}

		for (int i = 0; i < outputSize; i++) {
			final int index = inputSize + i;

			output[i] = activateNode(nodes.get(index));
		}

		return output;
	}

	private double activateNode(final int node, final double input) {
		return activateNode(nodes.get(node), input);
	}

	private double activateNode(final NodeGene node, final double input) {
		if (node == null) {
			throw new IllegalArgumentException("Node can't be null");
		}

		node.setActivation(input);
		return input;
	}

	private double activateNode(final int node) {
		return activateNode(nodes.get(node));
	}

	private double activateNode(final NodeGene node) {
		final ConnectionGene self = selfs.get(node.getSelf());
		double selfGain = 0;
		double selfWeight = 0;

		if (self != null) {
			selfGain = self.getGain();
			selfWeight = self.getWeight();
		}

		node.setOld(node.getState());
		node.setState(selfGain * selfWeight * node.getState() + node.getBias());

		for (final int id : node.getIncoming()) {
			final ConnectionGene c = connections.get(id);

			if (!c.isEnabled()) {
				continue;
			}

			final NodeGene from = nodes.get(c.getFromNode());

			node.setState(node.getState() + from.getActivation() * c.getWeight() * c.getGain());
		}

		node.activate();

		for (final int id : node.getGates()) {
			final ConnectionGene c = getConnection(id);

			if (c == null || !c.isEnabled()) {
				continue;
			}

			c.setGain(node.getActivation());
		}

		return node.getActivation();
	}

	public ConnectionGene connect(final int fromNode, final int toNode, final double weight) {
		return connect(nodes.get(fromNode), nodes.get(toNode), weight);
	}

	public ConnectionGene connect(final NodeGene fromNode, final NodeGene toNode, final double weight) {
		if (fromNode == null || toNode == null) {
			throw new IllegalArgumentException("From/To node can't be null");
		}

		ConnectionGene c;

		if (fromNode.equals(toNode)) {
			if (fromNode.getSelf() == -1) {
				c = new ConnectionGene(fromNode.getId(), toNode.getId(), weight);
				selfs.put(c.getId(), c);
			} else {
				c = selfs.get(fromNode.getSelf());
			}
		} else if ((c = getConnection(isProjectingTo(fromNode, toNode))) == null) {
			c = new ConnectionGene(fromNode.getId(), toNode.getId(), weight);
			toNode.addIncoming(c.getId());
			fromNode.addOutgoing(c.getId());

			connections.put(c.getId(), c);
		}

		return c;
	}

	public void disconnect(final int fromNode, final int toNode, final boolean twosided) {
		disconnect(nodes.get(fromNode), nodes.get(toNode), twosided);
	}

	public void disconnect(final NodeGene fromNode, final NodeGene toNode, final boolean twosided) {
		if (fromNode == null || toNode == null) {
			throw new IllegalArgumentException("From/To node can't be null");
		}

		if (fromNode.equals(toNode)) {
			selfs.remove(fromNode.getSelf());
			fromNode.setSelf(-1);

			return;
		}

		for (final int id : fromNode.getOutgoing()) {
			final ConnectionGene c = connections.get(id);

			if (c == null) {
				continue;
			}

			if (c.getToNode() != toNode.getId()) {
				continue;
			}

			connections.remove(id);
			toNode.removeIncoming(id);

			if (c.getGaterNode() != -1) {
				ungate(c);
			}

			break;
		}

		if (twosided) {
			disconnect(toNode, fromNode, false);
		}
	}

	public boolean gate(final int node, final ConnectionGene connection) {
		return gate(nodes.get(node), connection);
	}

	public boolean gate(final NodeGene node, final ConnectionGene connection) {
		if (connection.getGaterNode() != -1) {
			return false;
		}

		node.addGate(connection.getId());
		connection.setGaterNode(node.getId());

		gates.put(connection, node.getId());
		return true;
	}

	public boolean ungate(final ConnectionGene c) {
		if (c == null) {
			throw new IllegalArgumentException("Connection can't be null");
		}

		if (c.getGaterNode() == -1) {
			return false;
		}

		final NodeGene node = nodes.get(c.getGaterNode());

		if (node == null) {
			throw new IllegalStateException("Gater node isn't part of this genome");
		}

		node.removeGate(c.getId());
		c.setGaterNode(-1);
		c.setGain(1);

		gates.remove(c);

		return true;
	}

	public void removeNode(final int id) {
		removeNode(nodes.get(id));
	}

	public void removeNode(final NodeGene node) {
		if (node == null) {
			throw new IllegalArgumentException("Node can't be null");
		}

		disconnect(node, node, false);

		final List<Integer> gaterNodes = new Stack<>();
		final List<Integer> inputNodes = new Stack<>();
		final List<Integer> outputNodes = new Stack<>();

		for (int i = node.getIncoming().size() - 1; i >= 0; i--) {
			final ConnectionGene c = connections.get(node.getIncoming().get(i));

			if (neat.subNode.keepGates && c.getGaterNode() != -1 && c.getGaterNode() != node.getId()) {
				gaterNodes.add(c.getGaterNode());
			}

			inputNodes.add(c.getFromNode());
			disconnect(nodes.get(c.getFromNode()), node, false);
		}

		for (int i = node.getOutgoing().size() - 1; i >= 0; i--) {
			final ConnectionGene c = connections.get(node.getOutgoing().get(i));

			if (c == null) {
				continue;
			}

			if (neat.subNode.keepGates && c.getGaterNode() != -1 && c.getGaterNode() != node.getId()) {
				gaterNodes.add(c.getGaterNode());
			}

			outputNodes.add(c.getToNode());
			disconnect(node, nodes.get(c.getToNode()), false);
		}

		final List<ConnectionGene> connections = new Stack<>();
		for (final int input : inputNodes) {
			for (final int output : outputNodes) {
				if (isProjectingTo(input, output) != -1) {
					continue;
				}

				final ConnectionGene c = connect(input, output, 0);
				connections.add(c);
			}
		}

		for (final int gater : gaterNodes) {
			if (connections.isEmpty()) {
				break;
			}

			final ConnectionGene c = connections.get(random.nextInt(connections.size()));

			gate(gater, c);
			connections.remove(c);
		}

		for (int i = node.getGates().size() - 1; i >= 0; i--) {
			final ConnectionGene c = getConnection(node.getGates().get(i));
			ungate(c);
		}

		disconnect(node, node, false);

		nodes.remove(node.getId());
	}

	public boolean mutate(final Mutation method) {
		if (method == null) {
			throw new IllegalArgumentException("Mutation method can't be null");
		}

		return method.mutate(neat, (T) this);
	}

	public void clear() {
		for (final NodeGene node : nodes.values()) {
			clearNode(node);
		}
	}

	public void clearNode(final int node) {
		clearNode(nodes.get(node));
	}

	public void clearNode(final NodeGene node) {
		if (node == null) {
			throw new IllegalArgumentException("Node can't be null");
		}

		for (final int id : node.getGates()) {
			final ConnectionGene c = getConnection(id);
			c.setGain(0);
		}

		node.clear();
	}

	public int isProjectingTo(final int sourceNode, final int targetNode) {
		return isProjectingTo(nodes.get(sourceNode), nodes.get(targetNode));
	}

	public int isProjectingTo(final NodeGene sourceNode, final NodeGene targetNode) {
		if (sourceNode == null || targetNode == null) {
			throw new IllegalArgumentException("Source/Target node can't be null");
		}

		if (sourceNode.equals(targetNode)) {
			return sourceNode.getSelf();
		}

		final Iterator<Integer> iterator = sourceNode.getOutgoing().iterator();

		while (iterator.hasNext()) {
			final ConnectionGene c = getConnection(iterator.next());

			if (c == null) {
				iterator.remove();
			} else if (c.getToNode() == targetNode.getId()) {
				return c.getId();
			}
		}

		return -1;
	}

	public int isProjectedBy(final int sourceNode, final int targetNode) {
		return isProjectedBy(nodes.get(sourceNode), nodes.get(targetNode));
	}

	public int isProjectedBy(final NodeGene sourceNode, final NodeGene targetNode) {
		if (sourceNode == null || targetNode == null) {
			throw new IllegalArgumentException("Source/Target node can't be null");
		}

		if (sourceNode.equals(targetNode)) {
			return sourceNode.getSelf();
		}

		final Iterator<Integer> iterator = sourceNode.getIncoming().iterator();

		while (iterator.hasNext()) {
			final ConnectionGene c = getConnection(iterator.next());

			if (c == null) {
				iterator.remove();
			} else if (c.getFromNode() == targetNode.getId()) {
				return c.getId();
			}
		}

		return -1;
	}

	public NodeGene createNode() {
		final NodeGene node = neat.createNode();
		nodes.put(node.getId(), node);

		return node;
	}

	public NodeGene createNode(final int id) {
		final NodeGene node = neat.createNode(id);
		nodes.put(node.getId(), node);

		return node;
	}

	public int highestNodeID() {
		return MapUtils.highestKey(nodes);
	}

	public int highestConnectionID() {
		return MapUtils.highestKey(connections);
	}

	public int highestSelfID() {
		return MapUtils.highestKey(selfs);
	}

	public Neat<T> getNeat() {
		return neat;
	}

	public int getInputSize() {
		return inputSize;
	}

	public int getOutputSize() {
		return outputSize;
	}

	public int getHiddenSize() {
		return nodes.size() - inputSize - outputSize;
	}

	public int getNodeSize() {
		return nodes.size();
	}

	public NodeGene getNode(final int id) {
		return nodes.get(id);
	}

	public boolean containsNode(final int id) {
		return nodes.containsKey(id);
	}

	public Map<Integer, NodeGene> getNodes() {
		return nodes;
	}

	public ConnectionGene getConnection(final int id) {
		return connections.get(id);
	}

	public ConnectionGene getSelf(final int id) {
		return selfs.get(id);
	}

	public Map<Integer, ConnectionGene> getConnections() {
		return connections;
	}

	public Map<ConnectionGene, Integer> getGates() {
		return gates;
	}

	public Map<Integer, ConnectionGene> getSelfs() {
		return selfs;
	}

	public int getStaleness() {
		return staleness;
	}

	public void setStaleness(final int staleness) {
		this.staleness = staleness;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(final double fitness) {
		this.fitness = fitness;
	}

	public T getFather() {
		return father;
	}

	public T getMother() {
		return mother;
	}

	@Override
	public int compareTo(final T genome) {
		if (genome.getFitness() == getFitness()) {
			return 0;
		} else if (genome.getFitness() > getFitness()) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return String.format("Genome (%.2f)", fitness);
	}
}
