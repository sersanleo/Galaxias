package src.sersanleo.galaxies;

import java.io.File;
import java.io.IOException;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.game.rendering.SolverRenderer;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.window.GameWindow;

public final class Main {
	public static void main(String[] args) {
		window();
	}

	@SuppressWarnings("unused")
	private static final void window() {
		GameWindow window = new GameWindow();
		window.setVisible(true);
	}

	private static final void testSolver(int level) {
		Board board;
		try {
			board = Board.createFromRaetsel(level);
			Solver solver = new Solver(board);
			solver.solve();
			new SolverRenderer(solver).save(new File("F:\\Sergio\\Desktop\\TABLEROS\\Raetsel\\" + level + ".jpg"));
		} catch (IOException | BoardTooSmallException | CanNotAddGalaxyException e) {
			System.out.println(level);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static final void testSolver() {
		for (int level = 1; level < 503; level++) {
			testSolver(level);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}