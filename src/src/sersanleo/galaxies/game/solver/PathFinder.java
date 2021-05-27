package src.sersanleo.galaxies.game.solver;

import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Galaxy;

public class PathFinder {
	private final Galaxy galaxy;

	protected final Set<SolverCell> obligatorySteps = new HashSet<SolverCell>();

	protected PathFinder(Galaxy galaxy) {
		this.galaxy = galaxy;
	}

	private final boolean isGoal(SolverCell cell) {
		return cell.core && cell.solution() == galaxy;
	}

	private final boolean isStep(SolverCell cell) {
		return cell.contains(galaxy);
	}

	private final boolean find(SolverCell step, Set<SolverCell> path) {
		if (!path.contains(step) && isStep(step)) {
			if (isGoal(step))
				if (obligatorySteps.size() == 0)
					obligatorySteps.addAll(path);
				else {
					obligatorySteps.retainAll(path);
					if (obligatorySteps.size() == 0)
						return false;
				}
			else {
				path.add(step);

				for (SolverCell neighbor : step.neighbors()) {
					if (!find(neighbor, new HashSet<SolverCell>(path)))
						return false;
				}
			}
		}
		return true;
	}

	public final boolean find(SolverCell step) {
		return find(step, new HashSet<SolverCell>());
	}
}