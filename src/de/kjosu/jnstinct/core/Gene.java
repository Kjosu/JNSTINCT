package de.kjosu.jnstinct.core;

public abstract class Gene {

	protected final int id;

	public Gene(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract Gene clone();

}
