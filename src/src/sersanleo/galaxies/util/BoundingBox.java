package src.sersanleo.galaxies.util;

public abstract class BoundingBox {
	public abstract boolean overlaps(float x, float y);

	public abstract boolean overlaps(int x, int y);

	public final boolean overlaps(Vector2f v) {
		return overlaps(v.x, v.y);
	}

	public final boolean overlaps(Vector2i v) {
		return overlaps(v.x, v.y);
	}
}