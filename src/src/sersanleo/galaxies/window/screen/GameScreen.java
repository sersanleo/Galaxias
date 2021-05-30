package src.sersanleo.galaxies.window.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.AppConfig.AppConfigChangeListener;
import src.sersanleo.galaxies.AppConfig.ConfigParameter;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.Solution.SolutionFoundListener;
import src.sersanleo.galaxies.util.ColorUtil;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;
import src.sersanleo.galaxies.window.component.listener.GameMouseListener;

public class GameScreen extends Screen implements ActionListener, SolutionFoundListener, AppConfigChangeListener {
	private static final long serialVersionUID = 1L;

	private static final int TIMER_UPDATE_RATE = 100; // Cada cuántos milisegundos se actualiza el cronómetro
	private static final int PENALIZATION_EFFECT_LENGTH = 750;

	public final Game game;

	private final BoardView boardView;
	private final GameMouseListener boardViewListener;

	private final JPanel buttonPanel = new JPanel();
	private final JButton saveAsButton = new JButton(icon("saveAs.png"));
	private final JButton saveButton = new JButton(icon("save.png"));
	private final JButton undoButton = new JButton(icon("undo.png"));
	private final JButton redoButton = new JButton(icon("redo.png"));
	private final JButton loadStateButton = new JButton(icon("loadState.png"));
	private final JButton saveStateButton = new JButton(icon("saveState.png"));
	private final JButton nextStepButton = new JButton(icon("nextStep.png"));
	private final JButton checkButton = new JButton(icon("check.png"));
	private final JButton solveButton = new JButton(icon("solve.png"));
	private final JButton fotoButton = new JButton(icon("camera.png")); // DEBUG
	private final JButton editButton = new JButton(icon("edit.png")); // DEBUG

	private final JPanel infoPanel = new JPanel();
	private Long lastPenalization;
	private final JLabel timeLabel = new JLabel();
	private final JLabel movesLabel = new JLabel();

	private final Timer timer;

