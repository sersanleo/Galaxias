package src.sersanleo.galaxies.game.generator;

import src.sersanleo.galaxies.util.Vector2f;
import src.sersanleo.galaxies.util.Vector2i;

public class ParameterizedGalaxyGenerator extends GalaxyGenerator {
	private final int minWidth, maxWidth;
	private final int minHeight, maxHeight;
	private final int desiredArea;

	// Usados para la probabilidad gaussiana
	private final float b;
	private final float c;

	public ParameterizedGalaxyGenerator(BoardGenerator generator, Vector2f galaxy, int minWidth, int maxWidth,
			int minHeight, int maxHeight, int desiredArea, float difficulty) {
		super(generator, galaxy);

		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.desiredArea = desiredArea;

		this.b = 2.7f - difficulty * 1.7f; // Centro de la campana
		this.c = 0.7f - difficulty * 0.3f; // Apertura de la campana
	}

	public ParameterizedGalaxyGenerator(BoardGenerator generator, Vector2f galaxy, int desiredArea, float difficulty) {
		this(generator, galaxy, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, desiredArea, difficulty);
	}

	protected double weight(Vector2i v) {
		int tilesAround = 0;

		if (isVisited(v.x - 1, v.y))
			tilesAround++;
		if (isVisited(v.x + 1, v.y))
			tilesAround++;
		if (isVisited(v.x, v.y - 1))
			tilesAround++;
		if (isVisited(v.x, v.y + 1))
			tilesAround++;

		return Math.exp(-Math.pow(tilesAround - b, 2) / (2 * Math.pow(c, 2)));
	}

	private final boolean makesDesiredSize(int x, int y) {
		int newWidth = calculateWidth(x);
		int newHeight = calculateHeight(y);
		int width = getWidth();
		int height = getHeight();
		boolean widthNotReached = width < minWidth;
		boolean heightNotReached = height < minHeight;

		// Forzamos a alcanzar primero el tamaño mínimo
		if (widthNotReached || heightNotReached) {
			if (widthNotReached && newWidth > width)
				return true;

			if (heightNotReached && newHeight > height)
				return true;

			return false;
		}

		// Tamaño mínimo alcanzado; no permitimos que supere el máximo
		return newWidth <= maxWidth && newHeight <= maxHeight;
	}

	@Override
	protected boolean isValid(int x, int y) {
		return (MAKES_HOLE || !createsHole(x, y)) && makesDesiredSize(x, y);
	}

	private boolean sizeReached() {
		return this.getWidth() >= minWidth && this.getHeight() >= minHeight && this.getArea() >= desiredArea;
	}

	@Override
	protected boolean shouldContinue() {
		return next.size() > 0 && !sizeReached();
	}
}