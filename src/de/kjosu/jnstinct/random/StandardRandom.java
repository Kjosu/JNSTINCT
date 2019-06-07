package de.kjosu.jnstinct.random;

public class StandardRandom implements RandomValueIF {

	@Override
	public double random() {
		return random.nextDouble();
	}

}
