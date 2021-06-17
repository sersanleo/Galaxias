package src.sersanleo.galaxies.game;

import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.util.BoundingBoxf;
import src.sersanleo.galaxies.util.Vector2f;
import src.sersanleo.galaxies.util.Vector2i;

public class Galaxy extends GalaxyVector {
	private static int i = 1;
	private static final float SMALL_RADIUS = 0.5001f;
	private static final float BIG_RADIUS = 1.0001f;

	public final BoundingBoxf smallBB;
	public final BoundingBoxf bigBB;
	public final int a = i++;

	public Galaxy(float x, float y) {
		super(x, y);

		this.smallBB = BoundingBoxf.fromPointAndRadius(x, y, SMALL_RADIUS);
		float width = 2 * ((Math.round(2 * x) % 2 == 0) ? SMALL_RADIUS : BIG_RADIUS);
		float height = 2 * ((Math.round(2 * y) % 2 == 0) ? SMALL_RADIUS : BIG_RADIUS);
		this.bigBB = BoundingBoxf.fromCenteredPointAndDimensions(x, y, width, height);
	}

	public Galaxy(Vector2f v) {
		this(v.x, v.y);
	}

	public final Set<Vector2i> getNeighbors() {
		Set<Vector2i> neighbors = new HashSet<Vector2i>();

		for (int x = (int) Math.floor(this.x); x <= (int) Math.ceil(this.x); x++)
			for (int y = (int) Math.floor(this.y); y <= (int) Math.ceil(this.y); y++)
				neighbors.add(new Vector2i(x, y));

		return neighbors;
	}
}