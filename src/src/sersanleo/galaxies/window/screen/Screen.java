package src.sersanleo.galaxies.window.screen;

import javax.swing.JPanel;

import src.sersanleo.galaxies.window.GameWindow;

public abstract class Screen extends JPanel {
	private static final long serialVersionUID = 1L;

	protected final GameWindow window;

	public Screen(GameWindow window) {
		this.window = window;
	}

	public boolean canBeRemoved() {
		return true;
	}

	public abstract void release();

	public void added() {

	}
}