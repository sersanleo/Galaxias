package src.sersanleo.galaxies.game.solver;

import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Galaxy;

public class SolverPathfinder {
	private final SolverCell start;
	private final Galaxy galaxy;

	public final Set<SolverCell> visited = new HashSet<SolverCell>();
	public boolean goalReached = false;

	public SolverPathfinder(SolverCell start, Galaxy galaxy) {
		this.start = start;
		this.galaxy = galaxy;
	}

	private final boolean isGoal(SolverCell cell) {
		return cell.solution() == galaxy;
	}

	private final boolean isStep(SolverCell cell) {
		return cell.contains(galaxy);
	}

	public final boolean hasReachedGoal() {
		return goalReached;
	}

	public final void find(SolverCell cell) {
		if (!visited.contains(cell) && isStep(cell)) {
			visited.add(cell);
			goalReached = goalReached || isGoal(cell);
			for (SolverCell neighbor : cell.neighbors())
				find(neighbor);
		}
	}

	public final void find() {
		find(start);
	}
}