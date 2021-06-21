package src.sersanleo.galaxies.window.screen;

import static src.sersanleo.galaxies.util.SwingUtil.iconButton;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.AppConfig.AppConfigChangeListener;
import src.sersanleo.galaxies.AppConfig.ConfigParameter;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;
import src.sersanleo.galaxies.window.component.listener.BoardMouseListener;

public class BoardCreatorScreen extends Screen implements ActionListener, AppConfigChangeListener {
	private static final long serialVersionUID = 1L;

	public final Board board;

	private final BoardView boardView;

	private final JPanel buttonPanel = new JPanel();
	private final JButton playButton = iconButton("play.png", "Comprobar validez y jugar");
	private final JButton solveButton = iconButton("solve.png", "Abrir resolutor"); // DEBUG

	public BoardCreatorScreen(GameWindow window, Board board) {
		super(window);
		this.board = board;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardView = new BoardView(board, window.config.getBoardScale());
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		boardView.addMouseListener(new BoardMouseListener(board, boardView, window));
		add(boardView);

		// Button panel
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		add(buttonPanel);

		playButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		playButton.addActionListener(this);
		buttonPanel.add(playButton);

		if (AppConfig.DEBUG) {
			solveButton.setBackground(Color.MAGENTA);
			solveButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			solveButton.addActionListener(this);
			buttonPanel.add(solveButton);
		}

		window.config.addAppConfigChangeListener(this);
	}

	private final void play() {
		if (board.getGalaxies().size() <= 1)
			JOptionPane.showMessageDialog(this, "El tablero tiene que tener al menos 2 galaxias.", "Error",
					JOptionPane.ERROR_MESSAGE);
		else {
			if (board.solution == null) {
				Solver solver = new Solver(board, 2);
				solver.solve();
				if (solver.getSolutions() == 1)
					board.solution = solver.getSolution();
				else if (solver.getSolutions() == 0) {
					JOptionPane.showMessageDialog(this, "El tablero no tiene una solución válida.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} else if (solver.getSolutions() > 1) {
					JOptionPane.showMessageDialog(this, "El tablero tiene más de una solución válida.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			Game game = new Game(board);
			GameScreen newContent = new GameScreen(window, game);
			window.setScreen(newContent, true);
		}
	}

	private final void solve() {
		SolverScreen panel = new SolverScreen(window, board);
		window.setScreen(panel, true);
	}

	@Override
	public final void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == playButton)
			play();

		if (AppConfig.DEBUG)
			if (eventSource == solveButton)
				solve();
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
