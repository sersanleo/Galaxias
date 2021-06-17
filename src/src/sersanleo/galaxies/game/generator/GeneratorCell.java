package src.sersanleo.galaxies.game.generator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.solver.SolverCell;
import src.sersanleo.galaxies.util.Vector2i;

public class GeneratorCell extends Vector2i {
	protected final Set<Galaxy> galaxies;

	protected GeneratorCell(SolverCell cell) {
		super(cell.x, cell.y);

		galaxies = new HashSet<Galaxy>(cell.getGalaxies());
	}

	public final Set<Galaxy> getGalaxies() {
		return Collections.unmodifiableSet(galaxies);
	}

	public final Galaxy getSolution() {
		return galaxies.iterator().next();
	}

	public final boolean contains(Galaxy galaxy) {
		return galaxies.contains(galaxy);
	}

	public final int size() {
		return galaxies.size();
	}
}
