package src.sersanleo.galaxies.window;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.rendering.BoardRenderer;
import src.sersanleo.galaxies.game.rendering.GameRenderer;

public class BoardPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public final BoardRenderer renderer;

	private BoardPanel(BoardRenderer renderer) {
		this.renderer = renderer;

		super.setSize(renderer.getWidth(), renderer.getHeight());
		super.setPreferredSize(new Dimension(renderer.getWidth(), renderer.getHeight()));
	}

	public BoardPanel(Board board, float scale) {
		this(new BoardRenderer(board, scale));
	}

	public BoardPanel(Board board) {
		this(board, 1);
	}

	public BoardPanel(Board board, float width, float height) {
		this(new BoardRenderer(board));
	}

	public BoardPanel(Game game, float scale) {
		this(new GameRenderer(game, scale));
	}

	public BoardPanel(Game game) {
		this(game, 1);
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
	}

	public final void fitToSize(int width, int height) {
		fitRendererToSize(width, height);

		super.setSize(renderer.getWidth(), renderer.getHeight());
		super.setPreferredSize(new Dimension(renderer.getWidth(), renderer.getHeight()));
	}
}