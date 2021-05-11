package src.sersanleo.galaxies.window.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.rendering.BoardRenderer;
import src.sersanleo.galaxies.game.rendering.GameRenderer;

public class BoardView extends JPanel {
	private static final long serialVersionUID = 1L;

	public final BoardRenderer renderer;

	private BoardView(BoardRenderer renderer) {
		this.renderer = renderer;
		fitToRendererSize();
	}

	public BoardView(Board board, float scale) {
		this(new BoardRenderer(board, scale));
	}

	public BoardView(Board board) {
		this(board, 1);
	}

	public BoardView(Board board, float width, float height) {
		this(new BoardRenderer(board));
	}

	public BoardView(Game game, float scale) {
		this(new GameRenderer(game, scale));
	}

	public BoardView(Game game) {
		this(game, 1);
	}

	private final void fitToRendererSize() {
		Dimension dimension = new Dimension(renderer.getWidth(), renderer.getHeight());
		super.setSize(dimension);
		super.setPreferredSize(dimension);
		super.setMaximumSize(dimension);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		renderer.paint((Graphics2D) g);
	}

	private final void fitRendererToSize(int width, int height) {
		float scale = Math.min((float) width / renderer.getWidth(), (float) height / renderer.getHeight());
		renderer.scale(scale);
	}

	@Override
	public final void setSize(int width, int height) {
		fitRendererToSize(width, height);

		super.setSize(width, height);
		super.setPreferredSize(new Dimension(width, height));
		super.setMaximumSize(new Dimension(width, height));
	}

	public final void fitToSize(int width, int height) {
		fitRendererToSize(width, height);
		fitToRendererSize();
	}

	public final void setScale(float scale) {
		renderer.setScale(scale);
		fitToRendererSize();
	}
}