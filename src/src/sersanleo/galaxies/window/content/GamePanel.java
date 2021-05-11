package src.sersanleo.galaxies.window.content;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.AppConfig.AppConfigChangeListener;
import src.sersanleo.galaxies.AppConfig.ConfigParameter;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.Solution.SolutionFoundListener;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;
import src.sersanleo.galaxies.window.component.listener.GameMouseListener;

public class GamePanel extends AppContent implements ActionListener, SolutionFoundListener, AppConfigChangeListener {
	private static final long serialVersionUID = 1L;
	public final Game game;

	private final BoardView boardView;
	private final JButton undoButton;

	public GamePanel(GameWindow window, Game game) {
		super(window);
		this.game = game;
		game.solution.addSolutionFoundListener(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardView = new BoardView(game, window.config.getBoardScale());
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		GameMouseListener mouseListener = new GameMouseListener(game, this, boardView);
		boardView.addMouseListener(mouseListener);
		boardView.addMouseMotionListener(mouseListener);
		add(boardView);

		undoButton = new JButton("Deshacer");
		undoButton.setToolTipText("Deshacer");
		undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		updateUndoButton();
		undoButton.addActionListener(this);
		add(undoButton);
		
		window.config.addAppConfigChangeListener(this);
	}

	public final void updateUndoButton() {
		undoButton.setEnabled(game.canUndo());
	}

	private final void undo() {
		if (game.undo()) {
			updateUndoButton();
			repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == undoButton)
			undo();
	}

	@Override
	public void solutionFound() {

	}

	@Override
	public void appConfigChange(AppConfig config, ConfigParameter parameter) {
		if (parameter == ConfigParameter.BOARD_SCALE) {
			boardView.setScale(config.getBoardScale());
			window.packAndCenter();
		}
	}

	@Override
	public void release() {
		window.config.removeAppConfigChangeListener(this);
	}
}