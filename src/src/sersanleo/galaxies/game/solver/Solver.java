package src.sersanleo.galaxies.game.solver;

import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.util.Vector2i;

public class Solver {
	public final Board board;
	protected final SolverCell[][] cells;

	protected int solvedCells = 0;
	private boolean solved = false;

	public Solver(Board board) {
		this.board = board;
		cells = emptyCellsArray();
	}

	public final boolean isSolved() {
		return solved;
	}

	public final SolverCell cell(int x, int y) {
		return cells[x][y];
	}

	public final SolverCell cell(Vector2i v) {
		return cell(v.x, v.y);
	}

	private final SolverCell[][] emptyCellsArray() {
		SolverCell[][] res = new SolverCell[board.width][board.height];

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				res[x][y] = new SolverCell(this, x, y);

		return res;
	}

	private final void initialize() throws SolutionNotFoundException {
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
					throw new SolutionNotFoundException("No se puede quedar una casilla vac�a.");

		// Marcar como resueltas y n�cleos las casillas pertenecientes a una galaxia
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
								AreaFinder pathfinder = new AreaFinder(galaxy);
								pathfinder.find(cell);

								visited.addAll(pathfinder.visited);
								if (!pathfinder.goalReached) {
									changed = true;
									for (SolverCell c : pathfinder.visited)
										c.remove(galaxy);
								}
							}
						}
				}
			} while (changed);

			// Solucionar casillas resueltas para conectar con el n�cleo de la galaxia
			changed = false;
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					SolverCell cell = cells[x][y];
					if (cell.isSolved() && !cell.core) {
						Galaxy solution = cell.solution();
						PathFinder pathFinder = new PathFinder(solution);
						if (pathFinder.find(cell))
							for (SolverCell cellToSolve : pathFinder.obligatorySteps)
								if (!cellToSolve.isSolved()) {
									changed = true;
									cellToSolve.solve(solution);
								}
					}
				}
		} while (changed);
	}

	public final void solve() {
		try {
			initialize();
			iterate();
			solved = solvedCells == board.area;
		} catch (SolutionNotFoundException e) {
			e.printStackTrace();
		}

		printPossibilities();
		printSolved();
		System.out.println();
		printCore();
	}

	private final String printFormat(int length) {
		StringBuilder format = new StringBuilder();

		for (int i = 0; i < board.width; i++) {
			format.append("%-");
			format.append(length);
			format.append("s ");
		}
		format.append("\n");

		return format.toString();
	}

	private final String printFormat() {
		return printFormat((int) (1 + Math.floor(Math.log(board.getGalaxies().size()))));
	}

	private final void print() {
		String format = printFormat();
		for (int y = 0; y < board.height; y++) {
			String[] data = new String[board.width];
			for (int x = 0; x < board.width; x++)
				data[x] = cells[x][y].isSolved() ? "" + board.getGalaxyId(cells[x][y].solution()) : "-";
			System.out.format(format, data);
		}
		System.out.println();
	}

	public void printPossibilities() {
		String format = printFormat(12);
		for (int y = 0; y < board.height; y++) {
			String[] data = new String[board.width];
			for (int x = 0; x < board.width; x++)
				data[x] = cells[x][y].toString();
			System.out.format(format, data);
		}
		System.out.println();
	}

	public final void printSolved() {
		String format = printFormat(2);
		for (int y = 0; y < board.height; y++) {
			String[] data = new String[board.width];
			for (int x = 0; x < board.width; x++)
				data[x] = cells[x][y].isSolved() ? "1" : "0";
			System.out.format(format, data);
		}
		System.out.println();
	}

	public final void printCore() {
		String format = printFormat(2);
		for (int y = 0; y < board.height; y++) {
			String[] data = new String[board.width];
			for (int x = 0; x < board.width; x++)
				data[x] = cells[x][y].core ? "1" : "0";
			System.out.format(format, data);
		}
		System.out.println();
	}
}
