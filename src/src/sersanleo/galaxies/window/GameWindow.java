package src.sersanleo.galaxies.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.game.generator.GeneratorThread;
import src.sersanleo.galaxies.util.Raetsel;
import src.sersanleo.galaxies.window.screen.BoardCreatorScreen;
import src.sersanleo.galaxies.window.screen.GameScreen;
import src.sersanleo.galaxies.window.screen.Screen;
import src.sersanleo.galaxies.window.screen.SolverScreen;

public class GameWindow extends JFrame implements ActionListener, WindowListener {
	private static final long serialVersionUID = 1L;

	private static final int MIN_WIDTH = 400;
	private static final int MIN_HEIGHT = 400;

	private final static int MIN_BOARD_SIZE = 4;
	private final static int MAX_BOARD_SIZE = 18;
	private final static int DEFAULT_BOARD_WIDTH = 18;
	private final static int DEFAULT_BOARD_HEIGHT = 16;

	public final AppConfig config;

	// Menú
	private final JMenuBar menuBar;

	private final JMenu gameMenu = new JMenu("Juego");

	private final JMenu newGameMenuItem = new JMenu("Nuevo");
	private final JMenuItem generateBoardMenuItem = new JMenuItem("Generar tablero");
	private final JMenuItem createBoardMenuItem = new JMenuItem("Crear tablero");
	private final JMenuItem raetselMenuItem = new JMenuItem("De raetsel"); // DEBUG
	private final JMenuItem loadGameMenuItem = new JMenuItem("Cargar partida");

	private final JMenu viewMenu = new JMenu("Vista");
	private final ButtonGroup scaleButtonGroup = new ButtonGroup();
	private final JMenuItem scale0_5MenuItem = new JRadioButtonMenuItem("50%");
	private final JMenuItem scale0_75MenuItem = new JRadioButtonMenuItem("75%");
	private final JMenuItem scale1_0MenuItem = new JRadioButtonMenuItem("100%");
	private final JMenuItem scale1_25MenuItem = new JRadioButtonMenuItem("125%");
	private final JMenuItem scale1_5MenuItem = new JRadioButtonMenuItem("150%");

	private final JMenu helpMenu = new JMenu("Ayuda");
	private final JMenuItem infoMenuItem = new JMenuItem("Información sobre Galaxias");

	// Barra de estado
	private final JPanel statusBar;
	private final JLabel status;

	// Pantalla
	private Screen screen;

	public GameWindow() {
		super("Galaxias");

		this.config = new AppConfig();

		// Intenta establecer el Look & Feel del sistema
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setLayout(new BorderLayout());

		// Menú
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// Juego
		menuBar.add(gameMenu);

		gameMenu.add(newGameMenuItem);

		generateBoardMenuItem.addActionListener(this);
		newGameMenuItem.add(generateBoardMenuItem);

		createBoardMenuItem.addActionListener(this);
		newGameMenuItem.add(createBoardMenuItem);

		if (AppConfig.DEBUG) {
			raetselMenuItem.addActionListener(this);
			newGameMenuItem.add(raetselMenuItem);
		}

		loadGameMenuItem.addActionListener(this);
		gameMenu.add(loadGameMenuItem);

		// Vista
		menuBar.add(viewMenu);
		scaleButtonGroup.add(scale0_5MenuItem);
		scaleButtonGroup.add(scale0_75MenuItem);
		scaleButtonGroup.add(scale1_0MenuItem);
		scaleButtonGroup.add(scale1_25MenuItem);
		scaleButtonGroup.add(scale1_5MenuItem);
		scaleButtonGroup.setSelected(scale1_0MenuItem.getModel(), true);

		viewMenu.add(scale0_5MenuItem);
		viewMenu.add(scale0_75MenuItem);
		viewMenu.add(scale1_0MenuItem);
		viewMenu.add(scale1_25MenuItem);
		viewMenu.add(scale1_5MenuItem);

		scale0_5MenuItem.addActionListener(this);
		scale0_75MenuItem.addActionListener(this);
		scale1_0MenuItem.addActionListener(this);
		scale1_25MenuItem.addActionListener(this);
		scale1_5MenuItem.addActionListener(this);

		// Ayuda
		menuBar.add(helpMenu);

		infoMenuItem.setEnabled(false);
		infoMenuItem.addActionListener(this);
		helpMenu.add(infoMenuItem);

		// Barra de estado
		statusBar = new JPanel();
		statusBar.setLayout(new BorderLayout());
		add(statusBar, BorderLayout.SOUTH);

		statusBar.add(new JSeparator(), BorderLayout.NORTH);

		status = new JLabel();
		status.setBorder(new EmptyBorder(5, 5, 5, 5));
		statusBar.add(status, BorderLayout.SOUTH);
		setStatus("Empiece creando o cargando una partida.");

		boolean debug = false;
		if (debug) {
			SolverScreen panel;
			try {
				panel = new SolverScreen(this, Raetsel.createBoardFromRaetsel(5));
				setScreen(panel);
			} catch (IOException | BoardTooSmallException | CanNotAddGalaxyException e) {
				e.printStackTrace();
			}
		}

		setSize(MIN_WIDTH, MIN_HEIGHT);
		setResizable(false);
		setIconImage(Screen.icon("icon.jpg").getImage());
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		addWindowListener(this);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}

	public final void setScreen(Screen screen, boolean force) {
		if (this.screen != null) {
			if (force || this.screen.canBeRemoved()) {
				this.screen.release();
				remove(this.screen);
			} else
				return;
		}
		this.screen = screen;
		resetStatus();
		screen.added();
		add(screen, BorderLayout.NORTH);
		pack();
		repaint();
	}

