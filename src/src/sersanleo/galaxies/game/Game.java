package src.sersanleo.galaxies.game;

public class Game {
	public final Board board;
	public final Solution solution;

	public Game(Board board) {
		this.board = board;
		this.solution = new Solution(board);
	}
}