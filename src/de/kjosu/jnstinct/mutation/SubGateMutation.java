package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.util.MapUtils;

public class SubGateMutation implements Mutation {

	@Override
	public <T extends Genome<T>> void mutate(final Neat<T> neat, final T g) {
		if (g.getGates().isEmpty()) {
			return;
		}

		final ConnectionGene c = MapUtils.randomKey(g.getGates());
		g.ungate(c);
	}

}
