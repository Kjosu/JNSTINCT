package de.kjosu.jnstinct.util;

public class Pair<T, S> {

	private T key;
	private S value;

	public Pair(final T key, final S value) {
		this.key = key;
		this.value = value;
	}

	public T getKey() {
		return key;
	}

	public void setKey(final T key) {
		this.key = key;
	}

	public S getValue() {
		return value;
	}

	public void setValue(final S value) {
		this.value = value;
	}
}
