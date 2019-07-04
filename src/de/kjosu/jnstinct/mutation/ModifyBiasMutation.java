package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.MapUtils;

public class ModifyBiasMutation implements Mutation {

	public double min = -1;
	public double max = 1;

	@Override
	public <T extends Genome<T>> boolean mutate(final Neat<T> neat, final T g) {
		final int origin = g.getInputSize();
		final int bound = g.getNodeSize();

		final NodeGene node = MapUtils.randomValue(g.getNodes(), origin, bound);
		node.mutate(this);

		return true;
	}

}
