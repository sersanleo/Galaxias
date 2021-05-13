package src.sersanleo.galaxies.game;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.util.BoundingBoxi;
import src.sersanleo.galaxies.util.ExtFileInputStream;
import src.sersanleo.galaxies.util.ExtFileOutputStream;

public class Board extends BoundingBoxi {
	public final static String FILE_EXT = "tsb";
	public final static int MIN_SIZE = 2;

	public final int width;
	public final int height;
	public final int area;

	private final Set<Galaxy> galaxies = new HashSet<Galaxy>();
	public final Solution solution;

	public Board(int width, int height) throws BoardTooSmallException {
		super(0, width - 1, 0, height - 1);
		if (width < MIN_SIZE || height < MIN_SIZE)
			throw new BoardTooSmallException();

		this.width = width;
		this.height = height;
		this.area = width * height;
		this.solution = new Solution(this);
	}

	public final void addGalaxy(Galaxy galaxy) throws CanNotAddGalaxyException {
		if (overlaps(galaxy)) {
			for (Galaxy g : galaxies)
				if (g.bigBB.overlaps(galaxy))
					throw new CanNotAddGalaxyException(
							"No se puede añadir la galaxia; estaría demasiado cerca de otra.");
		} else
			throw new CanNotAddGalaxyException("No se puede añadir la galaxia; estaría fuera del tablero.");

		galaxies.add(galaxy);
	}

	public final void addGalaxy(float x, float y) throws CanNotAddGalaxyException {
		addGalaxy(new Galaxy(x, y));
	}

	public final boolean removeGalaxy(Galaxy galaxy) {
		return galaxies.remove(galaxy);
	}

	public final Set<Galaxy> getGalaxies() {
		return Collections.unmodifiableSet(galaxies);
	}

	public final void write(ExtFileOutputStream stream) throws IOException {
		stream.writeInt(width);
		stream.writeInt(height);
		stream.writeInt(galaxies.size());

		for (Galaxy galaxy : galaxies) {
			stream.writeFloat(galaxy.x);
			stream.writeFloat(galaxy.y);
		}

		solution.write(stream);
	}

	public final static Board createFromStream(ExtFileInputStream stream)
			throws BoardTooSmallException, IOException, CanNotAddGalaxyException {
		Board board = new Board(stream.readInt(), stream.readInt());

		int galaxiesLeft = stream.readInt();
		while (galaxiesLeft-- > 0)
			board.addGalaxy(stream.readFloat(), stream.readFloat());

		board.solution.set(Solution.createFromStream(board, stream));

		return board;
	}

	public final static Board createFromFile(File file)
			throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		ExtFileInputStream stream = new ExtFileInputStream(file);
		Board board = createFromStream(stream);
		stream.close();
		return board;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((galaxies == null) ? 0 : galaxies.hashCode());
		result = prime * result + height;
		result = prime * result + width;
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
		Board other = (Board) obj;
		if (galaxies == null) {
			if (other.galaxies != null)
				return false;
		} else if (!galaxies.equals(other.galaxies))
			return false;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		return true;
	}
}