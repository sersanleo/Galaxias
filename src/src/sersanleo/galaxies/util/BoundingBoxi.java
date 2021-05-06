package src.sersanleo.galaxies.util;

public class BoundingBoxi extends BoundingBox {
	public final int minX;
	public final int maxX;
	public final int minY;
	public final int maxY;

	public BoundingBoxi(int minX, int maxX, int minY, int maxY) {
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

	public static BoundingBoxi fromPointAndDimensions(int x, int y, int width, int height) {
		return new BoundingBoxi(x, x + width, y, y + height);
	}

	@Override
	public String toString() {
		return "BoundingBoxi [minX=" + minX + ", maxX=" + maxX + ", minY=" + minY + ", maxY=" + maxY + "]";
	}
}