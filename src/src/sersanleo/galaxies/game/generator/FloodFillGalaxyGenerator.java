package src.sersanleo.galaxies.game.generator;

import src.sersanleo.galaxies.util.Vector2f;

public class FloodFillGalaxyGenerator extends GalaxyGenerator {
	protected FloodFillGalaxyGenerator(BoardGenerator generator, Vector2f galaxy) {
		super(generator, galaxy);
	}

	@SuppressWarnings("unused")
	@Override
	protected boolean isValid(int x, int y) {
		return MAKES_HOLE || !createsHole(x, y);
	}

	@Override
	protected boolean shouldContinue() {
		return next.size() > 0;
	}
}