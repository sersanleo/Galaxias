package src.sersanleo.galaxies.window.content;

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
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;
import src.sersanleo.galaxies.window.component.listener.GameMouseListener;

public class GamePanel extends AppContent implements ActionListener, SolutionFoundListener, AppConfigChangeListener {
	private static final long serialVersionUID = 1L;

	public final Game game;

	private final BoardView boardView;

	private final JPanel buttonPanel = new JPanel();
	private final JButton saveAsButton = new JButton(icon("saveAs"));
	private final JButton saveButton = new JButton(icon("save"));
	private final JButton undoButton = new JButton(icon("undo"));
	private final JButton redoButton = new JButton(icon("redo"));
	private final JButton loadStateButton = new JButton(icon("loadState"));
	private final JButton saveStateButton = new JButton(icon("saveState"));
	private final JButton nextStepButton = new JButton(icon("nextStep"));
	private final JButton checkButton = new JButton(icon("check"));
	private final JButton fotoButton = new JButton("Foto");

	private final JPanel infoPanel = new JPanel();
	private final JLabel timeLabel = new JLabel();
	private final JLabel movesLabel = new JLabel();

	private final Timer timer;

	public GamePanel(GameWindow window, Game game) {
		super(window);
		this.game = game;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// Button panel
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		add(buttonPanel);

		saveAsButton.setToolTipText("Guardar partida como...");
		saveAsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveAsButton.addActionListener(this);
		buttonPanel.add(saveAsButton);

		saveButton.setToolTipText("Guardar partida");
		saveButton.setEnabled(game.saveFile != null);
		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveButton.addActionListener(this);
		buttonPanel.add(saveButton);

		updateUndoRedoButtons();
		undoButton.setToolTipText("Deshacer");
		undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		undoButton.addActionListener(this);
		buttonPanel.add(undoButton);

		redoButton.setToolTipText("Rehacer");
		redoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		redoButton.addActionListener(this);
		buttonPanel.add(redoButton);

		loadStateButton.setToolTipText("Cargar estado guardado");
		loadStateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		updateLoadStateButton();
		loadStateButton.addActionListener(this);
		buttonPanel.add(loadStateButton);

		saveStateButton.setToolTipText("Guardar estado actual");
		saveStateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveStateButton.addActionListener(this);
		buttonPanel.add(saveStateButton);

		nextStepButton.setToolTipText("Realizar siguiente movimiento");
		nextStepButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		nextStepButton.addActionListener(this);
		nextStepButton.setEnabled(game.board.solution != null);
		buttonPanel.add(nextStepButton);

		checkButton.setToolTipText("Comprobar partida");
		checkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkButton.addActionListener(this);
		checkButton.setEnabled(game.board.solution != null);
		buttonPanel.add(checkButton);

		fotoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		fotoButton.addActionListener(this);
		fotoButton.setVisible(false);
		buttonPanel.add(fotoButton);

		// Board
		boardView = new BoardView(game, window.config.getBoardScale());
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		GameMouseListener mouseListener = new GameMouseListener(game, this, boardView);
		boardView.addMouseListener(mouseListener);
		boardView.addMouseMotionListener(mouseListener);
		add(boardView);

		// Info
		infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
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
		long elapsedSeconds = game.elapsedSeconds();
		int seconds = (int) (elapsedSeconds % 60);
		int minutes = (int) (elapsedSeconds / 60) % 60;
		int hours = (int) (elapsedSeconds / 3600);
		timeLabel.setText(
				"Tiempo: " + hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
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
		else if (eventSource == fotoButton)
			boardView.renderer.save();
		else if (eventSource == checkButton)
			check();
	}

	@Override
	public void solutionFound() {
		timer.stop();
		updateTimeLabel();
		undoButton.setEnabled(false);
		redoButton.setEnabled(false);
		loadStateButton.setEnabled(false);
		saveStateButton.setEnabled(false);
		nextStepButton.setEnabled(false);
		saveButton.setEnabled(false);
		saveAsButton.setEnabled(false);
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

	@Override
	public final void added() {
		game.restartTimer();
	}
}