package src.sersanleo.galaxies.game;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import src.sersanleo.galaxies.game.Movement.EdgeType;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.util.ExtFileInputStream;
import src.sersanleo.galaxies.util.ExtFileOutputStream;
import src.sersanleo.galaxies.util.Vector2i;

public final class Solution {
	private final Board board;

	private final boolean[][] horizontalEdges;
	private final boolean[][] verticalEdges;
	private int moves;

	private SolutionCell[][] cells;

	private boolean solved = false;

	// Listeners
	private final Set<SolutionFoundListener> solutionFoundListeners = new LinkedHashSet<SolutionFoundListener>();

	public Solution(Board board, boolean[][] horizontalEdges, boolean[][] verticalEdges, int moves) {
		this.board = board;

		this.horizontalEdges = horizontalEdges;
		this.verticalEdges = verticalEdges;
		this.moves = moves;

		this.cells = new SolutionCell[board.width][board.height];
		updateCells();
	}

	public Solution(Board board, boolean[][] horizontalEdges, boolean[][] verticalEdges) {
		this(board, horizontalEdges, verticalEdges, 0);
	}

	public Solution(Board board) {
		this(board, new boolean[board.width][board.height - 1], new boolean[board.width - 1][board.height], 0);
	}

	public final void set(Solution solution) {
		if (board == solution.board) {
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height - 1; y++)
					horizontalEdges[x][y] = solution.horizontalEdges[x][y];

			for (int x = 0; x < board.width - 1; x++)
				for (int y = 0; y < board.height; y++)
					verticalEdges[x][y] = solution.verticalEdges[x][y];

			this.moves = solution.moves;

			updateCells();
		}
	}

	public final void addSolutionFoundListener(SolutionFoundListener listener) {
		solutionFoundListeners.add(listener);
	}

	public final void removeSolutionFoundListener(SolutionFoundListener listener) {
		solutionFoundListeners.remove(listener);
	}

	private final void propagateSolutionFound() {
		for (SolutionFoundListener listener : solutionFoundListeners)
			listener.solutionFound();
	}

	private final void resetCells() {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = new SolutionCell();
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
		if (x >= 0 && x < board.width && y >= 0 && y < board.height - 1)
			return horizontalEdges[x][y];
		else
			return true;
	}

	public final boolean isSolved() {
		return solved;
	}

	public final SolutionCell cell(int x, int y) {
		return cells[x][y];
	}

	protected final boolean switchHorizontalEdge(int x, int y, boolean undoing) {
		// Se alternará la arista correspondiente si no hay una galaxia sobre
		// ella
		Set<Galaxy> galaxiesToCheck = new HashSet<Galaxy>();
		galaxiesToCheck.add(new Galaxy(x - 0.5f, y + 0.5f));
		galaxiesToCheck.add(new Galaxy(x, y + 0.5f));
		galaxiesToCheck.add(new Galaxy(x + 0.5f, y + 0.5f));
		for (Galaxy galaxyToCheck : galaxiesToCheck)
			if (board.getGalaxies().contains(galaxyToCheck))
				return false;

		horizontalEdges[x][y] = !horizontalEdges[x][y];
		if (undoing)
			moves--;
		else
			moves++;

		boolean leftEnd = horizontalEdge(x - 1, y) || verticalEdge(x - 1, y) || verticalEdge(x - 1, y + 1);
		boolean rightEnd = horizontalEdge(x + 1, y) || verticalEdge(x, y) || verticalEdge(x, y + 1);
		boolean unsolvedCells = cells[x][y].state == CellState.NON_SOLVED
				&& cells[x][y + 1].state == CellState.NON_SOLVED;
		if ((leftEnd && rightEnd) || !unsolvedCells)
			updateCells();

		return true;
	}

	protected final boolean switchVerticalEdge(int x, int y, boolean undoing) {
		// Se alternará la arista correspondiente si no hay una galaxia sobre
		// ella
		Set<Galaxy> galaxiesToCheck = new HashSet<Galaxy>();
		galaxiesToCheck.add(new Galaxy(x + 0.5f, y + 0.5f));
		galaxiesToCheck.add(new Galaxy(x + 0.5f, y));
		galaxiesToCheck.add(new Galaxy(x + 0.5f, y - 0.5f));
		for (Galaxy galaxyToCheck : galaxiesToCheck)
			if (board.getGalaxies().contains(galaxyToCheck))
				return false;

		verticalEdges[x][y] = !verticalEdges[x][y];
		if (undoing)
			moves--;
		else
			moves++;

		boolean topEnd = verticalEdge(x, y - 1) || horizontalEdge(x, y - 1) || horizontalEdge(x + 1, y - 1);
		boolean bottomEnd = verticalEdge(x, y + 1) || horizontalEdge(x, y) || horizontalEdge(x + 1, y);
		boolean unsolvedCells = cells[x][y].state == CellState.NON_SOLVED
				&& cells[x + 1][y].state == CellState.NON_SOLVED;
		if ((topEnd && bottomEnd) || !unsolvedCells)
			updateCells();

		return true;
	}

	public final void updateCells() {
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

			if (state == CellState.SOLVED)
				solvedCells += galaxyCells.size();

			for (Vector2i cell : galaxyCells)
				this.cells[cell.x][cell.y].set(galaxy, state);
		}

		this.solved = solvedCells == board.area;
		if (this.solved)
			propagateSolutionFound();
	}

	public final boolean verticalEdge(int x, int y) {
		if (x >= 0 && x < board.width - 1 && y >= 0 && y < board.height)
			return verticalEdges[x][y];
		else
			return true;
	}

	public final void write(ExtFileOutputStream stream) throws IOException {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height - 1; y++)
				stream.writeBoolean(horizontalEdges[x][y]);

		for (int x = 0; x < board.width - 1; x++)
			for (int y = 0; y < board.height; y++)
				stream.writeBoolean(verticalEdges[x][y]);

		stream.writeInt(moves);
	}

	public final Movement getNextStep(Solution realSolution) {
		List<Movement> movements = new ArrayList<Movement>();
		boolean errorMode = false;

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height - 1; y++) {
				boolean edge = horizontalEdges[x][y];

				if (edge != realSolution.horizontalEdges[x][y]) {
					if (edge && !errorMode) {
						errorMode = true;
						movements.clear();
					}
					if (!errorMode || edge)
						movements.add(new Movement(x, y, EdgeType.HORIZONTAL));
				}
			}

		for (int x = 0; x < board.width - 1; x++)
			for (int y = 0; y < board.height; y++) {
				boolean edge = verticalEdges[x][y];

				if (edge != realSolution.verticalEdges[x][y]) {
					if (edge && !errorMode) {
						errorMode = true;
						movements.clear();
					}
					if (!errorMode || edge)
						movements.add(new Movement(x, y, EdgeType.VERTICAL));
				}
			}

		if (movements.size() > 0)
			return movements.get(new Random().nextInt(movements.size()));
		return null;
	}

	public final int[] compare(Solution realSolution) {
		int[] res = new int[2];

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height - 1; y++) {
				boolean edge = horizontalEdges[x][y];

				if (edge != realSolution.horizontalEdges[x][y])
					if (edge)
						res[0]++;
					else
						res[1]++;
			}

		for (int x = 0; x < board.width - 1; x++)
			for (int y = 0; y < board.height; y++) {
				boolean edge = verticalEdges[x][y];

				if (edge != realSolution.verticalEdges[x][y])
					if (edge)
						res[0]++;
					else
						res[1]++;
			}

		return res;
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

		int moves = stream.readInt();

		return new Solution(board, horizontalEdges, verticalEdges, moves);
	}

	public static interface SolutionFoundListener {
		public void solutionFound();
	}

	public final int getMoves() {
		return moves;
	}

	public static enum CellState {
		NON_SOLVED(Color.WHITE), SOLVED(Color.LIGHT_GRAY), SOLVED_PARTIALLY(new Color(207, 175, 175));

		public final Color color;

		CellState(Color color) {
			this.color = color;
		}
	}

	public static class SolutionCell {
		public Galaxy galaxy;
		public CellState state;

		private SolutionCell(Galaxy galaxy, CellState state) {
			this.galaxy = galaxy;
			this.state = state;
		}

		private SolutionCell() {
			this(null, CellState.NON_SOLVED);
		}

		public final void set(Galaxy galaxy, CellState state) {
			if (state == CellState.NON_SOLVED)
				this.galaxy = null;
			else
				this.galaxy = galaxy;
			this.state = state;
		}
	}
}