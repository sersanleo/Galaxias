package src.sersanleo.galaxies.game.generator;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.rendering.SolverRenderer;
import src.sersanleo.galaxies.game.solver.Solver;

public class BoardGeneratorFixer {
	protected final BoardGenerator generator;

	private final Board board;
	protected final Set<Galaxy>[][] cells;

	private Map<Galaxy, Set<Galaxy>> graph = new HashMap<Galaxy, Set<Galaxy>>();
	private Map<Galaxy, Integer> areas = new HashMap<Galaxy, Integer>();

	private Set<Galaxy> removed = new HashSet<Galaxy>();

	@SuppressWarnings("unchecked")
	public BoardGeneratorFixer(BoardGenerator generator, Solver solver) {
		this.generator = generator;

		board = generator.board;
		cells = new Set[board.width][board.height];
		for (int x = 0; x < board.width; x++)
			for (int y = 0; y < board.height; y++)
				cells[x][y] = new HashSet<Galaxy>(solver.cell(x, y).getGalaxies());
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
				Galaxy galaxy = generator.rows[x][y];
				areas.put(galaxy, areas.getOrDefault(galaxy, 0) + 1);
			}
	}

	private final void remove(Galaxy galaxy) {
		generator.remove(galaxy);
		removed.add(galaxy);
	}

	private final void iterate() {
		// Buscar todos los ciclos del grafo
		Set<Set<Galaxy>> cycles = new CycleFinder().find();
		for (Set<Galaxy> cycle : cycles)
			if (Collections.disjoint(cycle, removed)) { // Aun no se ha borrado ninguna galaxia del ciclo
				Optional<Galaxy> galaxy = cycle.stream().filter(x -> areas.get(x) > 1)
						.sorted(Comparator.comparing(x -> areas.get(x))).findFirst();

				if (galaxy.isPresent()) {
					remove(galaxy.get());
				} else {
					Iterator<Galaxy> it = cycle.iterator();
					int n = 2;
					while (n-- > 0)
						remove(it.next());
				}
			}

	}

	private final void updateGeneratorGalaxies() {
		generator.updateGalaxies();

		// No se deben generar las galaxias recién eliminadas
		generator.galaxies.removeAll(removed);
	}

	private final String toHex(Color color) {
		return String.format("#%06X", (0xFFFFFF & color.getRGB()));
	}

	public final void printGraph() {
		SolverRenderer renderer = new SolverRenderer(new Solver(board, 2));
		for (Galaxy from : graph.keySet()) {
			for (Galaxy to : graph.get(from))
				System.out.println(from.a + " -> " + to.a + ";");
			Color color = renderer.getColorByGalaxy(from);
			System.out.println(from.a + " [label=\"\", shape=circle, color=\"" + toHex(color) + "\", fillcolor=\""
					+ toHex(color) + "\", style=filled];");
		}
	}

	public final void fix() {
		initialize();
		iterate();
		updateGeneratorGalaxies();
	}

	private final class CycleFinder {
		private Set<Set<Galaxy>> cycles;

		private Galaxy goal;

		private final void addCycle(Set<Galaxy> path) {
			for (Set<Galaxy> cycle : cycles)
				if (!Collections.disjoint(cycle, path)) {
					cycle.addAll(path);
					return;
				}

			cycles.add(path);
		}

		private final void find(AbstractMap.Entry<Galaxy, Galaxy> step, Set<Galaxy> path,
				Map<Galaxy, Set<Galaxy>> visited) {
			if (visited.containsKey(step.getKey()) && visited.get(step.getKey()).contains(step.getValue()))
				return; // Ya visitado; no hacemos nada

			if (step.getValue() == goal)
				addCycle(path);
			else {
				path.add(step.getValue());

				Set<Galaxy> visitedSet;
				if (visited.containsKey(step.getKey()))
					visitedSet = visited.get(step.getKey());
				else {
					visitedSet = new HashSet<Galaxy>();
					visited.put(step.getKey(), visitedSet);
				}

				visitedSet.add(step.getValue());

				for (Galaxy neighbor : graph.get(step.getValue()))
					find(new AbstractMap.SimpleEntry<Galaxy, Galaxy>(step.getValue(), neighbor), path,
							new HashMap<Galaxy, Set<Galaxy>>(visited));
			}
		}

		private final void find(Galaxy step, Set<Galaxy> path, Map<Galaxy, Set<Galaxy>> visited) {
			path.add(step);

			for (Galaxy neighbor : graph.get(step))
				find(new AbstractMap.SimpleEntry<Galaxy, Galaxy>(step, neighbor), path,
						new HashMap<Galaxy, Set<Galaxy>>(visited));
		}

		private final void find(Galaxy start) {
			goal = start;
			find(start, new HashSet<Galaxy>(), new HashMap<Galaxy, Set<Galaxy>>());
		}

		protected final Set<Set<Galaxy>> find() {
			cycles = new HashSet<Set<Galaxy>>();

			for (Galaxy node : graph.keySet())
				find(node);

			return cycles;
		}
	}
}
