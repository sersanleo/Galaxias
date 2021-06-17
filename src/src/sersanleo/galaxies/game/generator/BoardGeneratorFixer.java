package src.sersanleo.galaxies.game.generator;

import java.util.Collections;
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
import src.sersanleo.galaxies.game.GalaxyVector;
import src.sersanleo.galaxies.game.solver.Solver;

public class BoardGeneratorFixer {
	protected final BoardGenerator boardGenerator;

	private final Board board;
	private final GeneratorCell[][] cells;

	private SortedSet<Galaxy> concurrent;

	private Set<Galaxy> removed = new HashSet();

	public BoardGeneratorFixer(BoardGenerator boardGenerator, Solver solver) {
		this.boardGenerator = boardGenerator;

		board = boardGenerator.board;
		cells = new GeneratorCell[board.width][board.height];
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = new GeneratorCell(solver.cell(x, y));
	}

	private final void initialize() {
		Map<Galaxy, Set<Galaxy>> concurrent = new HashMap<Galaxy, Set<Galaxy>>();

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

		this.concurrent = new TreeSet<Galaxy>(
				Comparator.comparing(x -> concurrent.get(x).size()).thenComparing(x -> x.hashCode()));
		this.concurrent.addAll(concurrent.keySet());
	}

	protected final void remove(Set<Galaxy> galaxiesToRemove, Galaxy keep) {
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				Set<Galaxy> galaxies = cells[x][y].galaxies;
				if (!Collections.disjoint(galaxiesToRemove, galaxies)) { // Tiene galaxias en común
					galaxiesToRemove.addAll(galaxies);
					galaxiesToRemove.remove(keep);
					galaxies.removeAll(galaxiesToRemove);

					if (galaxies.size() == 0)
						boardGenerator.empty(x, y);
					else if (galaxies.size() == 1)
						boardGenerator.fill(x, y, keep);
					else
						System.err.println("PROBLEMA");
				}
			}

		concurrent.removeAll(galaxiesToRemove);
		boardGenerator.board.removeAll(galaxiesToRemove);
		removed.addAll(galaxiesToRemove);
	}

	private final void iterate() {
		Galaxy galaxy;
		while ((galaxy = pop()) != null) {
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					GeneratorCell cell = cells[x][y];

					if (cell.size() > 1 && cell.contains(galaxy))
						remove(new HashSet<Galaxy>(cell.getGalaxies()), galaxy);
				}
		}
	}

	private final void updateGeneratorGalaxies() {
		boardGenerator.updateGalaxies();
		for (Galaxy g : removed)
			System.out.println(boardGenerator.galaxies.remove((GalaxyVector) g));
	}

	public final void fix() {
		System.err.println("Arreglando...");

		initialize();
		iterate();
		updateGeneratorGalaxies();
	}

	private final Galaxy pop() {
		if (concurrent.size() > 1) {
			Iterator<Galaxy> it = concurrent.iterator();
			Galaxy res = it.next();
			it.remove();
			return res;
		} else
			return null;
	}

	private final String printFormat(int length) {
		StringBuilder format = new StringBuilder();
		for (int i = 0; i < board.width; i++) {
			format.append("%-");
			format.append(length);
			format.append("s ");
		}
		format.append("\n");
		return format.toString();
	}

	private final String printFormat() {
		return printFormat((int) (1 + Math.floor(Math.log(board.getGalaxies().size()))));
	}

	private final void print() {
		String format = printFormat();
		for (int y = 0; y < board.height; y++) {
			String[] data = new String[board.width];
			for (int x = 0; x < board.width; x++)
				data[x] = cells[x][y].getGalaxies().size() == 1 ? cells[x][y].getSolution().a + "" : " ";
			System.out.format(format, data);
		}
		System.out.println();
	}
}
