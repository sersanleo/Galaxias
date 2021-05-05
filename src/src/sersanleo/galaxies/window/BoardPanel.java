package src.sersanleo.galaxies.window;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.BoardPainter;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.GamePainter;

public class BoardPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final BoardPainter painter;

	private BoardPanel(BoardPainter painter) {
		this.painter = painter;

		setSize(painter.width, painter.height);
		setPreferredSize(new Dimension(painter.width, painter.height));
	}

	public BoardPanel(Board board) {
		this(new BoardPainter(board));
	}

	public BoardPanel(Game game) {
		this(new GamePainter(game));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		painter.paint(g);
	}
}