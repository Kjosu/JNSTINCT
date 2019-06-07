package de.kjosu.jnstinct.core;

public class ConnectionGene extends Gene {

	/**
	 * Reference id of the input node
	 */
	private final int fromNode;

	/**
	 * Reference id of the output node
	 */
	private final int toNode;

	/**
	 * Connection gain
	 */
	private double gain = 1;

	/**
	 * Connection weight
	 */
	private double weight;

	/**
	 * Reference id of the gater node
	 */
	private int gaterNode = -1;

	/**
	 * Enabled state of the connection
	 */
	private boolean enabled = true;

	/**
	 * Creates a new ConnectionGene instance.
	 * The genes id is created by the ConnectionGene.innovationID method
	 *
	 * @param fromNode - Input node id
	 * @param toNode - Output node id
	 * @param weight - Connection weight
	 * @see de.kjosu.jnstinct.core.ConnectionGene#innovationID(int, int)
	 */
	public ConnectionGene(final int fromNode, final int toNode, final double weight) {
		super(innovationID(fromNode, toNode));

		this.fromNode = fromNode;
		this.toNode = toNode;

		this.weight = weight;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(final double gain) {
		this.gain = gain;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(final double weight) {
		this.weight = weight;
	}

	public int getGaterNode() {
		return gaterNode;
	}

	public void setGaterNode(final int gaterNode) {
		this.gaterNode = gaterNode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public int getFromNode() {
		return fromNode;
	}

	public int getToNode() {
		return toNode;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof ConnectionGene)) {
			return false;
		}

		final ConnectionGene c = (ConnectionGene) o;
		return c.getFromNode() == getFromNode() && c.getToNode() == getToNode();
	}

	/**
	 * Creates and returns a ungated copy of this ConnectionGene
	 */
	@Override
	public ConnectionGene clone() {
		final ConnectionGene clone = new ConnectionGene(fromNode, toNode, weight);
		clone.setGain(gain);
		clone.setEnabled(enabled);

		return clone;
	}

	/**
	 * Creates a unique id with the following pairing function:<br/>
	 * <br/>
	 * x = 1 / 2 * (a + b) * (a + b + 1) + b;
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static int innovationID(final int a, final int b) {
		return (int) (1D / 2D * (a + b) * (a + b + 1D) + b);
	}
}
