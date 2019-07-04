package de.kjosu.jnstinct.mutation;

import de.kjosu.jnstinct.core.ConnectionGene;
import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;
import de.kjosu.jnstinct.util.MapUtils;

public class ToggleConnectionMutation implements Mutation {

    @Override
    public <T extends Genome<T>> boolean mutate(Neat<T> neat, T g) {
        if (g.getConnections().isEmpty()) {
            return false;
        }

        ConnectionGene c = MapUtils.randomValue(g.getConnections());
        c.setEnabled(!c.isEnabled());

        return true;
    }
}
