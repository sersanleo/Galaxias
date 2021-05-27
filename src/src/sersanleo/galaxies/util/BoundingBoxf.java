package src.sersanleo.galaxies.util;

public class BoundingBoxf extends BoundingBox {
	public final float minX;
	public final float maxX;
	public final float minY;
	public final float maxY;

	public BoundingBoxf(float minX, float maxX, float minY, float maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	@Override
	public boolean overlaps(float x, float y) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	@Override
	public boolean overlaps(int x, int y) {
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	public static BoundingBoxf fromPointAndDimensions(float x, float y, float width, float height) {
		return new BoundingBoxf(x, x + width, y, y + height);
	}

	public static BoundingBoxf fromCenteredPointAndDimensions(float x, float y, float width, float height) {
		float semiWidth = width / 2f;
		float semiHeight = height / 2f;
		return new BoundingBoxf(x - semiWidth, x + semiWidth, y - semiHeight, y + semiHeight);
	}

	public static BoundingBoxf fromCenteredPointAndDimensions(Vector2f point, float width, float height) {
		return fromCenteredPointAndDimensions(point.x, point.y, width, height);
	}

	public static BoundingBoxf fromPointAndRadius(float x, float y, float radius) {
		return new BoundingBoxf(x - radius, x + radius, y - radius, y + radius);
	}

	@Override
	public String toString() {
		return "BoundingBoxf [minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY=" + maxY + "]";
	}
}