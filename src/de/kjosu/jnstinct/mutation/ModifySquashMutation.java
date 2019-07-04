package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.activation.Squash;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.MapUtils;

public class ModifySquashMutation implements Mutation {

	public boolean mutateOutput = true;
	public Squash[] allowed = Squash.values();

	@Override
	public <T extends Genome<T>> boolean mutate(final Neat<T> neat, final T g) {
		if (!mutateOutput && g.getHiddenSize() < 1) {
			return false;
		}

		final int origin = g.getInputSize() + ((mutateOutput) ? g.getOutputSize() : 0);
		final int bound = g.getNodeSize();

		final NodeGene node = MapUtils.randomValue(g.getNodes(), origin, bound);
		node.mutate(this);

		return true;
	}

}
