package src.sersanleo.galaxies.window.content;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.AppConfig.AppConfigChangeListener;
import src.sersanleo.galaxies.AppConfig.ConfigParameter;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.Solution;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;
import src.sersanleo.galaxies.window.component.listener.BoardMouseListener;

public class BoardCreatorPanel extends AppContent implements ActionListener, AppConfigChangeListener {
	private static final long serialVersionUID = 1L;

	public final Board board;

	private final BoardView boardView;
	private final JButton playButton = new JButton(icon("play.png"));

	public BoardCreatorPanel(GameWindow window, Board board) {
		super(window);
		this.board = board;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardView = new BoardView(board, window.config.getBoardScale());
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		boardView.addMouseListener(new BoardMouseListener(board, boardView, window));
		add(boardView);

		playButton.setToolTipText("Comprobar validez y jugar");
		playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		playButton.addActionListener(this);
		add(playButton);

		window.config.addAppConfigChangeListener(this);
	}

	private final void play() {
		// TODO: Usar solucionador para ver si se puede empezar o no

		if (board.getGalaxies().size() == 0)
			window.setStatus("El tablero está vacío.");
		else {
			Game game = new Game(board, new Solution(board));
			GamePanel newContent = new GamePanel(window, game);
			window.setContent(newContent, true);
		}
	}

	@Override
	public final void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == playButton)
			play();
	}

	@Override
	public final void appConfigChange(AppConfig config, ConfigParameter parameter) {
		if (parameter == ConfigParameter.BOARD_SCALE) {
			boardView.setScale(config.getBoardScale());
			window.pack();
		}
	}

	@Override
	public final void release() {
		window.config.removeAppConfigChangeListener(this);
	}

	@Override
	public final boolean canBeRemoved() {
		return JOptionPane.showConfirmDialog(this,
				"Estás a punto de salir sin guardar el tablero, ¿desea salir igualmente?", "¿Salir?",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	@Override
	public final void added() {
		window.setStatus("Añada galaxias sobre la cuadrícula.");
	}
}
