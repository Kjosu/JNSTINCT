package de.kjosu.jnstinct.mutation;

import java.util.List;
import java.util.Stack;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;

public class AddSelfConnectionMutation implements Mutation {

	@Override
	public <T extends Genome<T>> boolean mutate(final Neat<T> neat, final T g) {
		final List<NodeGene> possible = new Stack<>();
		final int highest = g.highestNodeID();

		for (int i = g.getInputSize(); i <= highest; i++) {
			final NodeGene node = g.getNode(i);

			if (node == null) {
				continue;
			}

			if (node.getSelf() == -1) {
				possible.add(node);
			}
		}

		if (possible.isEmpty()) {
			return false;
		}

		final NodeGene node = possible.get(random.nextInt(possible.size()));

		return g.connect(node, node, 0) != null;
	}

}
