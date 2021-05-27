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

	private Galaxy solution;
	protected boolean core = false;

	protected SolverCell(Solver solver, int x, int y) {
		super(x, y);

		this.solver = solver;
		galaxies = new HashSet<Galaxy>();
	}

	public final Set<Galaxy> getGalaxies() {
		return Collections.unmodifiableSet(galaxies);
	}

	public final boolean isSolved() {
		return solution != null;
	}

	public final Galaxy solution() {
		return solution;
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

	protected final void setCore() {
		if (isSolved()) {
			core = true;

			Galaxy solution = solution();
			for (SolverCell neighbor : neighbors())
				if (!neighbor.core && neighbor.solution() == solution)
					neighbor.setCore();
		} else
			System.err.println("SE HA INTENTADO MARCAR COMO NÚCLEO UNA CASILLA NO RESUELTA");
	}

	private final void checkCore() {
		if (isSolved()) {
			Galaxy solution = solution();
			for (SolverCell neighbor : neighbors())
				if (neighbor.core && neighbor.solution() == solution) {
					setCore();
					break;
				}
		}
	}

	public final void remove(Galaxy galaxy) throws SolutionNotFoundException {
		if (contains(galaxy)) {
			if (isSolved()) {
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
		if (isSolved()) {
			if (this.solution != solution)
				throw new SolutionNotFoundException(
						"No se puede solucionar una casilla ya solucionada con otra galaxia." + super.toString());
		} else if (contains(solution)) {
			this.solution = solution;
			solver.solvedCells++;

			Iterator<Galaxy> it = galaxies.iterator();
			while (it.hasNext()) {
				Galaxy galaxy = it.next();
				if (galaxy != solution) {
					it.remove();
					symmetric(galaxy).remove(galaxy);
				}
			}

			checkCore();
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
