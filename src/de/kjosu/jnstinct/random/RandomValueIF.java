package de.kjosu.jnstinct.random;

import java.util.concurrent.ThreadLocalRandom;

public interface RandomValueIF {

	final ThreadLocalRandom random = ThreadLocalRandom.current();

	public double random();

}
