package src.sersanleo.galaxies;

import javax.swing.UIManager;

import src.sersanleo.galaxies.window.GameWindow;

public final class Main {
	public static void main(String[] args) {
		// Para evitar problemas de visualización (bug de Java que depende de la
		// versión)
		System.setProperty("sun.java2d.opengl", "true");

		// Establece el Look & Feel del sistema
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			if (AppConfig.DEBUG)
				e.printStackTrace();
		}

		GameWindow window = new GameWindow();
		window.setVisible(true);
	}
}