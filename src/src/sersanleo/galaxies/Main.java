package src.sersanleo.galaxies;

import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.window.GameWindow;

public final class Main {
	public static void main(String[] args) throws BoardTooSmallException, CanNotAddGalaxyException {
		new GameWindow();
	}
}