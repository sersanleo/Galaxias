package src.sersanleo.galaxies.util;

import java.util.Collection;

public class Vector2i implements Comparable<Vector2i> {
	public int x;
	public int y;

	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2i(Vector2i v) {
		this(v.x, v.y);
	}

	public final static Vector2f mean(Collection<Vector2i> vectors) {
		float x = 0;
		float y = 0;

		for (Vector2i vector : vectors) {
			x += vector.x;
			y += vector.y;
		}

		return new Vector2f(x / vectors.size(), y / vectors.size());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2i other = (Vector2i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	@Override
	public int compareTo(Vector2i v) {
		Integer p1 = x * y;
		Integer p2 = v.x * v.y;
		return p1.compareTo(p2);
	}
}