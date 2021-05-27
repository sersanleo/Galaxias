package src.sersanleo.galaxies.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.util.BoundingBoxi;
import src.sersanleo.galaxies.util.ExtFileInputStream;
import src.sersanleo.galaxies.util.ExtFileOutputStream;
import src.sersanleo.galaxies.util.WebUtil;

public class Board extends BoundingBoxi {
	public final static String FILE_EXT = "tsb";
	public final static int MIN_SIZE = 2;

	public final int width;
	public final int height;
	public final int area;

	private final List<Galaxy> galaxies = new ArrayList<Galaxy>();
	public Solution solution;

	public Board(int width, int height) throws BoardTooSmallException {
		super(0, width - 1, 0, height - 1);
		if (width < MIN_SIZE || height < MIN_SIZE)
			throw new BoardTooSmallException();

		this.width = width;
		this.height = height;
		this.area = width * height;
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
		solution = null;
	}

	public final void addGalaxy(float x, float y) throws CanNotAddGalaxyException {
		addGalaxy(new Galaxy(x, y));
	}

	public final boolean removeGalaxy(Galaxy galaxy) {
		if (galaxies.remove(galaxy)) {
			solution = null;
			return true;
		}
		return false;
	}

	public final int getGalaxyId(Galaxy galaxy) {
		return galaxies.indexOf(galaxy);
	}

	public final List<Galaxy> getGalaxies() {
		return Collections.unmodifiableList(galaxies);
	}

	public final void write(ExtFileOutputStream stream) throws IOException {
		stream.writeInt(width);
		stream.writeInt(height);
		stream.writeInt(galaxies.size());

		for (Galaxy galaxy : galaxies) {
			stream.writeFloat(galaxy.x);
			stream.writeFloat(galaxy.y);
		}

		stream.writeBoolean(solution != null);
		if (solution != null)
			solution.write(stream);
	}

	public final void save(File file) throws IOException {
		ExtFileOutputStream stream = new ExtFileOutputStream(file);

		write(stream);

		stream.flush();
		stream.close();
	}

	public final static Board createFromStream(ExtFileInputStream stream)
			throws BoardTooSmallException, IOException, CanNotAddGalaxyException {
		Board board = new Board(stream.readInt(), stream.readInt());

		int galaxiesLeft = stream.readInt();
		while (galaxiesLeft-- > 0)
			board.addGalaxy(stream.readFloat(), stream.readFloat());

		if (stream.readBoolean())
			board.solution = Solution.createFromStream(board, stream);

		return board;
	}

	public final static Board createFromFile(File file)
			throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		ExtFileInputStream stream = new ExtFileInputStream(file);

		Board board = createFromStream(stream);

		stream.close();
		return board;
	}

	public final static Board createFromRaetsel(int id)
			throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		String codigo = WebUtil
				.getURLSource("https://www.janko.at/Raetsel/Galaxien/" + String.format("%03d", id) + ".a.htm");
		String map = codigo.split("problem\r\n")[1].split("\r\nsolution")[0].trim();

		String[] sp = map.split("\r\n");

		Board board = new Board((int) Math.ceil(sp[0].length() / 2f), sp.length);
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				char ch = sp[y].charAt(2 * x);
				switch (ch) {
				case '-':
					break;
				case '0':
					board.addGalaxy(x, y);
					break;
				case '1':
					board.addGalaxy(x + 0.5f, y);
					break;
				case '2':
					board.addGalaxy(x, y + 0.5f);
					break;
				case '3':
					board.addGalaxy(x + 0.5f, y + 0.5f);
					break;
				}
			}

		String mapi = codigo.split("solution\r\n")[1].split("\r\nmoves")[0].trim();
		String[] spi = mapi.split("\r\n");

		boolean[][] horizontalEdges = new boolean[board.width][board.height - 1];
		boolean[][] verticalEdges = new boolean[board.width - 1][board.height];

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height - 1; y++)
				if (!spi[y].split(" ")[x].equals(spi[y + 1].split(" ")[x]))
					horizontalEdges[x][y] = true;

		for (int x = 0; x < board.width - 1; x++)
			for (int y = 0; y < board.height; y++)
				if (!spi[y].split(" ")[x].equals(spi[y].split(" ")[x + 1]))
					verticalEdges[x][y] = true;

		board.solution = new Solution(board, horizontalEdges, verticalEdges);

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