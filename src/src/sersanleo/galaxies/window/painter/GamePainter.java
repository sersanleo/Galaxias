package src.sersanleo.galaxies.window.painter;

import java.awt.Color;

import src.sersanleo.galaxies.game.Game;

public class GamePainter extends BoardPainter {
	private final Game game;

	public GamePainter(Game game) {
		super(game.board);

		this.game = game;
	}

	@Override
	protected boolean horizontalEdge(int x, int y) {
		return game.horizontalEdge(x, y);
	}

	@Override
	protected boolean verticalEdge(int x, int y) {
		return game.verticalEdge(x, y);
	}

	@Override
	protected Color getCellColor(int x, int y) {
		if (game.solved(x, y))
			return Color.LIGHT_GRAY;
		else
			return Color.WHITE;
	}
}