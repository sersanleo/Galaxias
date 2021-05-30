package src.sersanleo.galaxies.game.generator;

import src.sersanleo.galaxies.util.Vector2f;

public class MaxGalaxyGenerator extends GalaxyGenerator {
	protected MaxGalaxyGenerator(PuzzleGenerator generator, Vector2f galaxy) {
		super(generator, galaxy);
	}

	@Override
	protected boolean isValid(int x, int y) {
		return !createsHole(x, y);
	}

	@Override
	protected boolean shouldContinue() {
		return next.size() > 0;
	}
}