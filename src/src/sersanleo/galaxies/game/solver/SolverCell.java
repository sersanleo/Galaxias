package src.sersanleo.galaxies.game.solver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.util.Vector2i;

public class SolverCell extends Vector2i {
	private final Solver solver;
	protected final Set<Galaxy> galaxies;

	protected boolean solved = false;
	protected boolean core = false;

	public SolverCell(Solver solver, int x, int y) {
		super(x, y);

		this.solver = solver;
		galaxies = new HashSet<Galaxy>();
	}

	public final Set<Galaxy> getGalaxies() {
		return Collections.unmodifiableSet(galaxies);
	}

	public final boolean isSolved() {
		return solved;
	}

	public final Galaxy solution() {
		if (solved && galaxies.size() == 1)
			return galaxies.iterator().next();
		else
			return null;
	}

	public final boolean contains(Galaxy galaxy) {
		return galaxies.contains(galaxy);
	}

	public final int size() {
		return galaxies.size();
	}

	public final void add(Galaxy galaxy) {
		galaxies.add(galaxy);
	}

	public final void remove(Galaxy galaxy) throws SolutionNotFoundException {
		if (contains(galaxy)) {
			if (solved) {
				if (solution() == galaxy)
					throw new SolutionNotFoundException("No se puede eliminar la solución de una casilla.");
			} else {
				galaxies.remove(galaxy);

				if (galaxies.size() == 1)
					solve();
				else if (galaxies.size() == 0)
					throw new SolutionNotFoundException("No se puede quedar una casilla vacía.");
			}
		}
	}

	public final void solve(Galaxy solution) throws SolutionNotFoundException {
		if (solved) {
			if (solution() != solution)
				throw new SolutionNotFoundException(
						"No se puede solucionar una casilla ya solucionada con otra galaxia.");
		} else if (contains(solution)) {
			solved = true;
			solver.solvedCells++;

			Iterator<Galaxy> it = galaxies.iterator();
			while (it.hasNext()) {
				Galaxy galaxy = it.next();
				if (galaxy != solution) {
					it.remove();
					symmetric(galaxy).remove(galaxy);
				}
			}

			symmetric(solution).solve(solution);
		} else
			throw new SolutionNotFoundException("No se puede solucionar una casilla con una galaxia que no contiene.");
	}

	public final void solve() throws SolutionNotFoundException {
		solve(galaxies.iterator().next());
	}

	private final SolverCell symmetric(Galaxy galaxy) {
		return solver.cell(galaxy.symmetric(this).round());
	}

	public final Set<SolverCell> neighbors() {
		Set<SolverCell> neighbors = new HashSet<SolverCell>();

		if (x > 0)
			neighbors.add(solver.cells[x - 1][y]);
		if (x < solver.board.width - 1)
			neighbors.add(solver.cells[x + 1][y]);
		if (y > 0)
			neighbors.add(solver.cells[x][y - 1]);
		if (y < solver.board.height - 1)
			neighbors.add(solver.cells[x][y + 1]);

		return neighbors;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[ ");

		for (Galaxy g : galaxies) {
			sb.append(solver.board.getGalaxyId(g));
			sb.append(' ');
		}
		sb.append("]");
		return sb.toString();
	}
}
