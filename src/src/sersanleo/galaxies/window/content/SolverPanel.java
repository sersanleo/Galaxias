package src.sersanleo.galaxies.window.content;

import java.awt.Component;
import javax.swing.BoxLayout;
import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.AppConfig.AppConfigChangeListener;
import src.sersanleo.galaxies.AppConfig.ConfigParameter;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.rendering.SolverRenderer;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;

public class SolverPanel extends AppContent implements AppConfigChangeListener {
	private static final long serialVersionUID = 1L;

	public final Board board;
	public final Solver solver;

	private final BoardView boardView;

	public SolverPanel(GameWindow window, Board board) {
		super(window);
		this.board = board;
		solver = new Solver(board);
		try {
			solver.solve();
		} catch (Exception e) {
			e.printStackTrace();
		}

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardView = new BoardView(new SolverRenderer(solver, 1));
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(boardView);

		window.config.addAppConfigChangeListener(this);
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
}
