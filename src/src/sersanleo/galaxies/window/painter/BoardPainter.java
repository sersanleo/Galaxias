package src.sersanleo.galaxies.window.painter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;

public class BoardPainter {
	private static final float CELL_SIZE = 45;
	private static final float EDGE_WIDTH = 1;
	private static final float SELECTED_EDGE_WIDTH_ADD = 2;
	private static final float GALAXY_DIAMETER = 13;
	private static final float GALAXY_BORDER = 1.5f;

	private static final Color EDGE_COLOR = Color.GRAY;
	private static final Color SELECTED_EDGE_COLOR = Color.BLACK;
	private static final Color GALAXY_COLOR = Color.GRAY;
	private static final Color GALAXY_BORDER_COLOR = Color.DARK_GRAY;

	// Valores calculados
	private static final float SELECTED_EDGE_WIDTH = EDGE_WIDTH + 2 * SELECTED_EDGE_WIDTH_ADD;
	private static final float SELECTED_EDGE_LENGTH = CELL_SIZE + 2 * (EDGE_WIDTH + SELECTED_EDGE_WIDTH_ADD);
	private static final float FULL_CELL_SIZE = CELL_SIZE + EDGE_WIDTH;

	public final Board board;

	public final float cellSize;
	public final float edgeWidth;
	public final float selectedEdgeWidthAdd;
	public final float galaxyDiameter;
	public final float galaxyBorder;

	public final float selectedEdgeWidth;
	public final float selectedEdgeLength;
	public final float fullCellSize;

	public final int width;
	public final int height;

	public BoardPainter(Board board, float scale) {
		this.board = board;

		this.cellSize = scale * CELL_SIZE;
		this.edgeWidth = scale * EDGE_WIDTH;
		this.selectedEdgeWidthAdd = scale * SELECTED_EDGE_WIDTH_ADD;
		this.galaxyDiameter = scale * GALAXY_DIAMETER;
		this.galaxyBorder = scale * GALAXY_BORDER;

		this.selectedEdgeWidth = scale * SELECTED_EDGE_WIDTH;
		this.selectedEdgeLength = scale * SELECTED_EDGE_LENGTH;
		this.fullCellSize = scale * FULL_CELL_SIZE;

		this.width = (int) Math.ceil(board.width * cellSize + (board.width + 1) * edgeWidth + 2 * selectedEdgeWidthAdd);
		this.height = (int) Math
				.ceil(board.height * cellSize + (board.height + 1) * edgeWidth + 2 * selectedEdgeWidthAdd);
	}

	public BoardPainter(Board board) {
		this(board, 1);
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

					float x0 = selectedEdgeWidthAdd + edgeWidth + x * fullCellSize;
					float y0 = selectedEdgeWidthAdd + edgeWidth + y * fullCellSize;
					g2d.fill(new Rectangle2D.Float(x0, y0, cellSize, cellSize));
				}
		}

		// Dibujar rejilla
		{
			g2d.setColor(EDGE_COLOR);

			// Horizontales
			float edgeLength = edgeWidth + board.width * fullCellSize;
			for (int y = 1; y < board.height; y++)
				g2d.fill(new Rectangle2D.Float(selectedEdgeWidthAdd, selectedEdgeWidthAdd + y * fullCellSize,
						edgeLength, edgeWidth));

			// Verticales
			edgeLength = edgeWidth + board.height * fullCellSize;
			for (int x = 1; x < board.width; x++)
				g2d.fill(new Rectangle2D.Float(selectedEdgeWidthAdd + x * fullCellSize, selectedEdgeWidthAdd, edgeWidth,
						edgeLength));
		}

		// Dibujar aristas seleccionadas
		{
			g2d.setColor(SELECTED_EDGE_COLOR);

			// Horizontal
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y <= board.height; y++)
					if (y == 0 || y == board.height || horizontalEdge(x, y - 1)) {
						float x0 = x * fullCellSize;
						float y0 = y * fullCellSize;

						g2d.fill(new RoundRectangle2D.Float(x0, y0, selectedEdgeLength, selectedEdgeWidth,
								selectedEdgeWidth, selectedEdgeWidth));
					}

			// Vertical
			for (int x = 0; x <= board.width; x++)
				for (int y = 0; y < board.height; y++)
					if (x == 0 || x == board.width || verticalEdge(x - 1, y)) {
						float x0 = x * fullCellSize;
						float y0 = y * fullCellSize;

						g2d.fill(new RoundRectangle2D.Float(x0, y0, selectedEdgeWidth, selectedEdgeLength,
								selectedEdgeWidth, selectedEdgeWidth));
					}
		}

		// Dibujar galaxias
		{
			for (Galaxy galaxy : board.getGalaxies()) {
				float x0 = selectedEdgeWidth / 2 + (galaxy.x + 0.5f) * fullCellSize - galaxyDiameter / 2;
				float y0 = selectedEdgeWidth / 2 + (galaxy.y + 0.5f) * fullCellSize - galaxyDiameter / 2;

				g2d.setColor(GALAXY_BORDER_COLOR);
				g2d.fill(new Ellipse2D.Float(x0 - galaxyBorder, y0 - galaxyBorder, galaxyDiameter + 2 * galaxyBorder,
						galaxyDiameter + 2 * galaxyBorder));

				g2d.setColor(GALAXY_COLOR);
				g2d.fill(new Ellipse2D.Float(x0, y0, galaxyDiameter, galaxyDiameter));
			}
		}
	}
}
