package src.sersanleo.galaxies.game.generator;

import src.sersanleo.galaxies.util.Vector2f;
import src.sersanleo.galaxies.util.Vector2i;

public class ParameterizedGalaxyGenerator extends GalaxyGenerator {
	private final int minWidth, maxWidth;
	private final int minHeight, maxHeight;
	private final int desiredArea;

	public ParameterizedGalaxyGenerator(PuzzleGenerator generator, Vector2f galaxy, int minWidth, int maxWidth,
			int minHeight, int maxHeight, int desiredArea) {
		super(generator, galaxy);

		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.desiredArea = desiredArea;
	}

	public ParameterizedGalaxyGenerator(PuzzleGenerator generator, Vector2f galaxy, int desiredArea) {
		this(generator, galaxy, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, desiredArea);
	}

	@Override
	protected void individualFill(Vector2i v) {
		super.individualFill(v);
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
		return !createsHole(x, y) && makesDesiredSize(x, y);
	}

	private boolean sizeReached() {
		return this.getWidth() >= minWidth && this.getHeight() >= minHeight && this.getArea() >= desiredArea;
	}

	@Override
	protected boolean shouldContinue() {
		return next.size() > 0 && !sizeReached();
	}
}