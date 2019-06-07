package de.kjosu.jnstinct.mutation;

import java.util.List;
import java.util.Stack;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.Pair;

public class AddBackConnectionMutation implements Mutation {

	@Override
	public <T extends Genome<T>> void mutate(final Neat<T> neat, final T g) {
		final List<Pair<NodeGene, NodeGene>> available = new Stack<>();
		final int highest = g.highestNodeID();

		for (int i = g.getInputSize(); i <= highest; i++) {
			final NodeGene node1 = g.getNode(i);

			if (node1 == null) {
				continue;
			}

			for (int j = g.getInputSize(); j < i; j++) {
				final NodeGene node2 = g.getNode(j);

				if (node2 == null) {
					continue;
				}

				if (!g.isProjectingTo(node1, node2)) {
					available.add(new Pair<>(node1, node2));
				}
			}
		}

		if (available.isEmpty()) {
			return;
		}

		final Pair<NodeGene, NodeGene> pair = available.get(random.nextInt(available.size()));

		g.connect(pair.getKey(), pair.getValue(), 0);
	}

}
