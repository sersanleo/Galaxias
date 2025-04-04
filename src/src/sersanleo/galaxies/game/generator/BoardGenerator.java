package src.sersanleo.galaxies.game.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.GalaxyVector;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.util.RandomUtil;
import src.sersanleo.galaxies.util.Vector2i;

public class BoardGenerator {
	private final static int MAX_FIXES = 1;

	protected final Board board;
	private final float difficulty;
	protected final RandomUtil rnd;

	private final int maxGalaxyArea;
	private final int minGalaxyArea;

	protected Galaxy[][] rows;
	private int emptyRows;
	protected List<GalaxyVector> galaxies;

	private BoardGenerator(int width, int height, float difficulty, RandomUtil rnd) throws BoardTooSmallException {
		board = new Board(width, height);
		this.difficulty = difficulty;
		this.rnd = rnd;

		maxGalaxyArea = (int) Math.ceil(Math.sqrt(board.area));
		minGalaxyArea = 4;
	}

	public BoardGenerator(int width, int height, float difficulty, long seed) throws BoardTooSmallException {
		this(width, height, difficulty, new RandomUtil(seed));
	}

	public BoardGenerator(int width, int height, float difficulty) throws BoardTooSmallException {
		this(width, height, difficulty, new RandomUtil());
	}

	private final void reset() {
		rows = new Galaxy[board.width][board.height];
		emptyRows = board.area;
		galaxies = initGalaxies();
		board.clear();
	}

	private final List<GalaxyVector> initGalaxies() {
		List<GalaxyVector> res = new ArrayList<GalaxyVector>();

		for (int x = 0; x < 2 * board.width - 1; x++)
			for (int y = 0; y < 2 * board.height - 1; y++)
				res.add(new GalaxyVector(x / 2f, y / 2f));

		Collections.shuffle(res, rnd.random);
		return res;
	}

	protected final void updateGalaxies() {
		for (int x = 0; x < 2 * board.width - 1; x++)
			for (int y = 0; y < 2 * board.height - 1; y++) {
				GalaxyVector galaxy = new GalaxyVector(x / 2f, y / 2f);
				boolean shouldBeAdded = true;

				for (int rowX = (int) Math.floor(galaxy.x); rowX <= (int) Math.ceil(galaxy.x); rowX++) {
					for (int rowY = (int) Math.floor(galaxy.y); rowY <= (int) Math.ceil(galaxy.y); rowY++) {
						if (rows[rowX][rowY] != null) {
							shouldBeAdded = false;
							break;
						}
					}

					if (!shouldBeAdded)
						break;
				}

				if (shouldBeAdded)
					galaxies.add(galaxy);
			}

		Collections.shuffle(galaxies, rnd.random);
	}

	protected final void remove(Galaxy galaxy) {
		if (board.remove(galaxy)) {
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++)
					if (rows[x][y] == galaxy)
						empty(x, y);
		}
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
		boolean empty = isEmpty(x, y);
		rows[x][y] = value;
		if (empty) {
			emptyRows--;
			for (int galaxyX = 2 * x - 1; galaxyX <= 2 * x + 1; galaxyX++)
				for (int galaxyY = 2 * y - 1; galaxyY <= 2 * y + 1; galaxyY++)
					galaxies.remove(new GalaxyVector(galaxyX / 2f, galaxyY / 2f));
		}
	}

	protected void fill(Vector2i v, Galaxy value) {
		fill(v.x, v.y, value);
	}

	protected void empty(int x, int y) {
		if (isFilled(x, y)) {
			rows[x][y] = null;
			emptyRows++;
		}
	}

	protected void empty(Vector2i v) {
		empty(v.x, v.y);
	}

	private final GalaxyGenerator getRandomGalaxyGenerator() {
		// Seleccionamos una galaxia que maximice el �rea
		GalaxyVector galaxy = null;
		FloodFillGalaxyGenerator sizeCalculator = null;

		if (galaxies.size() == 0)
			updateGalaxies();

		for (GalaxyVector currentGalaxy : galaxies) {
			FloodFillGalaxyGenerator currentSizeCalculator = new FloodFillGalaxyGenerator(this, currentGalaxy);
			currentSizeCalculator.generate();

			if (galaxy == null || currentSizeCalculator.getArea() > sizeCalculator.getArea()) {
				galaxy = currentGalaxy;
				sizeCalculator = currentSizeCalculator;
			}

			if (sizeCalculator.getArea() >= maxGalaxyArea)
				break;
		}

		int maxArea = Math.min(sizeCalculator.getArea(), maxGalaxyArea);
		int minArea = (int) Math.min(maxArea, minGalaxyArea);
		minArea = Math.round(0.9f * minArea + 0.1f * maxArea);

		int skeletonArea = sizeCalculator.getSkeletonArea();
		int randomArea = rnd.random(minArea, maxArea);
		int area = Math.round(difficulty * skeletonArea + (1 - difficulty) * randomArea);
		return new ParameterizedGalaxyGenerator(this, galaxy, area, difficulty);
	}

	private final void pass() {
		reset();

		int fixedCount = 0;
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
				if (++fixedCount > MAX_FIXES) {
					reset();
					fixedCount = 0;

					if (AppConfig.DEBUG)
						System.out.println("Creando otro tablero...");
				} else {
					if (AppConfig.DEBUG)
						System.out.println("Arreglando...");

					BoardGeneratorFixer fixer = new BoardGeneratorFixer(this, solver);
					fixer.fix();
				}
			}
		}
	}

	public final void generate() {
		while (true)
			try {
				pass();
				break;
			} catch (Exception e) {
				if (AppConfig.DEBUG)
					e.printStackTrace();
			}
	}
}