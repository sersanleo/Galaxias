package src.sersanleo.galaxies.game;

import java.util.Random;

public class Game {
	public final Board board;

	private final boolean[][] horizontalEdges;
	private final boolean[][] solved;
	private final boolean[][] verticalEdges;

	public Game(Board board) {
		this.board = board;

		this.horizontalEdges = new boolean[board.width][board.height - 1];
		this.verticalEdges = new boolean[board.width - 1][board.height];
		this.solved = new boolean[board.width][board.height];

		updateSolvedCells();
	}

	public final boolean horizontalEdge(int x, int y) {
		return horizontalEdges[x][y];
	}

	public final boolean solved(int x, int y) {
		return solved[x][y];
	}

	public final void switchHorizontalEdge(int x, int y) {
		horizontalEdges[x][y] = !horizontalEdges[x][y];
	}

	public final void switchVerticalEdge(int x, int y) {
		verticalEdges[x][y] = !verticalEdges[x][y];
	}

	private final void updateSolvedCells() {
		Random rnd = new Random();

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				solved[x][y] = rnd.nextBoolean();

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height - 1; y++)
				horizontalEdges[x][y] = rnd.nextBoolean();

		for (int x = 0; x < board.width - 1; x++)
			for (int y = 0; y < board.height; y++)
				verticalEdges[x][y] = rnd.nextBoolean();
	}

	public final boolean verticalEdge(int x, int y) {
		return verticalEdges[x][y];
	}
}