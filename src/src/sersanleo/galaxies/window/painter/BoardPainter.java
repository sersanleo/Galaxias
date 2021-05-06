package src.sersanleo.galaxies.window.painter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;

public class BoardPainter {
	public static final int CELL_SIZE = 45;
	public static final int EDGE_WIDTH = 1;
	public static final int SELECTED_EDGE_WIDTH_ADD = 2;
	private static final int GALAXY_DIAMETER = 13;
	private static final int GALAXY_BORDER = 1;

	private static final Color EDGE_COLOR = Color.GRAY;
	private static final Color SELECTED_EDGE_COLOR = Color.BLACK;
	private static final Color GALAXY_COLOR = Color.GRAY;
	private static final Color GALAXY_BORDER_COLOR = Color.DARK_GRAY;

	// Valores calculados
	private static final int SELECTED_EDGE_WIDTH = EDGE_WIDTH + 2 * SELECTED_EDGE_WIDTH_ADD;
	private static final int SELECTED_EDGE_LENGTH = CELL_SIZE + 2 * (EDGE_WIDTH + SELECTED_EDGE_WIDTH_ADD);
	public static final int FULL_CELL_SIZE = CELL_SIZE + EDGE_WIDTH;

	public final Board board;
	public final int width;
	public final int height;

	public BoardPainter(Board board) {
		this.board = board;
		this.width = board.width * CELL_SIZE + (board.width + 1) * EDGE_WIDTH + 2 * SELECTED_EDGE_WIDTH_ADD;
		this.height = board.height * CELL_SIZE + (board.height + 1) * EDGE_WIDTH + 2 * SELECTED_EDGE_WIDTH_ADD;
	}

	protected boolean horizontalEdge(int x, int y) {
		return false;
	}

	protected boolean verticalEdge(int x, int y) {
		return false;
	}

	protected Color getCellColor(int x, int y) {
		return Color.WHITE;
	}

	public final void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Dibujar celdas
		{
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					g.setColor(getCellColor(x, y));

					int x0 = SELECTED_EDGE_WIDTH_ADD + EDGE_WIDTH + x * FULL_CELL_SIZE;
					int y0 = SELECTED_EDGE_WIDTH_ADD + EDGE_WIDTH + y * FULL_CELL_SIZE;
					g.fillRect(x0, y0, CELL_SIZE, CELL_SIZE);
				}
		}

		// Dibujar rejilla
		{
			g.setColor(EDGE_COLOR);
			// Horizontales
			int edgeLength = EDGE_WIDTH + board.width * FULL_CELL_SIZE;
			for (int y = 1; y < board.height; y++)
				g.fillRect(SELECTED_EDGE_WIDTH_ADD, SELECTED_EDGE_WIDTH_ADD + y * FULL_CELL_SIZE, edgeLength,
						EDGE_WIDTH);

			// Verticales
			edgeLength = EDGE_WIDTH + board.height * FULL_CELL_SIZE;
			for (int x = 1; x < board.width; x++)
				g.fillRect(SELECTED_EDGE_WIDTH_ADD + x * FULL_CELL_SIZE, SELECTED_EDGE_WIDTH_ADD, EDGE_WIDTH,
						edgeLength);
		}

		// Dibujar aristas seleccionadas
		{
			g.setColor(SELECTED_EDGE_COLOR);
			// Horizontal
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y <= board.height; y++)
					if (y == 0 || y == board.height || horizontalEdge(x, y - 1)) {
						int x0 = x * FULL_CELL_SIZE;
						int y0 = y * FULL_CELL_SIZE;
						g.fillRoundRect(x0, y0, SELECTED_EDGE_LENGTH, SELECTED_EDGE_WIDTH, SELECTED_EDGE_WIDTH,
								SELECTED_EDGE_WIDTH);
					}

			// Vertical
			for (int x = 0; x <= board.width; x++)
				for (int y = 0; y < board.height; y++)
					if (x == 0 || x == board.width || verticalEdge(x - 1, y)) {
						int x0 = x * FULL_CELL_SIZE;
						int y0 = y * FULL_CELL_SIZE;
						g.fillRoundRect(x0, y0, SELECTED_EDGE_WIDTH, SELECTED_EDGE_LENGTH, SELECTED_EDGE_WIDTH,
								SELECTED_EDGE_WIDTH);
					}
		}

		// Dibujar galaxias
		{
			for (Galaxy galaxy : board.getGalaxies()) {
				int x0 = SELECTED_EDGE_WIDTH_ADD
						+ (int) Math.round((galaxy.x + 0.5f) * FULL_CELL_SIZE - GALAXY_DIAMETER / 2.);
				int y0 = SELECTED_EDGE_WIDTH_ADD
						+ (int) Math.round((galaxy.y + 0.5f) * FULL_CELL_SIZE - GALAXY_DIAMETER / 2.);

				g.setColor(GALAXY_BORDER_COLOR);
				g.fillOval(x0 - GALAXY_BORDER, y0 - GALAXY_BORDER, GALAXY_DIAMETER + 2 * GALAXY_BORDER,
						GALAXY_DIAMETER + 2 * GALAXY_BORDER);
				g.setColor(GALAXY_COLOR);
				g.fillOval(x0, y0, GALAXY_DIAMETER, GALAXY_DIAMETER);
			}
		}
	}
}
