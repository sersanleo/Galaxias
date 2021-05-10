package src.sersanleo.galaxies.game;

import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.util.ExtFileInputStream;
import src.sersanleo.galaxies.util.Vector2i;

public final class Solution {
	private final Board board;

	private final boolean[][] horizontalEdges;
	private final boolean[][] verticalEdges;

	private CellState[][] cells;

	private boolean solved = false;

	private Solution(Board board, boolean[][] horizontalEdges, boolean[][] verticalEdges) {
		this.board = board;

		this.horizontalEdges = horizontalEdges;
		this.verticalEdges = verticalEdges;

		this.cells = new CellState[board.width][board.height];
		resetCells();

		updateSolvedCells();
	}

	public Solution(Board board) {
		this(board, new boolean[board.width][board.height - 1], new boolean[board.width - 1][board.height]);
	}

	private final void resetCells() {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = CellState.NON_SOLVED;
	}

	private final Set<Vector2i> getNeighbors(int x, int y) {
		Set<Vector2i> neighbors = new HashSet<Vector2i>();

		if (x > 0 && !verticalEdges[x - 1][y]) // Izquierda
			neighbors.add(new Vector2i(x - 1, y));

		if (x < board.width - 1 && !verticalEdges[x][y]) // Derecha
			neighbors.add(new Vector2i(x + 1, y));

		if (y > 0 && !horizontalEdges[x][y - 1]) // Arriba
			neighbors.add(new Vector2i(x, y - 1));

		if (y < board.height - 1 && !horizontalEdges[x][y]) // Abajo
			neighbors.add(new Vector2i(x, y + 1));

		return neighbors;
	}

	private final Set<Vector2i> getNeighbors(Vector2i v) {
		return getNeighbors(v.x, v.y);
	}

	private final Set<Vector2i> getNeighborsInSet(Vector2i v, Set<Vector2i> set) {
		Set<Vector2i> neighbors = new HashSet<Vector2i>();
		neighbors.add(new Vector2i(v.x - 1, v.y));
		neighbors.add(new Vector2i(v.x + 1, v.y));
		neighbors.add(new Vector2i(v.x, v.y - 1));
		neighbors.add(new Vector2i(v.x, v.y + 1));

		Iterator<Vector2i> it = neighbors.iterator();
		while (it.hasNext())
			if (!set.contains(it.next()))
				it.remove();

		return neighbors;
	}

	public final boolean horizontalEdge(int x, int y) {
		return horizontalEdges[x][y];
	}

	public final boolean isSolved() {
		return solved;
	}

	public final CellState cell(int x, int y) {
		return cells[x][y];
	}

	public final void switchHorizontalEdge(int x, int y) {
		// Se activará/desactivará la arista correspondiente si no hay una galaxia sobre
		// ella
		Set<Galaxy> galaxiesToCheck = new HashSet<Galaxy>();
		galaxiesToCheck.add(new Galaxy(x - 0.5f, y + 0.5f));
		galaxiesToCheck.add(new Galaxy(x, y + 0.5f));
		galaxiesToCheck.add(new Galaxy(x + 0.5f, y + 0.5f));
		for (Galaxy galaxyToCheck : galaxiesToCheck)
			if (board.getGalaxies().contains(galaxyToCheck))
				return;

		horizontalEdges[x][y] = !horizontalEdges[x][y];
		updateSolvedCells();
	}

	public final void switchVerticalEdge(int x, int y) {
		// Se activará/desactivará la arista correspondiente si no hay una galaxia sobre
		// ella
		Set<Galaxy> galaxiesToCheck = new HashSet<Galaxy>();
		galaxiesToCheck.add(new Galaxy(x + 0.5f, y + 0.5f));
		galaxiesToCheck.add(new Galaxy(x + 0.5f, y));
		galaxiesToCheck.add(new Galaxy(x + 0.5f, y - 0.5f));
		for (Galaxy galaxyToCheck : galaxiesToCheck)
			if (board.getGalaxies().contains(galaxyToCheck))
				return;

		verticalEdges[x][y] = !verticalEdges[x][y];
		updateSolvedCells();
	}

	public final void updateSolvedCells() {
		resetCells();

		int solvedCells = 0;
		Set<Galaxy> galaxies = board.getGalaxies();

		for (Galaxy galaxy : galaxies) {
			// Primero se obtienen todas las casillas pertenecientes a la galaxia con
			// pathfinding
			Set<Vector2i> galaxyCells = new HashSet<Vector2i>();
			{
				Set<Vector2i> next = new HashSet<Vector2i>();
				Set<Vector2i> current = galaxy.getNeighbors();

				while (current.size() > 0) {
					for (Vector2i v : current) {
						galaxyCells.add(v);

						Set<Vector2i> neighbors = getNeighbors(v);
						next.addAll(neighbors);
					}

					current.clear();
					current.addAll(next);
					current.removeAll(galaxyCells);
					next.clear();
				}
			}

			// Se realizan comprobaciones sobre el área obtenida
			CellState state = CellState.SOLVED;
			for (Vector2i cell : galaxyCells) {
				// Simetría
				if (!galaxyCells.contains(galaxy.symmetric(cell).round())) {
					state = CellState.NON_SOLVED;
					break;
				}

				// Unicidad de galaxia
				for (Galaxy galaxy2 : galaxies)
					if (galaxy2 != galaxy && galaxy2.smallBB.overlaps(cell)) {
						state = CellState.NON_SOLVED;
						break;
					}
				if (state == CellState.NON_SOLVED)
					break;

				// Comprobar que no haya aristas entre casillas de la galaxia
				if (getNeighbors(cell).size() != getNeighborsInSet(cell, galaxyCells).size())
					state = CellState.SOLVED_PARTIALLY;
			}

			if (state != CellState.NON_SOLVED) {
				if (state == CellState.SOLVED)
					solvedCells += galaxyCells.size();

				for (Vector2i cell : galaxyCells)
					this.cells[cell.x][cell.y] = state;
			}
		}

		this.solved = solvedCells == board.area;
		if (this.solved)
			System.err.println("OLE");
	}

	public final boolean verticalEdge(int x, int y) {
		return verticalEdges[x][y];
	}

	public static enum CellState {
		NON_SOLVED(Color.WHITE), SOLVED(Color.LIGHT_GRAY), SOLVED_PARTIALLY(new Color(207, 175, 175));

		public final Color color;

		CellState(Color color) {
			this.color = color;
		}
	}

	public final static Solution createFromStream(Board board, ExtFileInputStream stream)
			throws BoardTooSmallException, IOException, CanNotAddGalaxyException {
		boolean[][] horizontalEdges = new boolean[board.width][board.height - 1];
		boolean[][] verticalEdges = new boolean[board.width - 1][board.height];

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height - 1; y++)
				horizontalEdges[x][y] = stream.readBoolean();

		for (int x = 0; x < board.width - 1; x++)
			for (int y = 0; y < board.height; y++)
				verticalEdges[x][y] = stream.readBoolean();

		return new Solution(board, horizontalEdges, verticalEdges);
	}
}