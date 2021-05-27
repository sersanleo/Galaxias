package src.sersanleo.galaxies.game.rendering;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.window.component.listener.GameMouseListener;

public class BoardRenderer {
	private static final boolean DEBUG = false;

	private static final float CELL_SIZE = 45;
	private static final float EDGE_WIDTH = 1;
	private static final float SELECTED_EDGE_WIDTH_ADD = 2;
	private static final float GALAXY_DIAMETER = 13;
	private static final float GALAXY_BORDER = 1.5f;

	// Valores calculados
	private static final float SELECTED_EDGE_WIDTH = EDGE_WIDTH + 2 * SELECTED_EDGE_WIDTH_ADD;
	private static final float SELECTED_EDGE_LENGTH = CELL_SIZE + 2 * (EDGE_WIDTH + SELECTED_EDGE_WIDTH_ADD);
	private static final float FULL_CELL_SIZE = CELL_SIZE + EDGE_WIDTH;

	// Colores
	protected static final Color EDGE_COLOR = Color.GRAY;
	protected static final Color SELECTED_EDGE_COLOR = Color.BLACK;
	protected static final Color GALAXY_BORDER_COLOR = Color.DARK_GRAY;

	public final Board board;

	private float scale = 1;

	protected float cellSize = CELL_SIZE;
	protected float edgeWidth = EDGE_WIDTH;
	protected float selectedEdgeWidthAdd = SELECTED_EDGE_WIDTH_ADD;
	protected float galaxyDiameter = GALAXY_DIAMETER;
	protected float galaxyBorder = GALAXY_BORDER;

	protected float selectedEdgeWidth = SELECTED_EDGE_WIDTH;
	protected float selectedEdgeLength = SELECTED_EDGE_LENGTH;
	protected float fullCellSize = FULL_CELL_SIZE;

	private int width;
	private int height;

	public BoardRenderer(Board board, float scale) {
		this.board = board;
		scale(scale);
	}

	public BoardRenderer(Board board) {
		this(board, 1);
	}

	public final void scale(float scale) {
		this.scale *= scale;

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

	public final void setScale(float scale) {
		scale(scale / this.scale);
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

	protected Color getGalaxyColor(Galaxy galaxy) {
		return Color.WHITE;
	}

	protected void paintCells(Graphics2D g) {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				g.setColor(getCellColor(x, y));

				float x0 = selectedEdgeWidthAdd + edgeWidth + x * fullCellSize;
				float y0 = selectedEdgeWidthAdd + edgeWidth + y * fullCellSize;
				g.fill(new Rectangle2D.Float(x0, y0, cellSize, cellSize));
			}
	}

	public final void paint(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Dibujar celdas
		paintCells(g);

		// Dibujar rejilla
		{
			g.setColor(EDGE_COLOR);

			// Horizontales
			float edgeLength = edgeWidth + board.width * fullCellSize;
			for (int y = 1; y < board.height; y++)
				g.fill(new Rectangle2D.Float(selectedEdgeWidthAdd, selectedEdgeWidthAdd + y * fullCellSize, edgeLength,
						edgeWidth));

			// Verticales
			edgeLength = edgeWidth + board.height * fullCellSize;
			for (int x = 1; x < board.width; x++)
				g.fill(new Rectangle2D.Float(selectedEdgeWidthAdd + x * fullCellSize, selectedEdgeWidthAdd, edgeWidth,
						edgeLength));
		}

		// Dividir cada casilla en 4 triángulos
		if (DEBUG) {
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					g.draw(new Line2D.Float(selectedEdgeWidthAdd + fullCellSize * x,
							selectedEdgeWidthAdd + fullCellSize * y, selectedEdgeWidthAdd + fullCellSize * (x + 1),
							selectedEdgeWidthAdd + fullCellSize * (y + 1)));
					g.draw(new Line2D.Float(selectedEdgeWidthAdd + fullCellSize * (x + 1),
							selectedEdgeWidthAdd + fullCellSize * y, selectedEdgeWidthAdd + fullCellSize * x,
							selectedEdgeWidthAdd + fullCellSize * (y + 1)));
				}

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

		// Mostrar área de interacción de las aristas
		if (DEBUG) {
			g.setColor(Color.BLUE);
			float width = (0.5f - GameMouseListener.CENTER_THRESHOLD) * cellSize;
			float length = (cellSize + 2 * (edgeWidth)) * (1 - 2 * GameMouseListener.LENGTH_THRESHOLD);
			for (int x = 0; x < board.width; x++)
				for (int y = 1; y < board.height; y++) {
					float x0 = selectedEdgeWidthAdd + (x + 0.5f) * fullCellSize - length / 2f;
					float y0 = selectedEdgeWidthAdd + y * fullCellSize - width;

					g.draw(new Rectangle2D.Float(x0, y0, length, width * 2));

				}

			for (int x = 1; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					float x0 = selectedEdgeWidthAdd + x * fullCellSize - width;
					float y0 = selectedEdgeWidthAdd + (y + 0.5f) * fullCellSize - length / 2f;

					g.draw(new Rectangle2D.Float(x0, y0, width * 2, length));

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

				g.setColor(getGalaxyColor(galaxy));
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

	public final void save(Component component) {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setDialogTitle("Guardar imagen del tablero");
		FileNameExtensionFilter tsb = new FileNameExtensionFilter("JPG (.jpg)", "jpg");
		fileChooser.addChoosableFileFilter(tsb);
		fileChooser.setFileFilter(tsb);
		File defaultFile = new File("F:\\Sergio\\Desktop\\TABLEROS\\" + System.currentTimeMillis() + ".jpg");
		if (!defaultFile.getParentFile().exists())
			defaultFile = new File(defaultFile.getName());
		fileChooser.setSelectedFile(defaultFile);

		int userSelection = fileChooser.showSaveDialog(component);

		if (userSelection == JFileChooser.APPROVE_OPTION)
			this.save(fileChooser.getSelectedFile());
	}

	public final void save(File file) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		paint(g);
		g.dispose();

		ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
		ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
		jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpgWriteParam.setCompressionQuality(1f);

		try {
			ImageInputStream iis = ImageIO.createImageOutputStream(file);
			jpgWriter.setOutput(iis);
			IIOImage outputImage = new IIOImage(img, null, null);
			jpgWriter.write(null, outputImage, jpgWriteParam);
			iis.flush();
			iis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		jpgWriter.dispose();
	}
}
