package src.sersanleo.galaxies.window;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.util.Vector2f;
import src.sersanleo.galaxies.util.Vector2i;
import src.sersanleo.galaxies.window.painter.BoardPainter;

public class GameMouseListener implements MouseListener, MouseMotionListener {
	private static final float A = 0.2f;

	private final Game game;
	private final BoardPanel panel;

	private final Set<Vector2i> horizontalEdges = new HashSet<Vector2i>();
	private final Set<Vector2i> verticalEdges = new HashSet<Vector2i>();

	public GameMouseListener(Game game, BoardPanel panel) {
		this.game = game;
		this.panel = panel;
	}

	private final void switchEdge(int mouseX, int mouseY, boolean softDetection) {
		float x = mouseX - BoardPainter.SELECTED_EDGE_WIDTH_ADD;
		float y = mouseY - BoardPainter.SELECTED_EDGE_WIDTH_ADD - BoardPainter.FULL_CELL_SIZE / 2f;

		float hipotenusa = (float) Math.sqrt(2 * Math.pow(BoardPainter.FULL_CELL_SIZE, 2)) / 2;
		// Se rota, se normaliza y se redondea
		Vector2f trans = new Vector2f(x, y).rotate(Math.PI / 4).scale(1 / hipotenusa);
		Vector2i rotated = trans.round();

		boolean vertical = Math.abs(rotated.x + rotated.y) % 2 == 0;
		if (vertical) {
			int cy = (rotated.y - rotated.x) / 2;
			int cx = rotated.x + cy - 1;

			if (cx >= 0 && cx < game.board.width - 1 && cy >= 0 && cy < game.board.height) {
				if (softDetection) {
					float offset = Math.abs(0.5f + (trans.y - trans.x) / 2);
					offset -= Math.floor(offset);
					if (offset > 0.5f)
						offset = 1 - offset;
					if (offset < A)
						return;
				}

				Vector2i edge = new Vector2i(cx, cy);
				if (!verticalEdges.contains(edge)) {
					game.switchVerticalEdge(cx, cy);
					panel.repaint();
					verticalEdges.add(edge);
				}
			}
		} else {
			int cy = (rotated.y - rotated.x - 1) / 2;
			int cx = rotated.x + cy;

			if (cx >= 0 && cx < game.board.width && cy >= 0 && cy < game.board.height - 1) {
				if (softDetection) {
					float offset = Math.abs(0.5f + trans.x + cy);
					offset -= Math.floor(offset);
					if (offset > 0.5f)
						offset = 1 - offset;
					if (offset < A)
						return;
				}

				Vector2i edge = new Vector2i(cx, cy);
				if (!horizontalEdges.contains(edge)) {
					game.switchHorizontalEdge(cx, cy);
					panel.repaint();
					horizontalEdges.add(edge);
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			switchEdge(e.getX(), e.getY(), false);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			switchEdge(e.getX(), e.getY(), true);
		}
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