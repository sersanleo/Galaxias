package src.sersanleo.galaxies.window.screen;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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

	public static final ImageIcon icon(String name) {
		try {
			return new ImageIcon(ImageIO.read(Screen.class.getResource("/icons/" + name)));
		} catch (IOException e) {
			e.printStackTrace();
			return new ImageIcon();
		}
	}
}