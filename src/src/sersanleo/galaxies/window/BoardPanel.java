package src.sersanleo.galaxies.window;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.window.painter.BoardPainter;
import src.sersanleo.galaxies.window.painter.GamePainter;

public class BoardPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public final BoardPainter painter;

	private BoardPanel(BoardPainter painter) {
		this.painter = painter;

		setSize(painter.width, painter.height);
		setPreferredSize(new Dimension(painter.width, painter.height));
	}

	public BoardPanel(Board board, float scale) {
		this(new BoardPainter(board, scale));

		addMouseListener(new BoardMouseListener(board, this));
	}

	public BoardPanel(Board board) {
		this(board, 1);
	}

	public BoardPanel(Game game, float scale) {
		this(new GamePainter(game, scale));

		GameMouseListener mouseListener = new GameMouseListener(game, this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
	}

	public BoardPanel(Game game) {
		this(game, 1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		painter.paint(g);
	}
}