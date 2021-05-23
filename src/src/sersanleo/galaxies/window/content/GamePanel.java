package src.sersanleo.galaxies.window.content;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
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

	private static final DateTimeFormatter ELAPSED_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	public final Game game;

	private final BoardView boardView;
	private final JButton undoButton = new JButton("Deshacer");
	private final JButton redoButton = new JButton("Rehacer");
	private final JButton saveStateButton = new JButton("Guardar estado");
	private final JButton loadStateButton = new JButton("Cargar estado");
	private final JButton saveButton = new JButton("Guardar partida");
	private final JButton checkButton = new JButton("Comprobar partida");
	private final JButton nextStepButton = new JButton("Siguiente paso");
	private final JButton fotoButton = new JButton("Foto");

	private final JPanel infoPanel = new JPanel();
	private final JLabel timeLabel = new JLabel();
	private final JLabel movesLabel = new JLabel();

	private final Timer timer;

	public GamePanel(GameWindow window, Game game) {
		super(window);
		this.game = game;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardView = new BoardView(game, window.config.getBoardScale());
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		GameMouseListener mouseListener = new GameMouseListener(game, this, boardView);
		boardView.addMouseListener(mouseListener);
		boardView.addMouseMotionListener(mouseListener);
		add(boardView);

		updateUndoRedoButtons();
		undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		undoButton.addActionListener(this);
		add(undoButton);

		redoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		redoButton.addActionListener(this);
		add(redoButton);

		saveStateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveStateButton.addActionListener(this);
		add(saveStateButton);

		updateLoadStateButton();
		loadStateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		loadStateButton.addActionListener(this);
		add(loadStateButton);

		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveButton.addActionListener(this);
		add(saveButton);

		checkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkButton.addActionListener(this);
		checkButton.setEnabled(game.board.solution != null);
		add(checkButton);

		nextStepButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		nextStepButton.addActionListener(this);
		nextStepButton.setEnabled(game.board.solution != null);
		add(nextStepButton);

		fotoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		fotoButton.addActionListener(this);
		fotoButton.setVisible(false);
		add(fotoButton);

		// Info
		infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoPanel.setLayout(new GridLayout(0, 2));
		add(infoPanel);

		updateMovesLabel();
		infoPanel.add(movesLabel);

		updateTimeLabel();
		timer = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTimeLabel();
			}
		});
		infoPanel.add(timeLabel);

		window.config.addAppConfigChangeListener(this);
		game.solution.addSolutionFoundListener(this);

		timer.start();
	}

	public final void updateLoadStateButton() {
		loadStateButton.setEnabled(game.hasSavedState());
	}

	public final void updateUndoRedoButtons() {
		undoButton.setEnabled(game.canUndo());
		redoButton.setEnabled(game.canRedo());
	}

	public final void updateMovesLabel() {
		movesLabel.setText("Movimientos: " + game.solution.getMoves());
	}

	public final void updateTimeLabel() {
		LocalTime time = LocalTime.ofSecondOfDay(game.elapsedSeconds());
		timeLabel.setText("Tiempo: " + time.format(ELAPSED_TIME_FORMATTER));
	}

	private final void undo() {
		if (game.undo()) {
			updateUndoRedoButtons();
			updateMovesLabel();
			repaint();
		}
	}

	private final void redo() {
		if (game.redo()) {
			updateUndoRedoButtons();
			updateMovesLabel();
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
		else if (eventSource == redoButton)
			redo();
		else if (eventSource == saveStateButton) {
			game.saveState();
			updateLoadStateButton();
		} else if (eventSource == loadStateButton) {
			game.loadState();
			updateLoadStateButton();
			updateUndoRedoButtons();
			updateMovesLabel();
			boardView.repaint();
		} else if (eventSource == saveButton)
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		else if (eventSource == nextStepButton) {
			game.nextStep();
			boardView.repaint();
		} else if (eventSource == fotoButton) {
			boardView.renderer.save();
		} else if (eventSource == checkButton) {
			int[] checkResult = game.check();
			JOptionPane.showMessageDialog(this, "Hay " + checkResult[0] + " fallos actualmente.\nFaltan "
					+ checkResult[1] + " aristas por colocar.");
		}
	}

	@Override
	public void solutionFound() {
		timer.stop();
		updateTimeLabel();
		undoButton.setEnabled(false);
		loadStateButton.setEnabled(false);
		saveStateButton.setEnabled(false);
		nextStepButton.setEnabled(false);
		saveButton.setEnabled(false);
		checkButton.setEnabled(false);
	}

	@Override
	public void appConfigChange(AppConfig config, ConfigParameter parameter) {
		if (parameter == ConfigParameter.BOARD_SCALE) {
			boardView.setScale(config.getBoardScale());
			window.pack();
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
		timer.stop();
	}
}