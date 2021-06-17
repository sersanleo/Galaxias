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
import src.sersanleo.galaxies.game.solver.Solver;

public class BoardGeneratorFixer {
	protected final BoardGenerator boardGenerator;

	private final Board board;
	private final Set<Galaxy>[][] cells;

	private SortedSet<Galaxy> concurrent;

	private Map<Galaxy, Integer> removed = new HashMap<Galaxy, Integer>();

	@SuppressWarnings("unchecked")
	public BoardGeneratorFixer(BoardGenerator boardGenerator, Solver solver) {
		this.boardGenerator = boardGenerator;

		board = boardGenerator.board;
		cells = new Set[board.width][board.height];
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = new HashSet<Galaxy>(solver.cell(x, y).getGalaxies());
	}

	private final void initialize() {
		Map<Galaxy, Set<Galaxy>> concurrent = new HashMap<Galaxy, Set<Galaxy>>();

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				Set<Galaxy> cell = cells[x][y];

				if (cell.size() > 1) {
					for (Galaxy galaxy1 : cell) {
						Set<Galaxy> set;
						if (!concurrent.containsKey(galaxy1)) {
							set = new HashSet<Galaxy>();
							concurrent.put(galaxy1, set);
						} else
							set = concurrent.get(galaxy1);

						for (Galaxy galaxy2 : cell) {
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

	private final void removed(Galaxy galaxy) {
		removed.put(galaxy, removed.getOrDefault(galaxy, 0) + 1);
	}

	protected final void remove(Set<Galaxy> galaxiesToRemove, Galaxy keep) {
		// Borrar todas las galaxias en galaxiesToRemove y aquellas que aparecen junto a
		// esas mismas galaxias en cualquier casilla

		boolean changed;
		do {
			changed = false;

			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					Set<Galaxy> galaxies = cells[x][y];
					if (!Collections.disjoint(galaxiesToRemove, galaxies)) { // Tiene galaxias en común
						galaxiesToRemove.addAll(galaxies);
						galaxiesToRemove.remove(keep);

						for (Galaxy galaxy : galaxies)
							if (galaxiesToRemove.contains(galaxy))
								removed(galaxy);

						galaxies.removeAll(galaxiesToRemove);

						if (galaxies.size() == 0) // Si la casilla se ha quedado vacía, se vacía en el generador
							boardGenerator.empty(x, y);
						else if (galaxies.size() == 1) // Si la casilla se ha resuelto, se resuelve en el generador
							boardGenerator.fill(x, y, keep);
						else
							System.err.println("[DEBUG] CASO INESPERADO");

						changed = true;
					}
				}
		} while (changed);

		concurrent.removeAll(galaxiesToRemove);
		boardGenerator.board.removeAll(galaxiesToRemove);
	}

	private final void iterate() {
		Galaxy galaxy;
		while ((galaxy = pop()) != null) {
			for (int x = 0; x < board.width; x++)
				for (int y = 0; y < board.height; y++) {
					Set<Galaxy> cell = cells[x][y];

					if (cell.size() > 1 && cell.contains(galaxy))
						remove(new HashSet<Galaxy>(cell), galaxy);
				}
		}
	}

	private final void updateGeneratorGalaxies() {
		boardGenerator.updateGalaxies();

		// Se elimina la posibilidad de que en la siguiente iteración del generador se
		// use una galaxia eliminada (de lo contrario, entraria en bucle)
		Galaxy toRemove = removed.entrySet().stream().sorted(Comparator.comparing(x -> x.getValue()))
				.map(x -> x.getKey()).findFirst().get();

		boardGenerator.galaxies.remove(toRemove);
	}

	public final void fix() {
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
}
