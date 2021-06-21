package src.sersanleo.galaxies.game.solver;

import javax.swing.JDialog;

import src.sersanleo.galaxies.game.Board;

public class SolverThread extends Thread {
	private final JDialog dialog;
	public final Solver solver;
	private boolean success = false;

	public SolverThread(JDialog dialog, Board board) {
		this.dialog = dialog;
		this.solver = new Solver(board, 2);
	}

	@Override
	public void run() {
		solver.solve();
		success = true;
		done();
	}

	public final boolean success() {
		return success;
	}

	public final void done() {
		dialog.dispose();
	}
}