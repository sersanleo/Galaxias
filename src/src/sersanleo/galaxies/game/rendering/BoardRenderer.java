package src.sersanleo.galaxies.game.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;

public class BoardRenderer {
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

	private float cellSize = CELL_SIZE;
	private float edgeWidth = EDGE_WIDTH;
	private float selectedEdgeWidthAdd = SELECTED_EDGE_WIDTH_ADD;
	private float galaxyDiameter = GALAXY_DIAMETER;
	private float galaxyBorder = GALAXY_BORDER;

	private float selectedEdgeWidth = SELECTED_EDGE_WIDTH;
	private float selectedEdgeLength = SELECTED_EDGE_LENGTH;
	private float fullCellSize = FULL_CELL_SIZE;

	private int width;
	private int height;

	public BoardRenderer(Board board) {
		this.board = board;
	}

	public BoardRenderer(Board board, float scale) {
		this(board);
		scale(scale);
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

	public final void scale(float scale) {
		this.cellSize *= scale;
		this.edgeWidth *= scale;
		this.selectedEdgeWidthAdd *= scale;
		this.galaxyDiameter *= scale;
		this.galaxyBorder *= scale;

		this.selectedEdgeWidth *= scale;
		this.selectedEdgeLength *= scale;
		this.fullCellSize *= scale;

		this.width = (int) Math.ceil(board.width * cellSize + (board.width + 1) * edgeWidth + 2 * selectedEdgeWidthAdd);
		this.height = (int) Math
				.ceil(board.height * cellSize + (board.height + 1) * edgeWidth + 2 * selectedEdgeWidthAdd);
	}

	public final void paint(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Dibujar celdas
		{
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					g.setColor(getCellColor(x, y));

					float x0 = selectedEdgeWidthAdd + edgeWidth + x * fullCellSize;
					float y0 = selectedEdgeWidthAdd + edgeWidth + y * fullCellSize;
					g.fill(new Rectangle2D.Float(x0, y0, cellSize, cellSize));
				}
		}

		// Dibujar rejilla
		{
			g.setColor(EDGE_COLOR);

			// Horizontales
			float edgeLength = edgeWidth + board.width * fullCellSize;
			for (int y = 1; y < board.height; y++)
				g.fill(new Rectangle2D.Float(selectedEdgeWidthAdd, selectedEdgeWidthAdd + y * fullCellSize,
						edgeLength, edgeWidth));

			// Verticales
			edgeLength = edgeWidth + board.height * fullCellSize;
			for (int x = 1; x < board.width; x++)
				g.fill(new Rectangle2D.Float(selectedEdgeWidthAdd + x * fullCellSize, selectedEdgeWidthAdd, edgeWidth,
						edgeLength));
		}

		// Dibujar aristas seleccionadas
		{
			g.setColor(SELECTED_EDGE_COLOR);

			// Horizontal
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y <= board.height; y++)
					if (y == 0 || y == board.height || horizontalEdge(x, y - 1)) {
						float x0 = x * fullCellSize;
						float y0 = y * fullCellSize;

						g.fill(new RoundRectangle2D.Float(x0, y0, selectedEdgeLength, selectedEdgeWidth,
								selectedEdgeWidth, selectedEdgeWidth));
					}

			// Vertical
			for (int x = 0; x <= board.width; x++)
				for (int y = 0; y < board.height; y++)
					if (x == 0 || x == board.width || verticalEdge(x - 1, y)) {
						float x0 = x * fullCellSize;
						float y0 = y * fullCellSize;

						g.fill(new RoundRectangle2D.Float(x0, y0, selectedEdgeWidth, selectedEdgeLength,
								selectedEdgeWidth, selectedEdgeWidth));
					}
		}

		// Dibujar galaxias
		{
			for (Galaxy galaxy : board.getGalaxies()) {
				float x0 = selectedEdgeWidth / 2 + (galaxy.x + 0.5f) * fullCellSize - galaxyDiameter / 2;
				float y0 = selectedEdgeWidth / 2 + (galaxy.y + 0.5f) * fullCellSize - galaxyDiameter / 2;

				g.setColor(GALAXY_BORDER_COLOR);
				g.fill(new Ellipse2D.Float(x0 - galaxyBorder, y0 - galaxyBorder, galaxyDiameter + 2 * galaxyBorder,
						galaxyDiameter + 2 * galaxyBorder));

				g.setColor(GALAXY_COLOR);
				g.fill(new Ellipse2D.Float(x0, y0, galaxyDiameter, galaxyDiameter));
			}
		}
	}

	public final float getCellSize() {
		return cellSize;
	}

	public final float getEdgeWidth() {
		return edgeWidth;
	}

	public final float getSelectedEdgeWidthAdd() {
		return selectedEdgeWidthAdd;
	}

	public final float getGalaxyDiameter() {
		return galaxyDiameter;
	}

	public final float getGalaxyBorder() {
		return galaxyBorder;
	}

	public final float getSelectedEdgeWidth() {
		return selectedEdgeWidth;
	}

	public final float getSelectedEdgeLength() {
		return selectedEdgeLength;
	}

	public final float getFullCellSize() {
		return fullCellSize;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}
}
