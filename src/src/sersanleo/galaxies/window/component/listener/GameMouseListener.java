package src.sersanleo.galaxies.window.component.listener;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.Movement;
import src.sersanleo.galaxies.game.Movement.EdgeType;
import src.sersanleo.galaxies.game.rendering.BoardRenderer;
import src.sersanleo.galaxies.window.component.BoardView;
import src.sersanleo.galaxies.window.screen.GameScreen;

public class GameMouseListener implements MouseListener, MouseMotionListener {
	// Umbral para marcar una arista cuando se arrastra el cursor
	public static final float LENGTH_THRESHOLD = 0.25f;
	public static final float CENTER_THRESHOLD = 0.35f;

	private final Game game;
	private final GameScreen gamePanel;
	private final BoardView boardView;

	private final Set<Movement> movements = new HashSet<Movement>();

	public GameMouseListener(Game game, GameScreen gamePanel, BoardView panel) {
		this.game = game;
		this.gamePanel = gamePanel;
		this.boardView = panel;
	}

	private final void switchEdge(int mouseX, int mouseY, boolean softDetection) {
		BoardRenderer renderer = boardView.renderer;

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

				Movement movement = new Movement(edgeX, edgeY, EdgeType.HORIZONTAL);
				if (!movements.contains(movement) && movement.apply(game, false, false)) {
					gamePanel.updateUndoRedoButtons();
					gamePanel.updateMovesLabel();
					movements.add(movement);
					boardView.repaint();
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

				Movement movement = new Movement(edgeX, edgeY, EdgeType.VERTICAL);
				if (!movements.contains(movement) && movement.apply(game, false, false)) {
					gamePanel.updateUndoRedoButtons();
					gamePanel.updateMovesLabel();
					movements.add(movement);
					boardView.repaint();
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
		if ((e.getModifiersEx() & InputEvent.getMaskForButton(MouseEvent.BUTTON1)) != 0)
			switchEdge(e.getX(), e.getY(), true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		movements.clear();
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