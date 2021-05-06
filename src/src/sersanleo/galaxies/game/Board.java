package src.sersanleo.galaxies.game;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.util.BoundingBoxi;
import src.sersanleo.galaxies.window.painter.BoardPainter;

public class Board extends BoundingBoxi {
	public final static int MIN_SIZE = 2;

	public final int width;
	public final int height;
	public final int area;

	private final Set<Galaxy> galaxies = new HashSet<Galaxy>();

	public Board(int width, int height) throws BoardTooSmallException {
		super(0, width - 1, 0, height - 1);
		if (width < MIN_SIZE || height < MIN_SIZE)
			throw new BoardTooSmallException();

		this.width = width;
		this.height = height;
		this.area = width * height;
	}

	protected BoardPainter createBoardPainter() {
		return new BoardPainter(this);
	}

	public void addGalaxy(Galaxy galaxy) throws CanNotAddGalaxyException {
		if (overlaps(galaxy)) {
			for (Galaxy g : galaxies)
				if (g.bigBB.overlaps(galaxy))
					throw new CanNotAddGalaxyException(
							"No se puede añadir la galaxia; estaría demasiado cerca de otra.");
		} else
			throw new CanNotAddGalaxyException("No se puede añadir la galaxia; estaría fuera del tablero.");

		galaxies.add(galaxy);
	}

	public void addGalaxy(float x, float y) throws CanNotAddGalaxyException {
		addGalaxy(new Galaxy(x, y));
	}

	public boolean removeGalaxy(Galaxy galaxy) {
		return galaxies.remove(galaxy);
	}

	public Set<Galaxy> getGalaxies() {
		return Collections.unmodifiableSet(galaxies);
	}
}