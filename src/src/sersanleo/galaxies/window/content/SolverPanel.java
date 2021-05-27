package src.sersanleo.galaxies.window.content;

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

public class SolverPanel extends AppContent implements AppConfigChangeListener, ActionListener {
	private static final long serialVersionUID = 1L;

	public final Solver solver;

	private final BoardView boardView;

	private final JPanel buttonPanel = new JPanel();
	private final JButton fotoButton = new JButton(icon("camera.png"));
	private final JButton editButton = new JButton(icon("edit.png"));

	public SolverPanel(GameWindow window, Board board) {
		super(window);

		solver = new Solver(board);
		try {
			solver.solve();
		} catch (Exception e) {
			e.printStackTrace();
		}

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardView = new BoardView(new SolverRenderer(solver, window.config.getBoardScale()));
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(boardView);

		// Button panel
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		add(buttonPanel);

		fotoButton.setToolTipText("Comprobar validez y jugar");
		fotoButton.addActionListener(this);
		buttonPanel.add(fotoButton);

		editButton.setToolTipText("Editar tablero");
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
			window.setContent(new BoardCreatorPanel(window, solver.board));
		else if (eventSource == fotoButton)
			boardView.renderer.save(this);
	}

	@Override
	public void added() {
		window.setStatus("Vista del resolutor (modo desarrollador)"); // TODO: debería decir si se ha resuelto
	}
}
