package src.sersanleo.galaxies.window.screen;

import static src.sersanleo.galaxies.util.SwingUtil.iconButton;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.AppConfig.AppConfigChangeListener;
import src.sersanleo.galaxies.AppConfig.ConfigParameter;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.rendering.SolverRenderer;
import src.sersanleo.galaxies.game.solver.Solver;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;

public class SolverScreen extends Screen implements AppConfigChangeListener, ActionListener {
	// DEBUG
	private static final long serialVersionUID = 1L;

	public final Solver solver;

	private final BoardView boardView;

	private final JPanel buttonPanel = new JPanel();
	private final JButton fotoButton = iconButton("camera.png", "Guardar imagen del tablero");
	private final JButton editButton = iconButton("edit.png", "Editar tablero");

	public SolverScreen(GameWindow window, Board board) {
		super(window);

		solver = new Solver(board, 2);
		solver.solve();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardView = new BoardView(new SolverRenderer(solver, window.config.getBoardScale()));
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(boardView);

		// Button panel
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		add(buttonPanel);

		fotoButton.setBackground(Color.MAGENTA);
		fotoButton.addActionListener(this);
		buttonPanel.add(fotoButton);

		editButton.setBackground(Color.MAGENTA);
		editButton.addActionListener(this);
		buttonPanel.add(editButton);

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

	@Override
	public void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();
		if (eventSource == editButton)
			window.setScreen(new BoardCreatorScreen(window, solver.board));
		else if (eventSource == fotoButton)
			boardView.renderer.save(this);
	}

	@Override
	public void added() {
		if (solver.getSolutions() == 0)
			window.setStatus("[DEBUG] Tablero sin soluciones.");
		else if (solver.getSolutions() == 1)
			window.setStatus("[DEBUG] Tablero con una única solución.");
		else
			window.setStatus("[DEBUG] Tablero con al menos dos soluciones.");
	}
}
