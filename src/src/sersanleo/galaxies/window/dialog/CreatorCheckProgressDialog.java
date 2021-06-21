package src.sersanleo.galaxies.window.dialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.game.solver.SolverThread;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.screen.GameScreen;

public class CreatorCheckProgressDialog extends JDialog implements WindowListener {
	private static final long serialVersionUID = 1L;

	private final GameWindow window;
	private SolverThread thread;

	public CreatorCheckProgressDialog(GameWindow window, Board board) {
		super(window, "Comprobando tablero...", true);

		this.window = window;
		thread = new SolverThread(this, board);

		JPanel mainContent = new JPanel();
		mainContent.setBorder(new EmptyBorder(8, 8, 8, 8));
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
		add(mainContent);

		JLabel label = new JLabel("<html><div style='text-align: center'>Comprobando tablero...</div></html>",
				SwingConstants.CENTER);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainContent.add(label);

		mainContent.add(Box.createVerticalStrut(8));

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		mainContent.add(progressBar);

		mainContent.add(Box.createVerticalStrut(8));

		JButton cancelButton = new JButton(new AbstractAction("Cancelar") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainContent.add(cancelButton);

		setResizable(false);
		pack();
		setLocationRelativeTo(window);
		addWindowListener(this);
		thread.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void windowClosed(WindowEvent arg0) {
		thread.stop();

		if (thread.success()) {
			Solver solver = thread.solver;
			if (solver.getSolutions() == 1) {
				solver.board.solution = solver.getSolution();
				GameScreen screen = new GameScreen(window, new Game(solver.board));
				window.setScreen(screen, true);
			} else if (solver.getSolutions() == 0)
				JOptionPane.showMessageDialog(window, "El tablero no tiene una solución válida.", "Error",
						JOptionPane.ERROR_MESSAGE);
			else if (solver.getSolutions() > 1)
				JOptionPane.showMessageDialog(window, "El tablero tiene más de una solución válida.", "Error",
						JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}
