package src.sersanleo.galaxies.game;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Board {
	public final int width;
	public final int height;

	private final Set<Galaxy> galaxies = new HashSet<Galaxy>();

	public Board(int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected BoardPainter createBoardPainter() {
		return new BoardPainter(this);
	}

	public boolean addGalaxy(Galaxy galaxy) {
		galaxies.add(galaxy);
		return true;
	}

	public boolean addGalaxy(float x, float y) {
		return addGalaxy(new Galaxy(x, y));
	}

	public boolean removeGalaxy(Galaxy galaxy) {
		return galaxies.remove(galaxy);
	}

	public Set<Galaxy> getGalaxies() {
		return Collections.unmodifiableSet(galaxies);
	}
}