package src.sersanleo.galaxies.game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import src.sersanleo.galaxies.util.Vector2i;

public class Game {
	public final Board board;

	private final boolean[][] horizontalEdges;
	private boolean solved = false;
	private boolean[][] solvedCells;

	private final boolean[][] verticalEdges;

	public Game(Board board) {
		this.board = board;

		this.horizontalEdges = new boolean[board.width][board.height - 1];
		this.verticalEdges = new boolean[board.width - 1][board.height];
		this.solvedCells = new boolean[board.width][board.height];

		updateSolvedCells();
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

	// En el switch añadir comprobacion si hay galaxia o no para no hacer nada

	public final boolean horizontalEdge(int x, int y) {
		return horizontalEdges[x][y];
	}

	public final boolean solved() {
		return solved;
	}

	public final boolean solved(int x, int y) {
		return solvedCells[x][y];
	}

	public final void switchHorizontalEdge(int x, int y) {
		// Se activará/desactivará la arista correspondiente si no hay una galaxia sobre
		// ella
		Galaxy checker = new Galaxy(x, y + 0.5f);
		if (!board.getGalaxies().contains(checker)) {
			horizontalEdges[x][y] = !horizontalEdges[x][y];
			updateSolvedCells();
		}
	}

	public final void switchVerticalEdge(int x, int y) {
		// Se activará/desactivará la arista correspondiente si no hay una galaxia sobre
		// ella
		Galaxy checker = new Galaxy(x + 0.5f, y);
		if (!board.getGalaxies().contains(checker)) {
			verticalEdges[x][y] = !verticalEdges[x][y];
			updateSolvedCells();
		}
	}

	public final void updateSolvedCells() {
		solvedCells = new boolean[board.width][board.height];

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
			boolean solved = true;
			for (Vector2i cell : galaxyCells) {
				// Simetría
				if (!galaxyCells.contains(galaxy.symmetric(cell).round())) {
					solved = false;
					break;
				}

				// Unicidad de galaxia
				for (Galaxy galaxy2 : galaxies)
					if (galaxy2 != galaxy && galaxy2.smallBB.overlaps(cell)) {
						solved = false;
						break;
					}
				if (!solved)
					break;

				// Comprobar que no haya aristas entre casillas de la galaxia
				if (getNeighbors(cell).size() != getNeighborsInSet(cell, galaxyCells).size()) {
					solved = false;
					break;
				}
			}

			if (solved) {
				solvedCells += galaxyCells.size();

				for (Vector2i cell : galaxyCells)
					this.solvedCells[cell.x][cell.y] = solved;
			}
		}

		this.solved = solvedCells == board.area;
		if (this.solved)
			System.err.println("OLE");
	}

	public final boolean verticalEdge(int x, int y) {
		return verticalEdges[x][y];
	}
}