package src.sersanleo.galaxies.window;

import javax.swing.JPanel;

import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.window.listener.GameMouseListener;

public class GamePanel extends JPanel {
	public final Game game;

	private final BoardPanel boardPanel;

	public GamePanel(Game game) {
		this.game = game;
		
		boardPanel = new BoardPanel(game);
		GameMouseListener mouseListener = new GameMouseListener(game, boardPanel);
		boardPanel.addMouseListener(mouseListener);
		boardPanel.addMouseMotionListener(mouseListener);
		add(boardPanel);
	}

}
