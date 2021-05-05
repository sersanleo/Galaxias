package src.sersanleo.galaxies.util;

public class Vector2f {
	public final float x;
	public final float y;

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2f(Vector2i v) {
		this.x = v.x;
		this.y = v.y;
	}

	public Vector2f symmetric(float x, float y) {
		return new Vector2f(2 * this.x - x, 2 * this.y - y);
	}

	public Vector2f symmetric(Vector2f v) {
		return symmetric(v.x, v.y);
	}

	public Vector2f symmetric(Vector2i v) {
		return symmetric(v.x, v.y);
	}

	public Vector2i round() {
		return new Vector2i(Math.round(x), Math.round(y));
	}

	public float distance(Vector2f v) {
		return (float) (Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2));
	}

	public float invDistance(Vector2f v) {
		float distance = distance(v);
		if (distance > 0)
			return 1f / distance;
		else
			return Float.MAX_VALUE;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2f other = (Vector2f) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}