	public GameScreen(GameWindow window, Game game) {
		super(window);
		this.game = game;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Button panel
		buttonPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		add(buttonPanel);

		saveAsButton.setToolTipText("Guardar partida como...");
		saveAsButton.addActionListener(this);
		buttonPanel.add(saveAsButton);

		saveButton.setToolTipText("Guardar partida");
		saveButton.setEnabled(game.saveFile != null);
		saveButton.addActionListener(this);
		buttonPanel.add(saveButton);

		updateUndoRedoButtons();
		undoButton.setToolTipText("Deshacer");
		undoButton.addActionListener(this);
		buttonPanel.add(undoButton);

		redoButton.setToolTipText("Rehacer");
		redoButton.addActionListener(this);
		buttonPanel.add(redoButton);

		loadStateButton.setToolTipText("Cargar estado guardado");
		updateLoadStateButton();
		loadStateButton.addActionListener(this);
		buttonPanel.add(loadStateButton);

		saveStateButton.setToolTipText("Guardar estado actual");
		saveStateButton.addActionListener(this);
		buttonPanel.add(saveStateButton);

		nextStepButton.setToolTipText("Realizar siguiente movimiento");
		nextStepButton.addActionListener(this);
		nextStepButton.setEnabled(game.board.solution != null);
		buttonPanel.add(nextStepButton);

		checkButton.setToolTipText("Comprobar partida");
		checkButton.addActionListener(this);
		checkButton.setEnabled(game.board.solution != null);
		buttonPanel.add(checkButton);

		solveButton.setToolTipText("Resolver tablero");
		solveButton.addActionListener(this);
		solveButton.setEnabled(game.board.solution != null);
		buttonPanel.add(solveButton);

		if (AppConfig.DEBUG) {
			fotoButton.setToolTipText("Guardar imagen del tablero");
			fotoButton.addActionListener(this);
			buttonPanel.add(fotoButton);

			editButton.setToolTipText("Editar tablero");
			editButton.addActionListener(this);
			buttonPanel.add(editButton);
		}

		// Board
		boardView = new BoardView(game, window.config.getBoardScale());
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		boardViewListener = new GameMouseListener(game, this, boardView);
		boardView.addMouseListener(boardViewListener);
		boardView.addMouseMotionListener(boardViewListener);
		add(boardView);

		// Info
		infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoPanel.setLayout(new GridLayout(0, 2));
		add(infoPanel);

		movesLabel.setHorizontalAlignment(JLabel.CENTER);
		updateMovesLabel();
		infoPanel.add(movesLabel);

		timeLabel.setHorizontalAlignment(JLabel.CENTER);
		updateTimeLabel(0);
		timer = new Timer(TIMER_UPDATE_RATE, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTimeLabel();
			}
		});
		infoPanel.add(timeLabel);

		window.config.addAppConfigChangeListener(this);
		game.solution.addSolutionFoundListener(this);
	}

	public final void updateLoadStateButton() {
		loadStateButton.setEnabled(game.hasSavedState());
	}

	public final void updateUndoRedoButtons() {
		undoButton.setEnabled(game.canUndo());
		redoButton.setEnabled(game.canRedo());
	}

	public final void updateMovesLabel() {
		movesLabel.setText("<html><b>Movimientos: </b>" + game.solution.getMoves());
	}

	public final void updateTimeLabel(boolean penalized, long elapsedSeconds) {
		String color = "000000";
		if (penalized)
			lastPenalization = System.currentTimeMillis();
		if (lastPenalization != null) {
			long offset = System.currentTimeMillis() - lastPenalization;
			if (offset < PENALIZATION_EFFECT_LENGTH) {
				color = Integer.toHexString(ColorUtil
						.interpolate(Color.RED, Color.BLACK, offset / (float) PENALIZATION_EFFECT_LENGTH).getRGB())
						.substring(2);
			} else
				lastPenalization = null;
		}

		int seconds = (int) (elapsedSeconds % 60);
		int minutes = (int) (elapsedSeconds / 60) % 60;
		int hours = (int) (elapsedSeconds / 3600);

		timeLabel.setText("<html><span style='color: " + color + "'><b>Tiempo: </b>" + hours + ":"
				+ String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "</span></html");

		if (game.solution.isSolved() && timer != null && lastPenalization == null)
			timer.stop();
	}

	public final void updateTimeLabel(boolean penalized) {
		updateTimeLabel(penalized, game.elapsedSeconds());
	}

	public final void updateTimeLabel(long elapsedSeconds) {
		updateTimeLabel(false, elapsedSeconds);
	}

	public final void updateTimeLabel() {
		updateTimeLabel(false, game.elapsedSeconds());
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

	private final void saveState() {
		game.saveState();
		updateLoadStateButton();
	}

	private final void loadState() {
		game.loadState();
		updateLoadStateButton();
		updateUndoRedoButtons();
		updateMovesLabel();
		boardView.repaint();
	}

	private final void nextStep() {
		game.nextStep();
		updateUndoRedoButtons();
		updateMovesLabel();
		updateTimeLabel(true);
		boardView.repaint();
	}

	private final void save(File file) {
		try {
			String fileName = file.getName();
			if (!fileName.endsWith("." + Board.FILE_EXT))
				file = new File(file.getParent() + "/" + file.getName() + "." + Board.FILE_EXT);

			if (!file.exists())
				file.createNewFile();

			game.save(file);
			game.saveFile = file;
			saveButton.setEnabled(true);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "No se puede guardar la partida", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private final void save() {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setDialogTitle("Guardar partida");
		FileNameExtensionFilter tsb = new FileNameExtensionFilter("Partida de galaxias (." + Board.FILE_EXT + ")",
				Board.FILE_EXT);
		fileChooser.addChoosableFileFilter(tsb);
		fileChooser.setFileFilter(tsb);
		fileChooser.setSelectedFile(new File(game.board.width + "x" + game.board.height + "." + Board.FILE_EXT));

		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION)
			save(fileChooser.getSelectedFile());
	}

	private final void check() {
		int[] checkResult = game.check();
		updateTimeLabel(true);

		StringBuilder sb = new StringBuilder("Hay ");
		sb.append(checkResult[0]);
		sb.append(" fallo");
		if (checkResult[0] != 1)
			sb.append("s");
		sb.append(" actualmente.\nFalta");
		if (checkResult[1] != 1)
			sb.append("n");
		sb.append(" ");
		sb.append(checkResult[1]);
		sb.append(" arista");
		if (checkResult[1] != 1)
			sb.append("s");
		sb.append(" por colocar.");

		JOptionPane.showMessageDialog(this, sb.toString(), "Estado de la partida", JOptionPane.INFORMATION_MESSAGE);
	}

	private final void solve() {
		if (JOptionPane.showConfirmDialog(this,
				"Si resuelves el tablero la partida acabará sin que puedas conservar tu puntuación. ¿De verdad quieres resolverlo?",
				"¿Resolver?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			game.solution.set(game.board.solution);
			repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == undoButton)
			undo();
		else if (eventSource == redoButton)
			redo();
		else if (eventSource == saveStateButton)
			saveState();
		else if (eventSource == loadStateButton)
			loadState();
		else if (eventSource == saveButton)
			save(game.saveFile);
		else if (eventSource == saveAsButton)
			save();
		else if (eventSource == nextStepButton)
			nextStep();
		else if (eventSource == checkButton)
			check();
		else if (eventSource == solveButton)
			solve();

		if (AppConfig.DEBUG)
			if (eventSource == fotoButton)
				boardView.renderer.save(this);
			else if (eventSource == editButton)
				window.setScreen(new BoardCreatorScreen(window, game.board), true);
	}

	@Override
	public void solutionFound() {
		undoButton.setEnabled(false);
		redoButton.setEnabled(false);
		loadStateButton.setEnabled(false);
		saveStateButton.setEnabled(false);
		nextStepButton.setEnabled(false);
		saveButton.setEnabled(false);
		saveAsButton.setEnabled(false);
		checkButton.setEnabled(false);
		solveButton.setEnabled(false);

		boardView.removeMouseListener(boardViewListener);
		boardView.removeMouseMotionListener(boardViewListener);

		if (!game.solution.isCheat()) { // Resuelto de manera honesta

		}
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
		if (!game.isSaved() && !game.solution.isSolved())
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

	@Override
	public final void added() {
		game.restartTimer();
		timer.start();
	}
}