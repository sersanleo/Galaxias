package src.sersanleo.galaxies.game.generator;

import src.sersanleo.galaxies.util.Vector2f;

public class FloodFillGalaxyGenerator extends GalaxyGenerator {
	protected FloodFillGalaxyGenerator(BoardGenerator generator, Vector2f galaxy) {
		super(generator, galaxy);
	}

	@Override
	protected boolean isValid(int x, int y) {
		return true;
	}

	@Override
	protected boolean shouldContinue() {
		return next.size() > 0;
	}
}