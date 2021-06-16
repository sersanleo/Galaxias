package src.sersanleo.galaxies.game;

import src.sersanleo.galaxies.util.Vector2f;

public class GalaxyVector extends Vector2f {
	public GalaxyVector(float x, float y) {
		super(x, y);
	}
	
	public GalaxyVector(Vector2f v) {
		super(v.x, v.y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Math.round(2 * x);
		result = prime * result + Math.round(2 * y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		GalaxyVector other = (GalaxyVector) obj;
		if (Math.round(2 * x) != Math.round(2 * other.x))
			return false;
		if (Math.round(2 * y) != Math.round(2 * other.y))
			return false;
		return true;
	}
}