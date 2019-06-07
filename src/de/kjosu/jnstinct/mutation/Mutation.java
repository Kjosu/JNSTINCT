package de.kjosu.jnstinct.mutation;

import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;

public interface Mutation {

	final ThreadLocalRandom random = ThreadLocalRandom.current();

	public <T extends Genome<T>> void mutate(Neat<T> neat, T g);

}
