package src.sersanleo.galaxies.window.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Galaxy;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.game.rendering.BoardRenderer;
import src.sersanleo.galaxies.window.BoardPanel;

public class BoardMouseListener implements MouseListener {
	private final Board board;
	private final BoardPanel panel;

	public BoardMouseListener(Board board, BoardPanel panel) {
		this.board = board;
		this.panel = panel;
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
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			BoardRenderer renderer = panel.renderer;

			float x = e.getX() - renderer.getSelectedEdgeWidth() / 2;
			float y = e.getY() - renderer.getSelectedEdgeWidth() / 2;

			x = (float) (Math.floor((x - 0.25f * renderer.getFullCellSize()) / (0.5f * renderer.getFullCellSize())) * 0.5f);
			y = (float) (Math.floor((y - 0.25f * renderer.getFullCellSize()) / (0.5f * renderer.getFullCellSize())) * 0.5f);

			try {
				Galaxy galaxy = new Galaxy(x, y);
				if (!board.removeGalaxy(galaxy))
					board.addGalaxy(galaxy);
				panel.repaint();
			} catch (CanNotAddGalaxyException exc) {
				JOptionPane.showMessageDialog(panel, exc.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}