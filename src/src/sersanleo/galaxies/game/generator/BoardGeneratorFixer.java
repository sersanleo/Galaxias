package src.sersanleo.galaxies.game.generator;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.solver.Solver;

public class BoardGeneratorFixer {
	protected final BoardGenerator generator;

	private final Board board;
	protected final GeneratorCell[][] cells;

	private Map<Galaxy, Set<Galaxy>> graph = new HashMap<Galaxy, Set<Galaxy>>();
	private Map<Galaxy, Integer> areas = new HashMap<Galaxy, Integer>();
	private Map<Galaxy, Integer> pureAreas = new HashMap<Galaxy, Integer>();

	private Set<Galaxy> removed = new HashSet<Galaxy>();

	public BoardGeneratorFixer(BoardGenerator generator, Solver solver) {
		this.generator = generator;

		board = generator.board;
		cells = new GeneratorCell[board.width][board.height];
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = new GeneratorCell(this, solver.cell(x, y));
	}

	private final void initialize() {
		// Creamos grafo
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				Set<Galaxy> cell = cells[x][y];

				if (cell.size() > 1) {
					for (Galaxy galaxy1 : cell) {
						Set<Galaxy> connections;
						if (!graph.containsKey(galaxy1)) {
							connections = new HashSet<Galaxy>();
							graph.put(galaxy1, connections);
						} else
							connections = graph.get(galaxy1);

						for (Galaxy galaxy2 : cell) {
							if (galaxy1 == galaxy2)
								continue;

							connections.add(galaxy2);
						}
					}
				}
			}

		// Obtenemos las propiedades de los vértices de los grafos: área (nº de casillas
		// donde podría estar la galaxia) y área pura (nº de casillas donde es seguro
		// que tiene que estar la galaxia)
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				Set<Galaxy> galaxies = cells[x][y];

				if (galaxies.size() == 1) {
					Galaxy galaxy = cells[x][y].iterator().next();
					if (graph.containsKey(galaxy)) {
						pureAreas.put(galaxy, pureAreas.getOrDefault(galaxy, 0) + 1);
						areas.put(galaxy, areas.getOrDefault(galaxy, 0) + 1);
					}
				} else
					for (Galaxy galaxy : galaxies)
						if (graph.containsKey(galaxy))
							areas.put(galaxy, areas.getOrDefault(galaxy, 0) + 1);
			}
	}

	private final void remove(Galaxy galaxy) {
		System.out.println("Borrando " + galaxy);
		removed.add(galaxy);

		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++) {
				GeneratorCell cell = cells[x][y];
				cell.remove(galaxy);
			}

		generator.board.remove(galaxy);
	}

	private final void iterate() {
		// Buscar todos los ciclos del grafo
		Set<Set<Galaxy>> cycles = new CycleFinder().find();
		for (Set<Galaxy> cycle : cycles)
			if (Collections.disjoint(cycle, removed)) { // Aun no se ha borrado ninguna galaxia del ciclo
				Galaxy galaxy = cycle.stream().filter(x -> pureAreas.get(x) > 1)
						.sorted(Comparator.comparing(x -> -areas.get(x))).findFirst().get();
				remove(galaxy);
			}

	}

	private final void updateGeneratorGalaxies() {
		generator.updateGalaxies();

		// No se deben generar las galaxias recién eliminadas
		generator.galaxies.removeAll(removed);
	}

	public final void fix() {
		initialize();
		iterate();
		updateGeneratorGalaxies();
	}

	private final class CycleFinder {
		private Set<Set<Galaxy>> cycles;

		private Galaxy goal;

		private final Set<Galaxy> find(Galaxy step, Set<Galaxy> path, Set<Galaxy> visited) {
			visited.add(step);
			Set<Galaxy> visitedNow = graph.get(step);

			path.addAll();

			visited.addAll(visitedNow);
			return null;
		}

		private final void find(Galaxy start) {
			find(start, new HashSet<Galaxy>(), new HashSet<Galaxy>());
		}

		protected final Set<Set<Galaxy>> find() {
			cycles = new HashSet<Set<Galaxy>>();

			for (Galaxy node : graph.keySet())
				find(node);

			return cycles;
		}
	}
}
