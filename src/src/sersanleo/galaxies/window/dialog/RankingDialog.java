package src.sersanleo.galaxies.window.dialog;

import javax.swing.JDialog;

import src.sersanleo.galaxies.window.GameWindow;

public class RankingDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public RankingDialog(GameWindow window) {
		super(window, "Cuadro de honor", true);

		pack();
		setLocationRelativeTo(window);
	}
}
