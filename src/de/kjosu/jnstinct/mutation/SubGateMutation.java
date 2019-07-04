package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.util.MapUtils;

public class SubGateMutation implements Mutation {

	@Override
	public <T extends Genome<T>> boolean mutate(final Neat<T> neat, final T g) {
		if (g.getGates().isEmpty()) {
			return false;
		}

		final ConnectionGene c = MapUtils.randomKey(g.getGates());
		return g.ungate(c);
	}

}
