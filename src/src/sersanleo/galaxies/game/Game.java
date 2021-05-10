package src.sersanleo.galaxies.game;

import java.io.File;
import java.io.IOException;

import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.util.ExtFileInputStream;

public class Game {
	public final Board board;
	public final Solution solution;

	public Game(Board board, Solution solution) {
		this.board = board;
		this.solution = solution;
	}

	public Game(Board board) {
		this(board, new Solution(board));
	}

	public final static Game createFromFile(File file)
			throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		ExtFileInputStream stream = new ExtFileInputStream(file);

		Board board = Board.createFromStream(stream);
		Solution solution = Solution.createFromStream(board, stream);

		stream.close();
		return new Game(board, solution);
	}
}