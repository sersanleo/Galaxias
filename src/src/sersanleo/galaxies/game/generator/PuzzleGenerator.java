package src.sersanleo.galaxies.game.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.util.RandomUtil;
import src.sersanleo.galaxies.util.Vector2f;
import src.sersanleo.galaxies.util.Vector2i;

public class PuzzleGenerator {
	public final Board board;

	private final float difficulty;

	public final RandomUtil rnd;

	private final Galaxy[][] rows;
	private int emptyRows;

	private final int[][] horizontalHeatmap;
	private final int[][] verticalHeatmap;

	private PuzzleGenerator(int width, int height, float difficulty, RandomUtil rnd) throws BoardTooSmallException {
		board = new Board(width, height);

		this.difficulty = difficulty;

		this.rnd = rnd;

		rows = new Galaxy[width][height];
		emptyRows = width * height;

		horizontalHeatmap = new int[width * 2 - 1][height * 2 - 1];
		verticalHeatmap = new int[width * 2 - 1][height * 2 - 1];
		initHeatmap();
	}

	public PuzzleGenerator(int width, int height, float difficulty, long seed) throws BoardTooSmallException {
		this(width, height, difficulty, new RandomUtil(seed));
	}

	public PuzzleGenerator(int width, int height, float difficulty) throws BoardTooSmallException {
		this(width, height, difficulty, new Random().nextLong());
	}

	private final void initHeatmap() {
		for (int x = 0; x < horizontalHeatmap.length; x++)
			for (int y = 0; y < horizontalHeatmap[0].length; y++)
				horizontalHeatmap[x][y] = Math.min(x + 1, horizontalHeatmap.length - x);

		for (int x = 0; x < verticalHeatmap.length; x++)
			for (int y = 0; y < verticalHeatmap[0].length; y++)
				verticalHeatmap[x][y] = Math.min(y + 1, verticalHeatmap[0].length - y);
	}

	public final Galaxy getGalaxy(int x, int y) {
		if (!board.overlaps(x, y))
			return null;
		return rows[x][y];
	}

	public final boolean isEmpty(int x, int y) {
		return board.overlaps(x, y) && getGalaxy(x, y) == null;
	}

	public final boolean isEmpty(Vector2i v) {
		return isEmpty(v.x, v.y);
	}

	public final boolean isFilled(int x, int y) {
		return !isEmpty(x, y);
	}

	public final boolean isFilled(Vector2i v) {
		return isFilled(v.x, v.y);
	}

	private final void setHorizontalHeatmap(int x, int y, int heat) {
		if (x >= 0 && x < horizontalHeatmap.length && y >= 0 && y < horizontalHeatmap[0].length
				&& horizontalHeatmap[x][y] > heat) {
			horizontalHeatmap[x][y] = heat;

			heat++;

			setHorizontalHeatmap(x - 1, y, heat);
			setHorizontalHeatmap(x + 1, y, heat);
		}
	}

	private final void setVerticalHeatmap(int x, int y, int heat) {
		if (x >= 0 && x < verticalHeatmap.length && y >= 0 && y < verticalHeatmap[0].length
				&& verticalHeatmap[x][y] > heat) {
			verticalHeatmap[x][y] = heat;

			heat++;

			setVerticalHeatmap(x, y - 1, heat);
			setVerticalHeatmap(x, y + 1, heat);
		}
	}

	private final void setHeatmap(int x, int y, int heat) {
		if (x >= 0 && x < verticalHeatmap.length && y >= 0 && y < verticalHeatmap[0].length && heat == 0) {
			setHorizontalHeatmap(x, y, heat);
			setVerticalHeatmap(x, y, heat);
		}
	}

	protected void fill(int x, int y, Galaxy value) {
		if (isEmpty(x, y)) {
			rows[x][y] = value;
			emptyRows--;

			int x2 = 2 * x;
			int y2 = 2 * y;

			setHeatmap(x2 - 1, y2 - 1, 0);
			setHeatmap(x2, y2 - 1, 0);
			setHeatmap(x2 + 1, y2 - 1, 0);

			setHeatmap(x2 - 1, y2, 0);
			setHeatmap(x2, y2, 0);
			setHeatmap(x2 + 1, y2, 0);

			setHeatmap(x2 - 1, y2 + 1, 0);
			setHeatmap(x2, y2 + 1, 0);
			setHeatmap(x2 + 1, y2 + 1, 0);
		}
	}

	private final List<Vector2f> getPossibleGalaxies() {
		List<Vector2f> res = new ArrayList<Vector2f>();

		int max = 0;
		for (int x = 0; x < horizontalHeatmap.length; x++)
			for (int y = 0; y < horizontalHeatmap[0].length; y++) {
				int heat = Math.min(horizontalHeatmap[x][y] * verticalHeatmap[x][y],
						Math.round(board.width * board.height * 0.25f));

				if ((x == 0 && y == 0) || (heat > max)) {
					max = heat;
					res.clear();
					res.add(new Vector2f(x / 2f, y / 2f));
				} else if (heat == max)
					res.add(new Vector2f(x / 2f, y / 2f));
			}

		return res;
	}

	private final GalaxyGenerator getRandomGalaxyGenerator() {
		Vector2f galaxy = rnd.random(getPossibleGalaxies());

		MaxGalaxyGenerator sizeCalculator = new MaxGalaxyGenerator(this, galaxy);
		sizeCalculator.generate();

		int width = sizeCalculator.getWidth();
		int desiredWidth = Math.min(width, (width % 2 == 0) ? 6 : 7);

		int height = sizeCalculator.getHeight();
		int desiredHeight = Math.min(height, (height % 2 == 0) ? 6 : 7);

		int availableArea = sizeCalculator.getArea();
		int maxArea = (int) Math.round(Math.pow(board.width * board.height, 1. / 2.));
		int minArea = (int) Math.round(0.5 * Math.pow(board.width * board.height, 1. / 2.));
		int area = Math.min(availableArea, rnd.random(minArea, maxArea));
		return new ParameterizedGalaxyGenerator(this, galaxy, area);
	}

	private void generate() {
		int iteration = 0;

		while (true) {
			while (emptyRows > 0) {
				GalaxyGenerator galaxyGenerator = getRandomGalaxyGenerator();
				galaxyGenerator.generate();
				galaxyGenerator.add();
			}

			iteration++;

			Solver solver = new Solver(board, 2);
			solver.solve();
			if (solver.getSolutions() == 1)
				board.solution = solver.getSolution();
			else if (solver.getSolutions() > 1) {
				System.err.println("COMPLETAR LUEGO");
			}
			break;
		}
	}

	public Board get() {
		if (emptyRows > 0)
			generate();
		return board;
	}
}