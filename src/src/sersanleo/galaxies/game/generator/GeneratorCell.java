package src.sersanleo.galaxies.game.generator;

import java.util.HashSet;
import java.util.Iterator;

import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.solver.SolverCell;
import src.sersanleo.galaxies.util.Vector2i;

public class GeneratorCell extends HashSet<Galaxy> {
	private static final long serialVersionUID = 1L;

	private final Vector2i pos;

	private final BoardGeneratorFixer fixer;
	private boolean solved;

	protected GeneratorCell(BoardGeneratorFixer fixer, SolverCell cell) {
		super(cell.getGalaxies());

		this.pos = new Vector2i(cell);
		this.fixer = fixer;
		this.solved = size() == 1;
	}

	protected final Galaxy getSolution() {
		return iterator().next();
	}

	@Override
	public final boolean remove(Object galaxy) {
		boolean res = super.remove(galaxy);

		if (res)
			switch (size()) {
			case 0:
				fixer.generator.empty(pos);
				break;
			case 1:
				solve();
				break;
			default:
				System.err.println("[DEBUG] CASO INESPERADO " + size());
			}

		return res;
	}

	private final void solve(Galaxy solution) {
		if (!solved) {
			solved = true;

			Iterator<Galaxy> it = iterator();
			while (it.hasNext()) {
				Galaxy galaxy = it.next();

				if (galaxy == solution)
					continue;

				it.remove();
				symmetric(galaxy).remove(galaxy);
			}

			fixer.generator.fill(pos, solution);
			symmetric(solution).solve(solution);
		}
	}

	private final void solve() {
		solve(getSolution());
	}

	private final GeneratorCell symmetric(Galaxy galaxy) {
		Vector2i symmCoords = galaxy.symmetric(pos).round();
		return fixer.cells[symmCoords.x][symmCoords.y];
	}
}