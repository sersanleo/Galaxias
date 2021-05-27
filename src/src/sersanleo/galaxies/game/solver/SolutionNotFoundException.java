package src.sersanleo.galaxies.game.solver;

class SolutionNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public SolutionNotFoundException(String message) {
		super(message);
	}
}