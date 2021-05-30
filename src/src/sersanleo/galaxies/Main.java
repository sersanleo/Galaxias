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
		try {
			Board board = Board
					.createFromFile(new File("F:\\Sergio\\Desktop\\TABLEROS\\archivos Raetsel\\" + level + ".tsb"));
			Solver solver = new Solver(board, 2);
			solver.solve();
			if (solver.getSolutions() == 0)
				System.err.println(level);
			new SolverRenderer(solver).save(new File("F:\\Sergio\\Desktop\\TABLEROS\\Raetsel\\" + level + ".jpg"));
		} catch (IOException | BoardTooSmallException | CanNotAddGalaxyException e) {
			System.out.println(level);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static final void testSolver() {
		for (int level = 1; level < 503; level++)
			testSolver(level);
	}

	private static final void testGuardar(int level) {
		try {
			Board board = Board.createFromRaetsel(level);
			board.save(new File("F:\\Sergio\\Desktop\\TABLEROS\\archivos Raetsel\\" + level + ".tsb"));
		} catch (IOException | BoardTooSmallException | CanNotAddGalaxyException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static final void testGuardar() {
		for (int level = 66; level < 503; level++) {
			testGuardar(level);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}