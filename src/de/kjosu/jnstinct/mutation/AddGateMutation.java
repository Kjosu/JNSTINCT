package de.kjosu.jnstinct.mutation;

import java.util.List;
import java.util.Stack;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.MapUtils;

public class AddGateMutation implements Mutation {

	@Override
	public <T extends Genome<T>> boolean mutate(final Neat<T> neat, final T g) {
		final List<ConnectionGene> allConnections = new Stack<>();
		allConnections.addAll(g.getConnections().values());
		allConnections.addAll(g.getSelfs().values());

		final List<ConnectionGene> possible = new Stack<>();
		for (final ConnectionGene c : allConnections) {
			if (c.getGaterNode() == -1) {
				possible.add(c);
			}
		}

		if (possible.isEmpty()) {
			return false;
		}

		final NodeGene node = MapUtils.randomValue(g.getNodes());
		final ConnectionGene c = possible.get(random.nextInt(possible.size()));

		return g.gate(node, c);
	}

}
