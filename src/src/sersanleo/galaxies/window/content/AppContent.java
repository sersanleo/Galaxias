package src.sersanleo.galaxies.window.content;

import javax.swing.JPanel;

import src.sersanleo.galaxies.window.GameWindow;

public abstract class AppContent extends JPanel {
	private static final long serialVersionUID = 1L;

	protected final GameWindow window;

	public AppContent(GameWindow window) {
		this.window = window;
	}

	public boolean canBeRemoved() {
		return true;
	}

	public abstract void release();
}