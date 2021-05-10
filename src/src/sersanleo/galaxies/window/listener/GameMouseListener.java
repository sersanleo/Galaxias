package src.sersanleo.galaxies.window.listener;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.rendering.BoardRenderer;
import src.sersanleo.galaxies.util.Vector2i;
import src.sersanleo.galaxies.window.BoardPanel;

public class GameMouseListener implements MouseListener, MouseMotionListener {
	// Umbral para marcar una arista cuando se arrastra el cursor
	private static final float LENGTH_THRESHOLD = 0.25f;
	private static final float CENTER_THRESHOLD = 0.3f;

	private final Game game;
	private final BoardPanel panel;

	private final Set<Vector2i> horizontalEdges = new HashSet<Vector2i>();
	private final Set<Vector2i> verticalEdges = new HashSet<Vector2i>();

	public GameMouseListener(Game game, BoardPanel panel) {
		this.game = game;
		this.panel = panel;
	}

	private final void switchEdge(int mouseX, int mouseY, boolean softDetection) {
		BoardRenderer renderer = panel.renderer;

		float x = (mouseX - renderer.getSelectedEdgeWidth() / 2) / renderer.getFullCellSize();
		float y = (mouseY - renderer.getSelectedEdgeWidth() / 2) / renderer.getFullCellSize();
		int cellX = (int) Math.floor(x);
		int cellY = (int) Math.floor(y);
		float offsetX = x - cellX;
		float offsetY = y - cellY;

		int subtriangle = (offsetX > offsetY ? 0 : 1) * 2 + (1 - offsetX > offsetY ? 0 : 1);
		if (subtriangle == 0 || subtriangle == 3) { // Arista horizontal
			int edgeX = cellX;
			int edgeY = cellY - (subtriangle == 0 ? 1 : 0);

			if (edgeX >= 0 && edgeX < game.board.width && edgeY >= 0 && edgeY < game.board.height - 1) {
				if (softDetection) {
					float lengthThreshold = Math.min(offsetX, 1 - offsetX);
					float centerThreshold = 0.5f - Math.min(offsetY, 1 - offsetY);
					if (lengthThreshold < LENGTH_THRESHOLD || centerThreshold < CENTER_THRESHOLD)
						return;
				}

				Vector2i edge = new Vector2i(edgeX, edgeY);
				if (!horizontalEdges.contains(edge)) {
					game.solution.switchHorizontalEdge(edgeX, edgeY);
					horizontalEdges.add(edge);
					panel.repaint();
				}
			}
		} else { // Arista vertical
			int edgeX = cellX - (subtriangle == 2 ? 1 : 0);
			int edgeY = cellY;

			if (edgeX >= 0 && edgeX < game.board.width - 1 && edgeY >= 0 && edgeY < game.board.height) {
				if (softDetection) {
					float lengthThreshold = Math.min(offsetY, 1 - offsetY);
					float centerThreshold = 0.5f - Math.min(offsetX, 1 - offsetX);
					if (lengthThreshold < LENGTH_THRESHOLD || centerThreshold < CENTER_THRESHOLD)
						return;
				}

				Vector2i edge = new Vector2i(edgeX, edgeY);
				if (!verticalEdges.contains(edge)) {
					game.solution.switchVerticalEdge(edgeX, edgeY);
					verticalEdges.add(edge);
					panel.repaint();
				}
			}
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (game.solution.isSolved())
			return;
		if (e.getButton() == MouseEvent.BUTTON1)
			switchEdge(e.getX(), e.getY(), false);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (game.solution.isSolved())
			return;
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
			switchEdge(e.getX(), e.getY(), true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		horizontalEdges.clear();
		verticalEdges.clear();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}