package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.activation.Squash;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.core.NodeGene;
import de.kjosu.jnstinct.util.MapUtils;

public class SwapNodesMutation implements Mutation {

	public boolean mutateOutput = true;

	@Override
	public <T extends Genome<T>> void mutate(final Neat<T> neat, final T g) {
		if ((mutateOutput && g.getNodeSize() - g.getInputSize() < 2) ||
				(!mutateOutput && g.getNodeSize() - g.getInputSize() - g.getOutputSize() < 2)) {
			return;
		}

		final int origin = g.getInputSize() + ((mutateOutput) ? 0 : g.getOutputSize());
		final int bound = g.getNodeSize();

		final NodeGene node1 = MapUtils.randomValue(g.getNodes(), origin, bound);
		final NodeGene node2 = MapUtils.randomValue(g.getNodes(), origin, bound);

		final double biasTemp = node1.getBias();
		final Squash squashTemp = node1.getSquash();

		node1.setBias(node2.getBias());
		node1.setSquash(node2.getSquash());
		node2.setBias(biasTemp);
		node2.setSquash(squashTemp);
	}

}
