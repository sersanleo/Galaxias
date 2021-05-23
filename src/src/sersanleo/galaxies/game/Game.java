package src.sersanleo.galaxies.game;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;

import src.sersanleo.galaxies.game.Movement.EdgeType;
import src.sersanleo.galaxies.game.Solution.SolutionFoundListener;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.util.ExtFileInputStream;
import src.sersanleo.galaxies.util.ExtFileOutputStream;

public class Game implements SolutionFoundListener {
	private static final int MAX_UNDO = 20;
	private static final long NEXT_STEP_PENALTY = 15;
	private static final long CHECK_PENALTY = 10;

	public final Board board;
	public final Solution solution;
	private Solution savedState;

	private long elapsedSeconds;

	private LinkedList<Movement> undoHistory = new LinkedList<Movement>();
	private LinkedList<Movement> redoHistory = new LinkedList<Movement>();
	private final LocalDateTime start;
	private boolean isSaved = false;

	public Game(Board board, Solution solution, Solution savedState, long elapsedSeconds) {
		this.board = board;
		this.solution = solution;
		this.solution.addSolutionFoundListener(this);
		this.savedState = savedState;

		this.elapsedSeconds = elapsedSeconds;

		this.start = LocalDateTime.now();
	}

	public Game(Board board, Solution solution) {
		this(board, solution, null, 0);
	}

	public Game(Board board) {
		this(board, new Solution(board));
	}

	public final void nextStep() {
		Movement nextStep = solution.getNextStep(board.solution);
		if (nextStep != null) {
			nextStep.apply(this, false, false);
			elapsedSeconds += NEXT_STEP_PENALTY;
		}
	}

	public final int[] check() {
		elapsedSeconds += CHECK_PENALTY;
		return solution.compare(board.solution);
	}

	public final boolean hasSavedState() {
		return savedState != null;
	}

	public final void saveState() {
		savedState = new Solution(board);
		savedState.set(solution);
		isSaved = false;
	}

	public final void loadState() {
		solution.set(savedState);
		undoHistory.clear();
		redoHistory.clear();
		isSaved = false;
	}

	public final boolean isSaved() {
		return isSaved;
	}

	public final long elapsedSeconds(boolean solved) {
		if (solved)
			return elapsedSeconds;
		return elapsedSeconds + Math.max(0, ChronoUnit.SECONDS.between(start, LocalDateTime.now()));
	}

	public final long elapsedSeconds() {
		return elapsedSeconds(solution.isSolved());
	}

	private final void addMovement(Movement movement, boolean redoing) {
		if (!redoing)
			redoHistory.clear();
		while (undoHistory.size() >= MAX_UNDO)
			undoHistory.removeFirst();

		undoHistory.add(movement);
	}

	public final boolean switchHorizontalEdge(int x, int y, boolean undoing, boolean redoing) {
		if (solution.switchHorizontalEdge(x, y, undoing)) {
			isSaved = false;

			Movement movement = new Movement(x, y, EdgeType.HORIZONTAL);
			if (!undoing)
				addMovement(movement, redoing);
			else
				redoHistory.addLast(movement);

			return true;
		}
		return false;
	}

	public final boolean switchVerticalEdge(int x, int y, boolean undoing, boolean redoing) {
		if (solution.switchVerticalEdge(x, y, undoing)) {
			isSaved = false;

			Movement movement = new Movement(x, y, EdgeType.VERTICAL);
			if (!undoing)
				addMovement(movement, redoing);
			else
				redoHistory.addLast(movement);

			return true;
		}
		return false;
	}

	public final boolean canUndo() {
		return undoHistory.size() > 0 && !solution.isSolved();
	}

	public final boolean canRedo() {
		return redoHistory.size() > 0 && !solution.isSolved();
	}

	public final boolean undo() {
		if (canUndo())
			return undoHistory.removeLast().apply(this, true, false);
		return false;
	}

	public final boolean redo() {
		if (canRedo())
			return redoHistory.removeLast().apply(this, false, true);
		return false;
	}

	public final void solve() {
		solution.set(board.solution);
	}

	@Override
	public void solutionFound() {
		elapsedSeconds = elapsedSeconds(false);
	}

	public final void write(ExtFileOutputStream stream) throws IOException {
		board.write(stream);
		solution.write(stream);
		boolean hasSavedState = hasSavedState();
		stream.writeBoolean(hasSavedState);
		if (hasSavedState)
			savedState.write(stream);

		stream.writeLong(elapsedSeconds());
	}

	public final void save(File file) throws IOException {
		ExtFileOutputStream stream = new ExtFileOutputStream(file);

		write(stream);

		stream.flush();
		stream.close();

		isSaved = true;
	}

	public final static Game createFromStream(ExtFileInputStream stream)
			throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		Board board = Board.createFromStream(stream);
		Solution solution = Solution.createFromStream(board, stream);
		Solution savedState = stream.readBoolean() ? Solution.createFromStream(board, stream) : null;

		long elapsedSeconds = stream.readLong();

		return new Game(board, solution, savedState, elapsedSeconds);
	}

	public final static Game createFromFile(File file)
			throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		ExtFileInputStream stream = new ExtFileInputStream(file);
		Game game = createFromStream(stream);
		stream.close();
		return game;
	}
}