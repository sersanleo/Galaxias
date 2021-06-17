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
	protected boolean solved;

	protected GeneratorCell(BoardGeneratorFixer fixer, SolverCell cell) {
		super(cell.x, cell.y);

		this.fixer = fixer;
		galaxies = new HashSet<Galaxy>(cell.getGalaxies());
		solved = galaxies.size() == 1;
	}

	protected final void solve(Galaxy solution) {
		if (!solved) {
			solved = true;

			if (solution != null)
				solveSymmetric(solution, solution);
			else
				fixer.boardGenerator.empty(x, y);

			Iterator<Galaxy> it = galaxies.iterator();
			while (it.hasNext()) {
				Galaxy galaxy = it.next();
				if (galaxy != solution) {
					it.remove();
					solveSymmetric(galaxy, null);
				}
			}
		}
	}

	private final void solveSymmetric(Galaxy galaxy, Galaxy solution) {
		GeneratorCell symmetric = fixer.cell(galaxy.symmetric(this).round());
		if (symmetric != null)
			symmetric.solve(solution);
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
