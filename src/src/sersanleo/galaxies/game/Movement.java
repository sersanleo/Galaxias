package src.sersanleo.galaxies.game;

public class Movement {
	public final int x;
	public final int y;
	public final EdgeType edge;
	
	public Movement(int x, int y, EdgeType edge) {
		this.x = x;
		this.y = y;
		this.edge = edge;
	}
	
	public final boolean apply(Game game, boolean undoing) {
		if(edge == EdgeType.HORIZONTAL)
			return game.switchHorizontalEdge(x, y, undoing);
		else
			return game.switchVerticalEdge(x, y, undoing);
	}

	public static enum EdgeType {
		HORIZONTAL, VERTICAL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edge == null) ? 0 : edge.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movement other = (Movement) obj;
		if (edge != other.edge)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
