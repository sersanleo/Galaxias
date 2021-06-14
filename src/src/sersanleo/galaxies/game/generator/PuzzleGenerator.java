package src.sersanleo.galaxies.game.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.GalaxyVector;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.util.RandomUtil;
import src.sersanleo.galaxies.util.Vector2i;

public class PuzzleGenerator {
	public final Board board;
	private final float difficulty;
	public final RandomUtil rnd;

	private final Galaxy[][] rows;
	private int emptyRows;

	private final int maxGalaxyArea;
	private final List<GalaxyVector> galaxies;

	private PuzzleGenerator(int width, int height, float difficulty, RandomUtil rnd) throws BoardTooSmallException {
		board = new Board(width, height);
		this.difficulty = difficulty;
		this.rnd = rnd;

		rows = new Galaxy[width][height];
		emptyRows = board.area;

		maxGalaxyArea = (int) Math.ceil(10);
		galaxies = initGalaxies();
	}

	public PuzzleGenerator(int width, int height, float difficulty, long seed) throws BoardTooSmallException {
		this(width, height, difficulty, new RandomUtil(seed));
	}

	public PuzzleGenerator(int width, int height, float difficulty) throws BoardTooSmallException {
		this(width, height, difficulty, new RandomUtil());
	}

	private final List<GalaxyVector> initGalaxies() {
		List<GalaxyVector> res = new ArrayList<GalaxyVector>();

		for (int x = 0; x < 2 * board.width - 1; x++)
			for (int y = 0; y < 2 * board.height - 1; y++)
				res.add(new GalaxyVector(x / 2f, y / 2f));

		Collections.shuffle(res, rnd.random);
		return res;
	}

	public final boolean isEmpty(int x, int y) {
		return board.overlaps(x, y) && rows[x][y] == null;
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

	protected void fill(int x, int y, Galaxy value) {
		if (isEmpty(x, y)) {
			rows[x][y] = value;
			emptyRows--;

			for (int galaxyX = 2 * x - 1; galaxyX <= 2 * x + 1; galaxyX++)
				for (int galaxyY = 2 * y - 1; galaxyY <= 2 * y + 1; galaxyY++)
					galaxies.remove(new Galaxy(galaxyX / 2f, galaxyY / 2f));
		}
	}

	private final GalaxyGenerator getRandomGalaxyGenerator() {
		GalaxyVector galaxy = null;
		int area = 0;
		for (GalaxyVector currentGalaxy : galaxies) {
			System.out.println(currentGalaxy);
			FloodFillGalaxyGenerator sizeCalculator = new FloodFillGalaxyGenerator(this, currentGalaxy);
			sizeCalculator.generate();

			if (sizeCalculator.getArea() > area) {
				galaxy = currentGalaxy;
				area = sizeCalculator.getArea();
			}

			if (area >= maxGalaxyArea)
				break;
		}

		galaxies.remove(galaxy);

		int availableArea = area;
		int minArea = (int) Math.round(0.5 * Math.pow(board.area, 1. / 2.));
		area = Math.min(availableArea, rnd.random(minArea, maxGalaxyArea));
		return new ParameterizedGalaxyGenerator(this, galaxy, area);
	}

	public final Board generate() {
		while (true) {
			while (emptyRows > 0) {
				GalaxyGenerator galaxyGenerator = getRandomGalaxyGenerator();
				galaxyGenerator.generate();
				galaxyGenerator.add();
			}

			Solver solver = new Solver(board, 2);
			solver.solve(rows);

			if (solver.getSolutions() == 1) {
				board.solution = solver.getSolution();
				break;
			} else {
				System.err.println("COMPLETAR LUEGO");
				break;
			}
		}

		return board;
	}
}