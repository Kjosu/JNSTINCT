package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.util.MapUtils;

public class SubSelfConnectionMutation implements Mutation {

	@Override
	public <T extends Genome<T>> void mutate(final Neat<T> neat, final T g) {
		if (g.getSelfs().isEmpty()) {
			return;
		}

		final ConnectionGene c = MapUtils.randomValue(g.getSelfs());
		g.disconnect(c.getFromNode(), c.getToNode(), true);
	}

}
