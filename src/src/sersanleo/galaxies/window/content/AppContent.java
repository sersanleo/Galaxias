package src.sersanleo.galaxies.window.content;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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

	public void added() {

	}

	protected static final Icon icon(String name) {
		try {
			return new ImageIcon(ImageIO.read(AppContent.class.getResource("/icons/" + name + ".png")));
		} catch (IOException e) {
			e.printStackTrace();
			return new ImageIcon();
		}
	}
}