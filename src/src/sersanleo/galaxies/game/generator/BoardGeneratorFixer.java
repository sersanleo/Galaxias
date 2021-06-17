package src.sersanleo.galaxies.game.generator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.util.Vector2i;

public class BoardGeneratorFixer {
	private final BoardGenerator boardGenerator;

	private final Board board;
	private final GeneratorCell[][] cells;

	private final Map<Galaxy, Set<Galaxy>> concurrent = new HashMap<Galaxy, Set<Galaxy>>();
	private final SortedSet<Galaxy> orderedConcurrent = new TreeSet<Galaxy>(
			Comparator.comparing(x -> concurrent.get(x).size()).thenComparing(x -> x.hashCode()));

	public BoardGeneratorFixer(BoardGenerator boardGenerator, Solver solver) {
		this.boardGenerator = boardGenerator;

		board = boardGenerator.board;
		cells = new GeneratorCell[board.width][board.height];
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = new GeneratorCell(this, solver.cell(x, y));
	}

	private final void initialize() {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				GeneratorCell cell = cells[x][y];

				if (cell.size() > 1) {
					for (Galaxy galaxy1 : cell.getGalaxies()) {
						Set<Galaxy> set;
						if (!concurrent.containsKey(galaxy1)) {
							set = new HashSet<Galaxy>();
							concurrent.put(galaxy1, set);
						} else
							set = concurrent.get(galaxy1);

						for (Galaxy galaxy2 : cell.getGalaxies()) {
							if (galaxy1 == galaxy2)
								continue;
							set.add(galaxy2);
						}
					}
				}
			}

		orderedConcurrent.addAll(concurrent.keySet());
	}

	private final void iterate() {
		Galaxy galaxy;
		while ((galaxy = pop()) != null) {
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					GeneratorCell cell = cells[x][y];

					if (cell.size() > 1 && cell.contains(galaxy)) {
						cell.solve(galaxy);
					}
				}
		}
	}

	public final void fix() {
		System.err.println("Arreglando...");

		initialize();
		iterate();

		System.out.println(concurrent);
	}

	private final Galaxy pop() {
		if (orderedConcurrent.size() > 1) {
			Iterator<Galaxy> it = orderedConcurrent.iterator();
			Galaxy res = it.next();
			it.remove();
			return res;
		} else
			return null;
	}

	protected final GeneratorCell cell(Vector2i v) {
		return cells[v.x][v.y];
	}
}
