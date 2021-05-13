package src.sersanleo.galaxies.game.rendering;

import java.awt.Color;

import src.sersanleo.galaxies.game.Game;

public class GameRenderer extends BoardRenderer {
	private final static Color WIN_COLOR = new Color(175, 207, 175);

	private final Game game;

	public GameRenderer(Game game, float scale) {
		super(game.board, scale);

		this.game = game;
	}

	public GameRenderer(Game game) {
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
		if (game.solution.isSolved())
			return WIN_COLOR;
		return game.solution.cell(x, y).color;
	}
}