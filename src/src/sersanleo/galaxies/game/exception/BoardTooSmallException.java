package src.sersanleo.galaxies.game.exception;

import src.sersanleo.galaxies.game.Board;

public class BoardTooSmallException extends Exception {
	private static final long serialVersionUID = 1L;

	public BoardTooSmallException() {
        super("El tablero debe ser de " + Board.MIN_SIZE + "x" + Board.MIN_SIZE + " o más grande.");
    }
}