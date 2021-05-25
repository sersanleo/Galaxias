package src.sersanleo.galaxies.window.component.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.game.rendering.BoardRenderer;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;

public class BoardMouseListener implements MouseListener {
	private final Board board;
	private final BoardView panel;
	private final GameWindow window;

	public BoardMouseListener(Board board, BoardView panel, GameWindow window) {
		this.board = board;
		this.panel = panel;
		this.window = window;
	}

	private final float transformCoord(float coord) {
		float fullCellSize = panel.renderer.getFullCellSize();
		return (float) (Math.floor((4 * coord - fullCellSize) / (2 * fullCellSize)) * 0.5f);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			BoardRenderer renderer = panel.renderer;

			float x = e.getX() - renderer.getSelectedEdgeWidth() / 2;
			float y = e.getY() - renderer.getSelectedEdgeWidth() / 2;

			x = transformCoord(x);
			y = transformCoord(y);

			try {
				Galaxy galaxy = new Galaxy(x, y);
				if (!board.removeGalaxy(galaxy))
					board.addGalaxy(galaxy);
				panel.repaint();
				window.resetStatus();
			} catch (CanNotAddGalaxyException exc) {
				window.setStatus(exc.getLocalizedMessage());
			}
		}
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
	public void mouseReleased(MouseEvent e) {
	}
}