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
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import src.sersanleo.galaxies.game.generator.GeneratorThread;
import src.sersanleo.galaxies.window.GameWindow;

public class GeneratorProgressDialog extends JDialog implements WindowListener {
	private static final long serialVersionUID = 1L;

	private final GameWindow window;
	private GeneratorThread thread;

	public GeneratorProgressDialog(GameWindow window, int width, int height, float difficulty) {
		super(window, "Generando tablero...", true);

		this.window = window;
		thread = new GeneratorThread(this, width, height, difficulty);

		JPanel mainContent = new JPanel();
		mainContent.setBorder(new EmptyBorder(8, 8, 8, 8));
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
		add(mainContent);

		JLabel label = new JLabel(
				"<html><div style='text-align: center'>Generando tablero...<br>Si tarda demasiado, pruebe a cancelar e inténtalo de nuevo.</div></html>",
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
		if (thread.success())
			thread.setScreen(window);
		else
			window.generateNewBoard();
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
