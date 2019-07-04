package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.MapUtils;

public class SubNodeMutation implements Mutation {

	public boolean keepGates = true;

	@Override
	public <T extends Genome<T>> boolean mutate(final Neat<T> neat, final T g) {
		if (g.getHiddenSize() < 1) {
			return false;
		}

		final int origin = g.getInputSize() + g.getOutputSize();
		final int bound = g.getNodeSize();

		final NodeGene node = MapUtils.randomValue(g.getNodes(), origin, bound);
		g.removeNode(node);

		return true;
	}

}
