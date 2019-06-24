package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.util.MapUtils;

public class ToggleConnectionMutation implements Mutation {

    @Override
    public <T extends Genome<T>> void mutate(Neat<T> neat, T g) {
        ConnectionGene c = MapUtils.randomValue(g.getConnections());
        c.setEnabled(!c.isEnabled());
    }
}
