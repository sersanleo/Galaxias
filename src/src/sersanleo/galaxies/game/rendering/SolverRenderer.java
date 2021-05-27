package src.sersanleo.galaxies.game.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.game.solver.SolverCell;
import src.sersanleo.galaxies.util.ColorUtil;

public class SolverRenderer extends BoardRenderer {
	private final static boolean DEBUG = true;

	private static final String[] indexcolors = new String[] { "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941",
			"#006FA6", "#A30059", "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF",
			"#997D87", "#5A0007", "#809693", "#FEFFE6", "#1B4400", "#4FC601", "#3B5DFF", "#4A3B53", "#FF2F80",
			"#61615A", "#BA0900", "#6B7900", "#00C2A0", "#FFAA92", "#FF90C9", "#B903AA", "#D16100", "#DDEFFF",
			"#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8", "#013349", "#00846F", "#372101", "#FFB500",
			"#C2FFED", "#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09", "#00489C", "#6F0062", "#0CBD66",
			"#EEC3FF", "#456D75", "#B77B68", "#7A87A1", "#788D66", "#885578", "#FAD09F", "#FF8A9A", "#D157A0",
			"#BEC459", "#456648", "#0086ED", "#886F4C", "#34362D", "#B4A8BD", "#00A6AA", "#452C2C", "#636375",
			"#A3C8C9", "#FF913F", "#938A81", "#575329", "#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757",
			"#C8A1A1", "#1E6E00", "#7900D7", "#A77500", "#6367A9", "#A05837", "#6B002C", "#772600", "#D790FF",
			"#9B9700", "#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF", "#99ADC0", "#3A2465", "#922329",
			"#5B4534", "#FDE8DC", "#404E55", "#0089A3", "#CB7E98", "#A4E804", "#324E72", "#6A3A4C", "#83AB58",
			"#001C1E", "#D1F7CE", "#004B28", "#C8D0F6", "#A3A489", "#806C66", "#222800", "#BF5650", "#E83000",
			"#66796D", "#DA007C", "#FF1A59", "#8ADBB4", "#1E0200", "#5B4E51", "#C895C5", "#320033", "#FF6832",
			"#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58" };

	private final Solver solver;

	public SolverRenderer(Solver solver, float scale) {
		super(solver.board, scale);

		this.solver = solver;
	}

	public SolverRenderer(Solver solver) {
		this(solver, 1);
	}

	private final Color getColorByGalaxy(Galaxy galaxy) {
		return Color.decode(indexcolors[solver.board.getGalaxyId(galaxy) % indexcolors.length]);
	}

	@Override
	protected Color getGalaxyColor(Galaxy galaxy) {
		Color color = getColorByGalaxy(galaxy);
		return ColorUtil.add(color, Color.WHITE, 0.5f);
	}

	@Override
	protected Color getCellColor(int x, int y) {
		Set<Galaxy> galaxies = solver.cell(x, y).getGalaxies();
		if (galaxies.size() == 1)
			return getColorByGalaxy(galaxies.iterator().next());
		return super.getCellColor(x, y);
	}

	@Override
	protected boolean horizontalEdge(int x, int y) {
		Set<Galaxy> cell1 = solver.cell(x, y).getGalaxies();
		SolverCell cell2 = solver.cell(x, y + 1);

		for (Galaxy g1 : cell1)
			if (cell2.contains(g1))
				return false;
		return !(cell1.size() == 0 && cell2.size() == 0);
	}

	@Override
	protected boolean verticalEdge(int x, int y) {
		Set<Galaxy> cell1 = solver.cell(x, y).getGalaxies();
		SolverCell cell2 = solver.cell(x + 1, y);

		for (Galaxy g1 : cell1)
			if (cell2.contains(g1))
				return false;
		return !(cell1.size() == 0 && cell2.size() == 0);
	}

	@Override
	protected void paintCells(Graphics2D g) {
		if (DEBUG) {
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					float x0 = selectedEdgeWidthAdd + edgeWidth + x * fullCellSize;
					float y0 = selectedEdgeWidthAdd + edgeWidth + y * fullCellSize;
					Rectangle2D clip = new Rectangle2D.Float(x0, y0, cellSize, cellSize);
					g.setClip(clip);

					Rectangle2D rect = new Rectangle2D.Float(x0 - cellSize, y0 - cellSize, 3 * cellSize, 3 * cellSize);
					SolverCell cell = solver.cell(x, y);
					int i = 0;
					float arc = 360f / cell.size();
					for (Galaxy galaxy : cell.getGalaxies()) {
						g.setColor(getColorByGalaxy(galaxy));
						g.fill(new Arc2D.Float(rect, 90 + i * arc, arc, Arc2D.PIE));
						i++;
					}
				}

			g.setClip(null);
		} else
			super.paintCells(g);
	}
}