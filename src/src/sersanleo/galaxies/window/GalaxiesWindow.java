package src.sersanleo.galaxies.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.UIManager;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.game.Game;

public class GalaxiesWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	// Menú
	private final JMenuBar menuBar;

	private final JMenu gameMenu = new JMenu("Juego");
	private final JMenuItem newGameMenuItem = new JMenuItem("Nuevo");
	private final JMenuItem createBoardMenuItem = new JMenuItem("Crear tablero");
	private final JMenuItem saveProgressMenuItem = new JMenuItem("Guardar partida");
	private final JMenuItem saveBoardMenuItem = new JMenuItem("Guardar tablero");

	private final JMenu editMenu = new JMenu("Editar");
	private final JMenuItem undoMenuItem = new JMenuItem("Deshacer");
	private final JMenuItem redoMenuItem = new JMenuItem("Rehacer");

	private final BoardPanel boardPanel;

	public GalaxiesWindow() {
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

		boardPanel = new BoardPanel(new Game(new Board(8, 15)));
		boardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(boardPanel);

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

	@Override
	public final void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == newGameMenuItem) {

		} else if (eventSource == createBoardMenuItem) {

		} else if (eventSource == saveProgressMenuItem) {

		} else if (eventSource == saveBoardMenuItem) {

		} else if (eventSource == undoMenuItem) {

		} else if (eventSource == redoMenuItem) {

		}
	}
}