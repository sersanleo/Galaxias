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
	private static final int MAX_UNDO = 10;

	public final Board board;
	public final Solution solution;

	private int moves;
	private LinkedList<Movement> movements = new LinkedList<Movement>();
	private long elapsedSeconds;
	private final LocalDateTime start;

	private boolean isSaved = true;

	public Game(Board board, Solution solution, int moves, long elapsedSeconds) {
		this.board = board;
		this.solution = solution;
		this.solution.addSolutionFoundListener(this);
		this.elapsedSeconds = elapsedSeconds;
		this.start = LocalDateTime.now();
	}

	public Game(Board board, Solution solution) {
		this(board, solution, 0, 0);
	}

	public Game(Board board) {
		this(board, new Solution(board), 0, 0);
	}

	public final boolean isSaved() {
		return isSaved;
	}

	public final long elapsedSeconds() {
		return elapsedSeconds + Math.max(0, ChronoUnit.SECONDS.between(LocalDateTime.now(), start));
	}

	private final void addMovement(Movement movement) {
		while (movements.size() >= MAX_UNDO)
			movements.removeFirst();

		movements.add(movement);
	}

	public final boolean switchHorizontalEdge(int x, int y, boolean undoing) {
		if (solution.switchHorizontalEdge(x, y)) {
			if (!undoing) {
				moves++;
				isSaved = false;
				addMovement(new Movement(x, y, EdgeType.HORIZONTAL));
			} else {
				moves--;
				isSaved = false;
			}
			return true;
		}
		return false;
	}

	public final boolean switchVerticalEdge(int x, int y, boolean undoing) {
		if (solution.switchVerticalEdge(x, y)) {
			if (!undoing) {
				moves++;
				isSaved = false;
				addMovement(new Movement(x, y, EdgeType.VERTICAL));
			} else {
				moves--;
				isSaved = false;
			}
			return true;
		}
		return false;
	}

	public final boolean canUndo() {
		return movements.size() > 0;
	}

	public final boolean undo() {
		if (canUndo())
			return movements.removeLast().apply(this, true);

		return false;
	}

	public final void solve() {
		solution.set(board.solution);
	}

	@Override
	public void solutionFound() {
		elapsedSeconds = elapsedSeconds();
	}

	public final void save(File file) throws IOException {
		ExtFileOutputStream stream = new ExtFileOutputStream(file);

		board.write(stream);
		solution.write(stream);

		stream.writeInt(moves);

		if (solution.isSolved())
			stream.writeLong(elapsedSeconds);
		else
			stream.writeLong(elapsedSeconds());

		stream.flush();
		stream.close();
		
		isSaved = true;
	}

	public final static Game createFromFile(File file)
			throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		ExtFileInputStream stream = new ExtFileInputStream(file);

		Board board = Board.createFromStream(stream);
		Solution solution = Solution.createFromStream(board, stream);

		int moves = stream.readInt();
		long elapsedSeconds = stream.readLong();

		stream.close();
		return new Game(board, solution, moves, elapsedSeconds);
	}
}