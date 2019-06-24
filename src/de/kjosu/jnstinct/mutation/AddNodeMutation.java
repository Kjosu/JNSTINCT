package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.MapUtils;

public class AddNodeMutation implements Mutation {

	@Override
	public <T extends Genome<T>> void mutate(final Neat<T> neat, final T g) {
		if (g.getConnections().isEmpty()) {
			return;
		}

		final ConnectionGene connection = MapUtils.randomValue(g.getConnections());
		final int gater = connection.getGaterNode();

		g.disconnect(connection.getFromNode(), connection.getToNode(), true);

		final NodeGene node = g.createNode(connection.getId() + g.getInputSize() + g.getOutputSize());
		node.mutate(neat.modifySquash);

		final ConnectionGene newConn1 = g.connect(connection.getFromNode(), node.getId(), 0);
		final ConnectionGene newConn2 = g.connect(node.getId(), connection.getToNode(), connection.getWeight());

		if (gater != -1) {
			g.gate(gater, (random.nextBoolean()) ? newConn1 : newConn2);
		}
	}

}
