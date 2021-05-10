package src.sersanleo.galaxies.window;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.exception.BoardTooSmallException;
import src.sersanleo.galaxies.game.exception.CanNotAddGalaxyException;

public class GameWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	// Menú
	private final JMenuBar menuBar;

	// Barra de estado
	private final JPanel statusBar = null; // TODO
	private final JLabel status = null; // TODO

	private final JMenu gameMenu = new JMenu("Juego");
	private final JMenuItem newGameMenuItem = new JMenuItem("Nuevo");
	private final JMenuItem createBoardMenuItem = new JMenuItem("Crear tablero");
	private final JMenuItem saveProgressMenuItem = new JMenuItem("Guardar partida");
	private final JMenuItem openBoardMenuItem = new JMenuItem("Abrir tablero");

	private final JMenu editMenu = new JMenu("Editar");
	private final JMenuItem undoMenuItem = new JMenuItem("Deshacer");
	private final JMenuItem redoMenuItem = new JMenuItem("Rehacer");

	// Contenido
	private JPanel content;
	private JPanel nextContent;

	public GameWindow() throws BoardTooSmallException, CanNotAddGalaxyException, IOException {
		// Configuración del JFrame
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

		setVisible(true);
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

	private final void setContent(JPanel content) {
		if (this.content != null)
			remove(this.content);
		this.content = content;
		add(content);
		pack();
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
			BoardCreatorPanel panel = new BoardCreatorPanel(
					new Board((int) widthSpinner.getValue(), (int) heightSpinner.getValue()));
			setContent(panel);
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
			
			setContent(new GamePanel(new Game(Board.createFromFile(file))));
		}
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

		}
	}
}