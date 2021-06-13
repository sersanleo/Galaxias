package src.sersanleo.galaxies.game.solver;

import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Galaxy;

public class FloodFill {
	private final Galaxy galaxy;

	protected final Set<SolverCell> visited = new HashSet<SolverCell>();
	protected boolean goalReached = false;

	protected FloodFill(Galaxy galaxy) {
		this.galaxy = galaxy;
	}

	private final boolean isGoal(SolverCell cell) {
		return cell.core && cell.solution() == galaxy;
	}

	private final boolean isStep(SolverCell cell) {
		return cell.contains(galaxy);
	}

	public final void find(SolverCell step) {
		if (!visited.contains(step) && isStep(step)) {
			visited.add(step);
			goalReached = goalReached || isGoal(step);
			for (SolverCell neighbor : step.neighbors())
				find(neighbor);
		}
	}
}