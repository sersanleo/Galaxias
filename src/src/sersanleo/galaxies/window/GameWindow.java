package src.sersanleo.galaxies.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.window.content.AppContent;
import src.sersanleo.galaxies.window.content.BoardCreatorPanel;
import src.sersanleo.galaxies.window.content.GamePanel;

public class GameWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private static final int MIN_WIDTH = 400;
	private static final int MIN_HEIGHT = 400;

	public final AppConfig config;

	// Menú
	private final JMenuBar menuBar;

	private final JMenu gameMenu = new JMenu("Juego");
	private final JMenuItem newGameMenuItem = new JMenuItem("Nuevo");
	private final JMenuItem createBoardMenuItem = new JMenuItem("Crear tablero");
	private final JMenuItem saveProgressMenuItem = new JMenuItem("Guardar partida");
	private final JMenuItem openBoardMenuItem = new JMenuItem("Abrir tablero");

	private final JMenu editMenu = new JMenu("Editar");
	private final JMenuItem undoMenuItem = new JMenuItem("Deshacer");
	private final JMenuItem redoMenuItem = new JMenuItem("Rehacer");

	private final JMenu viewMenu = new JMenu("Vista");
	private final ButtonGroup scaleButtonGroup = new ButtonGroup();
	private final JMenuItem scale0_5MenuItem = new JRadioButtonMenuItem("50%");
	private final JMenuItem scale0_75MenuItem = new JRadioButtonMenuItem("75%");
	private final JMenuItem scale1_0MenuItem = new JRadioButtonMenuItem("100%");
	private final JMenuItem scale1_25MenuItem = new JRadioButtonMenuItem("125%");
	private final JMenuItem scale1_5MenuItem = new JRadioButtonMenuItem("150%");

	// Barra de estado
	private final JPanel statusBar;
	private final JLabel status;

	// Contenido
	private AppContent content;

	public GameWindow() throws BoardTooSmallException, CanNotAddGalaxyException, IOException {
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

		newGameMenuItem.addActionListener(this);
		gameMenu.add(newGameMenuItem);

		createBoardMenuItem.addActionListener(this);
		gameMenu.add(createBoardMenuItem);

		gameMenu.add(new JSeparator());

		saveProgressMenuItem.setEnabled(false);
		saveProgressMenuItem.addActionListener(this);
		gameMenu.add(saveProgressMenuItem);

		openBoardMenuItem.addActionListener(this);
		gameMenu.add(openBoardMenuItem);

		// Editar
		editMenu.setEnabled(false);
		menuBar.add(editMenu);

		undoMenuItem.setEnabled(false);
		undoMenuItem.addActionListener(this);
		editMenu.add(undoMenuItem);

		redoMenuItem.setEnabled(false);
		redoMenuItem.addActionListener(this);
		editMenu.add(redoMenuItem);

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

		// Barra de estado
		statusBar = new JPanel();
		statusBar.setLayout(new BorderLayout());
		add(statusBar, BorderLayout.SOUTH);

		statusBar.add(new JSeparator(), BorderLayout.NORTH);

		status = new JLabel();
		status.setBorder(new EmptyBorder(5, 5, 5, 5));
		statusBar.add(status, BorderLayout.SOUTH);
		setStatus("Esperando una acción.");

		setSize(MIN_WIDTH, MIN_HEIGHT);
		setResizable(false);
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		packAndCenter();
	}

	public final void setContent(AppContent content) {
		if (this.content != null) {
			if (this.content.canBeRemoved()) {
				this.content.release();
				remove(this.content);
			} else
				return;
		}
		this.content = content;
		add(content, BorderLayout.NORTH);
		resetStatus();
		packAndCenter();
	}

	private final SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel(5, 2, 14, 1);
	private final SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel(5, 2, 14, 1);

	public final void createNewBoard() throws BoardTooSmallException {
		JSpinner widthSpinner = new JSpinner(widthSpinnerModel);
		JSpinner heightSpinner = new JSpinner(heightSpinnerModel);

		final JComponent[] inputs = new JComponent[] { new JLabel("Ancho:"), widthSpinner, new JLabel("Alto:"),
				heightSpinner };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Crear nuevo tablero", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			int width = (int) widthSpinner.getValue();
			int height = (int) heightSpinner.getValue();
			BoardCreatorPanel content = new BoardCreatorPanel(this, new Board(width, height));
			setContent(content);
		}
	}

	private final void openBoard() throws IOException, BoardTooSmallException, CanNotAddGalaxyException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Abrir tablero");
		FileNameExtensionFilter tsb = new FileNameExtensionFilter("Tablero de galaxias (." + Board.FILE_EXT + ")",
				Board.FILE_EXT);
		fileChooser.addChoosableFileFilter(tsb);
		fileChooser.setFileFilter(tsb);

		int userSelection = fileChooser.showOpenDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			GamePanel content = new GamePanel(this, new Game(Board.createFromFile(file)));
			setContent(content);
		}
	}

	public final void packAndCenter() {
		pack();
		this.setLocationRelativeTo(null);
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

		if (eventSource == newGameMenuItem) {

		} else if (eventSource == createBoardMenuItem)
			try {
				createNewBoard();
			} catch (BoardTooSmallException e) {
			}
		else if (eventSource == saveProgressMenuItem) {

		} else if (eventSource == openBoardMenuItem) {
			try {
				openBoard();
			} catch (IOException | BoardTooSmallException | CanNotAddGalaxyException e) {
				e.printStackTrace();
			}
		} else if (eventSource == undoMenuItem) {

		} else if (eventSource == redoMenuItem) {

		} else if (eventSource == scale0_5MenuItem)
			config.setBoardScale(0.5f);
		else if (eventSource == scale0_75MenuItem)
			config.setBoardScale(0.75f);
		else if (eventSource == scale1_0MenuItem)
			config.setBoardScale(1);
		else if (eventSource == scale1_25MenuItem)
			config.setBoardScale(1.25f);
		else if (eventSource == scale1_5MenuItem)
			config.setBoardScale(1.5f);
	}
}