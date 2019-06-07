package de.kjosu.jnstinct.random;

public class RangedRandom implements RandomValueIF {

	public double min;
	public double max;

	@Override
	public double random() {
		return random.nextDouble(min, max);
	}

}
