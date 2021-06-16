package src.sersanleo.galaxies.game.generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.util.RandomUtil.WeightedObject;
import src.sersanleo.galaxies.util.Vector2f;
import src.sersanleo.galaxies.util.Vector2i;

public abstract class GalaxyGenerator {
	private final PuzzleGenerator generator;
	private final Vector2f galaxy;

	private int width = 0;
	private int height = 0;

	private final List<Vector2i> visited = new ArrayList<Vector2i>();
	protected final List<Vector2i> next = new ArrayList<Vector2i>();

	protected GalaxyGenerator(PuzzleGenerator generator, Vector2f galaxy) {
		this.generator = generator;
		this.galaxy = galaxy;
	}

	private final void addNext(Vector2i v) {
		// Solo expandimos por la mitad de la galaxia
		if (v.y <= Math.floor(galaxy.y)) {
			Vector2i symmetric = galaxy.symmetric(v).round();
			if (!next.contains(v) && !visited.contains(v) && generator.isEmpty(v) && generator.isEmpty(symmetric))
				next.add(v);
		}
	}

	private final void addNext(int x, int y) {
		addNext(new Vector2i(x, y));
	}

	public final int getSkeletonArea() {
		int res = width + height;

		if (width % 2 == 1 || height % 2 == 1)
			res--;

		return res;
	}

	protected void individualFill(Vector2i v) {
		width = Math.max(width, calculateWidth(v.x));
		height = Math.max(height, calculateHeight(v.y));

		visited.add(v);
		next.remove(v);

		addNext(v.x - 1, v.y);
		addNext(v.x + 1, v.y);
		addNext(v.x, v.y - 1);
		addNext(v.x, v.y + 1);
	}

	private final void individualFill(int x, int y) {
		individualFill(new Vector2i(x, y));
	}

	private final void fill(Vector2i v) {
		individualFill(v);

		Vector2i symmetric = galaxy.symmetric(v).round();
		if (!v.equals(symmetric))
			individualFill(symmetric);
	}

	protected final boolean isVisited(int x, int y) {
		return visited.contains(new Vector2i(x, y));
	}
	
	// Esto servía para evitar la creación de galaxias con agujeros (galaxias rodeadas de una misma galaxia).
	// Creía que hacía que disminuía el número de soluciones y, por tanto, aligeraba la resolución y generación de tableros;
	// luego ví que no, pero lo dejo por si en un futuro me hace falta
	protected final boolean createsHole(int x, int y) {
		boolean[] surroundings = new boolean[] { isVisited(x - 1, y - 1), isVisited(x, y - 1), isVisited(x + 1, y - 1),
				isVisited(x + 1, y), isVisited(x + 1, y + 1), isVisited(x, y + 1), isVisited(x - 1, y + 1),
				isVisited(x - 1, y) };

		int changes = 0;
		boolean last = surroundings[0];
		for (int i = 1; i <= surroundings.length; i++) {
			boolean current = surroundings[i % surroundings.length];

			if (last != current && ++changes > 2)
				return true;

			last = current;
		}

		return false;
	}

	protected abstract boolean isValid(int x, int y);

	private final boolean isValid(Vector2i v) {
		return isValid(v.x, v.y);
	}

	protected double weight(Vector2i v) {
		return 1;
	}

	private final Vector2i getNextStep() {
		TreeSet<WeightedObject<Vector2i>> steps = new TreeSet<WeightedObject<Vector2i>>(
				Comparator.comparing(x -> x.weight));

		double sum = 0;
		for (Vector2i step : next)
			if (isValid(step)) {
				double weight = weight(step);
				sum += weight;
				steps.add(new WeightedObject<Vector2i>(step, weight));
			}

		if (steps.size() > 0)
			return generator.rnd.randomWeighted(steps, sum);
		else
			return null;
	}

	private final void fillCore() {
		for (int x = (int) Math.floor(galaxy.x); x <= (int) Math.ceil(galaxy.x); x++)
			for (int y = (int) Math.floor(galaxy.y); y <= (int) Math.ceil(galaxy.y); y++)
				individualFill(x, y);
	}

	protected abstract boolean shouldContinue();

	public final void generate() {
		fillCore();

		while (shouldContinue()) {
			Vector2i nextStep = getNextStep();

			if (nextStep == null)
				break;

			fill(nextStep);
		}
	}

	public final void add() {
		Galaxy galaxy = new Galaxy(this.galaxy);
		try {
			generator.board.addGalaxy(galaxy);

			for (Vector2i v : visited)
				generator.fill(v.x, v.y, galaxy);
		} catch (CanNotAddGalaxyException e) {
		}
	}

	public final int getArea() {
		return visited.size();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	protected final int calculateWidth(int x) {
		return Math.round(2 * Math.abs(x - galaxy.x)) + 1;
	}

	protected final int calculateHeight(int y) {
		return Math.round(2 * Math.abs(y - galaxy.y)) + 1;
	}
}