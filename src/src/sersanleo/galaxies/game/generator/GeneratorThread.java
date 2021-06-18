package src.sersanleo.galaxies.game.generator;

import javax.swing.JDialog;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.screen.GameScreen;

public class GeneratorThread extends Thread {
	private final JDialog dialog;
	private final int width;
	private final int height;
	private final float difficulty;

	private Board board = null;
	private boolean success = false;

	public GeneratorThread(JDialog dialog, int width, int height, float difficulty) {
		this.dialog = dialog;
		this.width = width;
		this.height = height;
		this.difficulty = difficulty;
	}

	@Override
	public void run() {
		try {
			BoardGenerator generator = new BoardGenerator(width, height, difficulty);
			generator.generate();
			board = generator.board;
			success = true;
		} catch (BoardTooSmallException e) {
		}
		done();
	}

	public final boolean success() {
		return success && board != null;
	}

	public final void setScreen(GameWindow window) {
		GameScreen screen = new GameScreen(window, new Game(board));
		window.setScreen(screen);
	}

	public final void done() {
		dialog.dispose();
	}
}