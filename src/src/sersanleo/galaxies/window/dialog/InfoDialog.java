package src.sersanleo.galaxies.window.dialog;

import javax.swing.JDialog;

import src.sersanleo.galaxies.window.GameWindow;

public class InfoDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public InfoDialog(GameWindow window) {
		super(window, "Información sobre Galaxias", true);
		
		
		pack();
		setLocationRelativeTo(window);
	}
}
