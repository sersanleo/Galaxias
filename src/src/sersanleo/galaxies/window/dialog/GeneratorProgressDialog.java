package src.sersanleo.galaxies.window.dialog;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import src.sersanleo.galaxies.game.generator.GeneratorThread;
import src.sersanleo.galaxies.window.GameWindow;

public class GeneratorProgressDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private GeneratorThread thread;

	public GeneratorProgressDialog(GameWindow window, int width, int height, float difficulty) {
		super(window, "Generando tablero...", true);

		thread = new GeneratorThread(window, this, width, height, difficulty);

		JPanel mainContent = new JPanel();
		mainContent.setBorder(new EmptyBorder(8, 8, 8, 8));
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
		add(mainContent);

		JLabel label = new JLabel("Generando tablero...");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainContent.add(label);

		mainContent.add(Box.createVerticalStrut(8));

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		mainContent.add(progressBar);

		mainContent.add(Box.createVerticalStrut(8));

		@SuppressWarnings("serial")
		JButton cancelButton = new JButton(new AbstractAction("Cancelar") {
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				thread.stop();
				thread.done();
				window.generateNewBoard();
			}
		});
		cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainContent.add(cancelButton);

		setResizable(false);
		pack();
		setLocationRelativeTo(window);
		thread.start();
	}
}
