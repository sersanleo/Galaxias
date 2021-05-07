package src.sersanleo.galaxies.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;
import src.sersanleo.galaxies.window.listener.BoardMouseListener;
import src.sersanleo.galaxies.window.listener.GameMouseListener;

public class GameWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	// Men�
	private final JMenuBar menuBar;

	// Barra de estado
	private final JPanel statusBar = null; // TODO
	private final JLabel status = null; // TODO

	private final JMenu gameMenu = new JMenu("Juego");
	private final JMenuItem newGameMenuItem = new JMenuItem("Nuevo");
	private final JMenuItem createBoardMenuItem = new JMenuItem("Crear tablero");
	private final JMenuItem saveProgressMenuItem = new JMenuItem("Guardar partida");
	private final JMenuItem saveBoardMenuItem = new JMenuItem("Guardar tablero");

	private final JMenu editMenu = new JMenu("Editar");
	private final JMenuItem undoMenuItem = new JMenuItem("Deshacer");
	private final JMenuItem redoMenuItem = new JMenuItem("Rehacer");

	public BoardPanel boardPanel;
	private Board board;

	public GameWindow() throws BoardTooSmallException, CanNotAddGalaxyException {
		// Configuraci�n del JFrame
		super("Galaxias");

		// Intenta establecer el Look & Feel del sistema
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		board = new Board(7, 7);
		boardPanel = new BoardPanel(board);
		boardPanel.addMouseListener(new BoardMouseListener(board, boardPanel));
		boardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(boardPanel);

		// Men�
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

		saveBoardMenuItem.setEnabled(false);
		saveBoardMenuItem.addActionListener(this);
		gameMenu.add(saveBoardMenuItem);

		// Editar
		editMenu.setEnabled(false);
		menuBar.add(editMenu);

		undoMenuItem.setEnabled(false);
		undoMenuItem.addActionListener(this);
		editMenu.add(undoMenuItem);

		redoMenuItem.setEnabled(false);
		redoMenuItem.addActionListener(this);
		editMenu.add(redoMenuItem);

		setVisible(true);
		setSize(500, 500);
		pack();
	}

	@Override
	public final void setSize(int width, int height) {
		Insets insets = this.getInsets();
		Dimension menu = menuBar.getSize();

		int horizontalInsets = insets.left + insets.right;
		int verticalInsets = insets.top + insets.bottom + menu.height;
		super.setSize(width + horizontalInsets, height + verticalInsets);
	}

	private final SpinnerNumberModel widthSpinnerModel = new SpinnerNumberModel(5, 2, 14, 1);
	private final SpinnerNumberModel heightSpinnerModel = new SpinnerNumberModel(5, 2, 14, 1);

	public final void showCreateNewBoardDialog() throws BoardTooSmallException {
		JSpinner widthSpinner = new JSpinner(widthSpinnerModel);
		JSpinner heightSpinner = new JSpinner(heightSpinnerModel);

		final JComponent[] inputs = new JComponent[] { new JLabel("Ancho:"), widthSpinner, new JLabel("Alto:"),
				heightSpinner };

		int result = JOptionPane.showConfirmDialog(this, inputs, "Crear nuevo tablero", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			remove(boardPanel);

			board = new Board((int) widthSpinner.getValue(), (int) heightSpinner.getValue());
			boardPanel = new BoardPanel(board);
			boardPanel.addMouseListener(new BoardMouseListener(board, boardPanel));
			boardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

			add(boardPanel);
			pack();
		}
	}

	@Override
	public final void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == newGameMenuItem) {
			remove(boardPanel);

			Game game = new Game(board);
			BoardPanel newPanel = new BoardPanel(game);
			newPanel.fitToSize(boardPanel.getWidth(), boardPanel.getHeight());
			boardPanel = newPanel;
			GameMouseListener mouseListener = new GameMouseListener(game, boardPanel);
			boardPanel.addMouseListener(mouseListener);
			boardPanel.addMouseMotionListener(mouseListener);
			add(boardPanel);
		} else if (eventSource == createBoardMenuItem)
			try {
				showCreateNewBoardDialog();
			} catch (BoardTooSmallException e) {
			}
		else if (eventSource == saveProgressMenuItem) {

		} else if (eventSource == saveBoardMenuItem) {

		} else if (eventSource == undoMenuItem) {

		} else if (eventSource == redoMenuItem) {

		}
	}
}