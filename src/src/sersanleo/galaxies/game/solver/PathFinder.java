package src.sersanleo.galaxies.game.solver;

import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;

public class PathFinder {
	private final Galaxy galaxy;

	private int minY;
	private int maxY;

	protected final Set<SolverCell> obligatorySteps = new HashSet<SolverCell>();
	private boolean goalReached = false;

	protected PathFinder(Galaxy galaxy) {
		this.galaxy = galaxy;
	}

	private final boolean isGoal(SolverCell cell) {
		return cell.core && cell.solution() == galaxy;
	}

	private final boolean isStep(SolverCell cell) {
		return cell.y >= minY && cell.y <= maxY && cell.contains(galaxy);
	}

	private final boolean find(SolverCell step, Set<SolverCell> path) {
		if (!path.contains(step) && isStep(step)) {
			if (isGoal(step))
				if (obligatorySteps.size() == 0) {
					goalReached = true;
					obligatorySteps.addAll(path);
				} else {
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

	public final boolean find(SolverCell start, Board board) throws SolutionNotFoundException {
		if (start.y <= galaxy.y) {
			minY = board.minY;
			maxY = (int) Math.ceil(galaxy.y);
		} else {
			minY = (int) Math.floor(galaxy.y);
			maxY = board.maxY;
		}

		boolean res = find(start, new HashSet<SolverCell>());
		if (!goalReached)
			throw new SolutionNotFoundException("No es posible conectar una casilla resuelta con su galaxia.");
		return res;
	}
}