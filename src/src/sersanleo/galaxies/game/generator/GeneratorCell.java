package src.sersanleo.galaxies.game.generator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.solver.SolverCell;
import src.sersanleo.galaxies.util.Vector2i;

public class GeneratorCell extends Vector2i {
	private final BoardGeneratorFixer fixer;
	private final Set<Galaxy> galaxies;

	protected GeneratorCell(BoardGeneratorFixer fixer, SolverCell cell) {
		super(cell.x, cell.y);

		this.fixer = fixer;
		galaxies = new HashSet<Galaxy>(cell.getGalaxies());
	}

	private final void empty() {
		// vaciar esta casilla y simetricas
		Iterator<Galaxy> it = galaxies.iterator();
		while (it.hasNext()) {
			Galaxy galaxy = it.next();
			
			it.remove();
			symmetric(galaxy).galaxies.remove(galaxy);
		}
	}

	protected final void solve(Galaxy solution) {
		Iterator<Galaxy> it = galaxies.iterator();
		while (it.hasNext()) {
			Galaxy galaxy = it.next();
			if (galaxy != solution) {
				it.remove();
				symmetric(galaxy).empty();
			}
		}

		symmetric(solution).solve(solution);
	}

	private final GeneratorCell symmetric(Galaxy galaxy) {
		return fixer.cell(galaxy.symmetric(this).round());
	}

	public final Set<Galaxy> getGalaxies() {
		return Collections.unmodifiableSet(galaxies);
	}

	public final boolean contains(Galaxy galaxy) {
		return galaxies.contains(galaxy);
	}

	public final int size() {
		return galaxies.size();
	}
}
