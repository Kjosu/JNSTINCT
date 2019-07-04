package de.kjosu.jnstinct.mutation;

import java.util.concurrent.ThreadLocalRandom;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;

public interface Mutation {

	ThreadLocalRandom random = ThreadLocalRandom.current();

	 <T extends Genome<T>> boolean mutate(Neat<T> neat, T g);

}