	public final void setScreen(Screen screen) {
		setScreen(screen, false);
	}

	private final SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel(DEFAULT_BOARD_WIDTH, MIN_BOARD_SIZE,
			MAX_BOARD_SIZE, 1);
	private final SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel(DEFAULT_BOARD_HEIGHT, MIN_BOARD_SIZE,
			MAX_BOARD_SIZE, 1);

	public final void createNewBoard() {
		JSpinner widthSpinner = new JSpinner(widthSpinnerModel);
		JSpinner heightSpinner = new JSpinner(heightSpinnerModel);

		final JComponent[] inputs = new JComponent[] { new JLabel("Ancho:"), widthSpinner, new JLabel("Alto:"),
				heightSpinner };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Crear nuevo tablero", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			int width = (int) widthSpinner.getValue();
			int height = (int) heightSpinner.getValue();

			try {
				Board board = new Board(width, height);
				BoardCreatorScreen screen = new BoardCreatorScreen(this, board);
				setScreen(screen);
			} catch (BoardTooSmallException e) {
			}
		}
	}

	@SuppressWarnings("serial")
	private final static Map<String, Float> DIFFICULTY_CHOICES = new LinkedHashMap<String, Float>() {
		{
			put("Difícil", 1f);
			put("Normal", .5f);
			put("Fácil", 0f);
		}
	};
	@SuppressWarnings("serial")
	private final JComboBox<String> difficultyComboBox = new JComboBox<String>(
			DIFFICULTY_CHOICES.keySet().toArray(new String[DIFFICULTY_CHOICES.size()])) {
		{
			setSelectedIndex(1);
		}
	};

	public final void generateNewBoard() {
		JSpinner widthSpinner = new JSpinner(widthSpinnerModel);
		JSpinner heightSpinner = new JSpinner(heightSpinnerModel);

		final JComponent[] inputs = new JComponent[] { new JLabel("Ancho:"), widthSpinner, new JLabel("Alto:"),
				heightSpinner, new JLabel("Dificultad:"), difficultyComboBox };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Generar nuevo tablero", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			int width = (int) widthSpinner.getValue();
			int height = (int) heightSpinner.getValue();
			float difficulty = DIFFICULTY_CHOICES.get(difficultyComboBox.getSelectedItem());

			generateNewBoard(width, height, difficulty);
		}
	}

	public final void generateNewBoard(int width, int height, float difficulty) {
		JDialog dialog = new JDialog(this, "Generando tablero...", true);
		dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

		JLabel label = new JLabel("Generando tablero...");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		dialog.add(label);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		dialog.add(progressBar);

		GeneratorThread thread = new GeneratorThread(this, dialog, width, height, difficulty);
		JButton cancelButton = new JButton(new AbstractAction("Cancelar") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				thread.stop();
				thread.done();
			}
		});
		cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		dialog.add(cancelButton);

		dialog.setResizable(false);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		thread.start();
		dialog.setVisible(true);
	}

	private final SpinnerNumberModel raetselSpinnerModel = new SpinnerNumberModel(1, 1, 600, 1);

	public final void raetsel() throws BoardTooSmallException, IOException, CanNotAddGalaxyException {
		JSpinner raetselSpinner = new JSpinner(raetselSpinnerModel);

		final JComponent[] inputs = new JComponent[] { new JLabel("ID:"), raetselSpinner };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Cargar tablero de Raetsel",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			Board board = Raetsel.createBoardFromRaetsel((int) raetselSpinner.getValue());
			GameScreen screen = new GameScreen(this, new Game(board));
			setScreen(screen);
		}
	}

	private final void loadGame() throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setDialogTitle("Cargar partida");
		FileNameExtensionFilter tsb = new FileNameExtensionFilter("Partida de galaxias (." + Board.FILE_EXT + ")",
				Board.FILE_EXT);
		fileChooser.addChoosableFileFilter(tsb);
		fileChooser.setFileFilter(tsb);

		int userSelection = fileChooser.showOpenDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			GameScreen screen = new GameScreen(this, Game.createFromFile(file));
			setScreen(screen);
		}
	}

	public final void setStatus(String text) {
		status.setText(text);
		status.setToolTipText(text);
	}

	public final void resetStatus() {
		status.setText(" ");
		status.setToolTipText(null);
	}

	@Override
	public final void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == createBoardMenuItem)
			createNewBoard();
		else if (eventSource == generateBoardMenuItem)
			generateNewBoard();
		else if (eventSource == loadGameMenuItem)
			try {
				loadGame();
			} catch (IOException | BoardTooSmallException | CanNotAddGalaxyException e) {
				e.printStackTrace();
			}
		else if (eventSource == scale0_5MenuItem)
			config.setBoardScale(0.5f);
		else if (eventSource == scale0_75MenuItem)
			config.setBoardScale(0.75f);
		else if (eventSource == scale1_0MenuItem)
			config.setBoardScale(1);
		else if (eventSource == scale1_25MenuItem)
			config.setBoardScale(1.25f);
		else if (eventSource == scale1_5MenuItem)
			config.setBoardScale(1.5f);

		if (AppConfig.DEBUG)
			if (eventSource == raetselMenuItem)
				try {
					raetsel();
				} catch (BoardTooSmallException | IOException | CanNotAddGalaxyException e) {
					setStatus("Datos del tablero corrupto.");
				}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		if (this.screen != null && !this.screen.canBeRemoved())
			return;
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}