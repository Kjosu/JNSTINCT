package de.kjosu.jnstinct.mutation;

import java.util.List;
import java.util.Stack;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;

public class SubConnectionMutation implements Mutation {

	@Override
	public <T extends Genome<T>> boolean mutate(final Neat<T> neat, final T g) {
		if (g.getConnections().size() <= 1) {
			return false;
		}

		final List<ConnectionGene> possible = new Stack<>();

		for (final ConnectionGene c : g.getConnections().values()) {
			final NodeGene from = g.getNode(c.getFromNode());
			final NodeGene to = g.getNode(c.getToNode());

			if (from.getOutgoing().size() > 1 && to.getIncoming().size() > 1) {
				possible.add(c);
			}
		}

		if (possible.isEmpty()) {
			return false;
		}

		final ConnectionGene c = possible.get(random.nextInt(possible.size()));

		g.disconnect(c.getFromNode(), c.getToNode(), true);

		return true;
	}

}
