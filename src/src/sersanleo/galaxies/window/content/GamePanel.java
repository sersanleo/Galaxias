package src.sersanleo.galaxies.window.content;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.AppConfig.AppConfigChangeListener;
import src.sersanleo.galaxies.AppConfig.ConfigParameter;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.Solution.SolutionFoundListener;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;
import src.sersanleo.galaxies.window.component.listener.GameMouseListener;

public class GamePanel extends AppContent implements ActionListener, SolutionFoundListener, AppConfigChangeListener {
	private static final long serialVersionUID = 1L;
	public final Game game;

	private final BoardView boardView;
	private final JButton undoButton = new JButton("Deshacer");
	private final JButton saveButton = new JButton("Guardar partida");

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

		undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		updateUndoButton();
		undoButton.addActionListener(this);
		add(undoButton);

		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveButton.addActionListener(this);
		add(saveButton);

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

	private final void save() throws IOException {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setDialogTitle("Guardar partida");
		FileNameExtensionFilter tsb = new FileNameExtensionFilter("Partida de galaxias (." + Board.FILE_EXT + ")",
				Board.FILE_EXT);
		fileChooser.addChoosableFileFilter(tsb);
		fileChooser.setFileFilter(tsb);
		fileChooser.setSelectedFile(new File(game.board.width + "x" + game.board.height + "." + Board.FILE_EXT));

		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			String fileName = file.getName();
			if (!fileName.endsWith("." + Board.FILE_EXT))
				file = new File(file.getParent() + "/" + file.getName() + "." + Board.FILE_EXT);

			if (!file.exists())
				file.createNewFile();

			game.save(file);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == undoButton)
			undo();
		else if (eventSource == saveButton)
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	public boolean canBeRemoved() {
		if (!game.isSaved())
			return JOptionPane.showConfirmDialog(this,
					"Estás a punto de salir sin guardar la partida, ¿desea salir igualmente?", "¿Salir?",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		else
			return true;
	}

	@Override
	public void release() {
		window.config.removeAppConfigChangeListener(this);
	}
}