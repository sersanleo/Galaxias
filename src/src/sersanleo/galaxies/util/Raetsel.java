package src.sersanleo.galaxies.util;

import java.io.IOException;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Solution;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;

public class Raetsel {
	public final static Board createBoardFromRaetsel(int id)
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
					board.add(x, y);
					break;
				case '1':
					board.add(x + 0.5f, y);
					break;
				case '2':
					board.add(x, y + 0.5f);
					break;
				case '3':
					board.add(x + 0.5f, y + 0.5f);
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
}