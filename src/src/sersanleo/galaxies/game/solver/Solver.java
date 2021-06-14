package src.sersanleo.galaxies.game.solver;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.Solution;
import src.sersanleo.galaxies.util.Vector2i;

public class Solver {
	public final Board board;
	private final int limit;

	private final Set<Galaxy[][]> solutions = new HashSet<Galaxy[][]>();
	protected final SolverCell[][] cells;
	protected int solvedCells;

	public Solver(Board board, int limit) {
		this.board = board;
		this.limit = limit;

		cells = new SolverCell[board.width][board.height];
	}

	public final int getSolutions() {
		return solutions.size();
	}

	public final Solution getSolution() {
		Galaxy[][] solution = solutions.iterator().next();

		boolean[][] horizontalEdges = new boolean[board.width][board.height - 1];
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height - 1; y++)
				if (solution[x][y] != solution[x][y + 1])
					horizontalEdges[x][y] = true;

		boolean[][] verticalEdges = new boolean[board.width - 1][board.height];
		for (int x = 0; x < board.width - 1; x++)
			for (int y = 0; y < board.height; y++)
				if (solution[x][y] != solution[x + 1][y])
					verticalEdges[x][y] = true;

		return new Solution(board, horizontalEdges, verticalEdges);
	}

	public final SolverCell cell(int x, int y) {
		return cells[x][y];
	}

	public final SolverCell cell(Vector2i v) {
		return cell(v.x, v.y);
	}

	private final boolean saveSolution() {
		Galaxy[][] solution = new Galaxy[board.width][board.height];

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				solution[x][y] = cells[x][y].solution();

		solutions.add(solution);
		return solutions.size() >= limit;
	}

	private final SolverCell[][] getState() {
		SolverCell[][] state = new SolverCell[board.width][board.height];

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				state[x][y] = cells[x][y].clone();

		return state;
	}

	private final void setState(SolverCell[][] state) {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = state[x][y].clone();
	}

	private final void initialize() throws SolutionNotFoundException {
		// Inicializar array de casillas
		solutions.clear();
		solvedCells = 0;
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = new SolverCell(this, x, y);

		// Rellenar todas las posibilidades
		for (Galaxy galaxy : board.getGalaxies()) {
			float semiWidth = Math.min(galaxy.x, board.width - (galaxy.x + 1));
			float semiHeight = Math.min(galaxy.y, board.height - (galaxy.y + 1));

			int startX = Math.round(galaxy.x - semiWidth);
			int endX = Math.round(galaxy.x + semiWidth);
			int startY = Math.round(galaxy.y - semiHeight);
			int endY = Math.round(galaxy.y + semiHeight);

			for (int x = startX; x <= endX; x++)
				for (int y = startY; y <= endY; y++)
					cells[x][y].add(galaxy);
		}

		// Comprobar que todas las casillas tienen al menos una posibilidad
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				if (cells[x][y].size() == 0)
					throw new SolutionNotFoundException("No se puede quedar una casilla vacía.");

		// Marcar como resueltas y núcleos las casillas pertenecientes a una galaxia
		for (Galaxy galaxy : board.getGalaxies())
			for (Vector2i v : galaxy.getNeighbors()) {
				SolverCell cell = cells[v.x][v.y];
				cell.solve(galaxy);
				cell.setCore();
			}

		// Marcar como resueltas las casillas con una sola galaxia
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				SolverCell cell = cells[x][y];
				if (!cell.isSolved() && cell.size() == 1)
					cell.solve();
			}
	}

	private final void iterate() throws SolutionNotFoundException {
		boolean changed;
		do {
			// Eliminar posibilidades no conectadas con una casilla solucionada
			do {
				changed = false;
				for (Galaxy galaxy : board.getGalaxies()) {
					Set<SolverCell> visited = new HashSet<SolverCell>();

					for (int x = 0; x < board.width; x++)
						for (int y = 0; y < board.height; y++) {
							SolverCell cell = cells[x][y];

							if (cell.contains(galaxy) && !visited.contains(cell) && !cell.isSolved()) {
								FloodFill floodFill = new FloodFill(galaxy);
								floodFill.find(cell);

								visited.addAll(floodFill.visited);
								if (!floodFill.goalReached) {
									changed = true;
									for (SolverCell c : floodFill.visited)
										c.remove(galaxy);
								}
							}
						}
				}
			} while (changed);

			changed = false;
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					SolverCell cell = cells[x][y];

					if (cell.isSolved() && !cell.core) {
						Galaxy solution = cell.solution();
						PathFinder pathFinder = new PathFinder(solution);

						if (pathFinder.find(cell, board)) {
							for (SolverCell cellToSolve : pathFinder.obligatorySteps)
								if (!cellToSolve.isSolved()) {
									changed = true;
									cellToSolve.solve(solution);
								}
						}
					}
				}
		} while (changed);
	}

	private final boolean isSolved() {
		return solvedCells >= board.area;
	}

	private final boolean backtracking(Galaxy[][] solution) {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				SolverCell cell = cells[x][y];

				if (!cell.isSolved()) {
					// Guardamos estado actual
					int solvedCells = this.solvedCells;
					SolverCell[][] state = getState();

					Set<Galaxy> galaxies;
					if (solution == null)
						galaxies = new HashSet<Galaxy>();
					else {
						galaxies = new LinkedHashSet<Galaxy>();
						galaxies.add(solution[x][y]);
					}
					galaxies.addAll(cell.getGalaxies());

					for (Galaxy galaxy : galaxies) {
						try {
							cell.solve(galaxy);
							iterate();

							if (!isSolved()) {
								if (backtracking(solution))
									return true;
							} else if (saveSolution())
								return true;
						} catch (SolutionNotFoundException e) {
						}

						// Volvemos al estado guardado antes de irnos por esta rama
						this.solvedCells = solvedCells;
						setState(state);
						cell = cells[x][y];
					}
					return false;
				}
			}
		return false;
	}

	private final void merge() {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				SolverCell cell = new SolverCell(this, x, y);

				for (Galaxy[][] solution : solutions)
					cell.add(solution[x][y]);

				cells[x][y] = cell;
			}
	}

	public final void solve(Galaxy[][] solution) {
		try {
			initialize();
			iterate();

			if (isSolved())
				saveSolution();
			else {
				// backtracking(solution);
				// merge();
			}
		} catch (SolutionNotFoundException e) {
		}
	}

	public final void solve() {
		solve(null);
	}
}
