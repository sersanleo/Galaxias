package src.sersanleo.galaxies.window.painter;

import java.awt.Color;

import src.sersanleo.galaxies.game.Game;

public class GamePainter extends BoardPainter {
	private final Game game;

	public GamePainter(Game game, float scale) {
		super(game.board, scale);

		this.game = game;
	}

	public GamePainter(Game game) {
		this(game, 1);
	}

	@Override
	protected boolean horizontalEdge(int x, int y) {
		return game.solution.horizontalEdge(x, y);
	}

	@Override
	protected boolean verticalEdge(int x, int y) {
		return game.solution.verticalEdge(x, y);
	}

	@Override
	protected Color getCellColor(int x, int y) {
		if (game.solution.solved(x, y))
			return Color.LIGHT_GRAY;
		else
			return Color.WHITE;
	}
}