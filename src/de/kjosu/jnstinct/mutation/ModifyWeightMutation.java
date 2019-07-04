package de.kjosu.jnstinct.mutation;

import java.util.List;
import java.util.Stack;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;

public class ModifyWeightMutation implements Mutation {

	public double min = -1;
	public double max = 1;

	@Override
	public <T extends Genome<T>> boolean mutate(final Neat<T> neat, final T g) {
		final List<ConnectionGene> allConnections = new Stack<>();
		allConnections.addAll(g.getConnections().values());
		allConnections.addAll(g.getSelfs().values());

		if (allConnections.isEmpty()) {
			return false;
		}

		final ConnectionGene c = allConnections.get(random.nextInt(allConnections.size()));
		final double mod = random.nextDouble(min, max);

		c.setWeight(c.getWeight() + mod);

		return true;
	}

}
