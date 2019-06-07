package de.kjosu.jnstinct.selection;

import de.kjosu.jnstinct.core.Genome;
import de.kjosu.jnstinct.core.Neat;

import java.util.List;

public interface Selection {

	public <T extends Genome<T>> T select(Neat<T> neat);
}
