package src.sersanleo.galaxies.window.content;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.AppConfig.AppConfigChangeListener;
import src.sersanleo.galaxies.AppConfig.ConfigParameter;
import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.window.GameWindow;
import src.sersanleo.galaxies.window.component.BoardView;
import src.sersanleo.galaxies.window.component.listener.BoardMouseListener;

public class BoardCreatorPanel extends AppContent implements ActionListener, AppConfigChangeListener {
	private static final long serialVersionUID = 1L;

	public final Board board;

	private final BoardView boardView;
	private final JButton saveButton;

	public BoardCreatorPanel(GameWindow window, Board board) {
		super(window);
		this.board = board;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardView = new BoardView(board, window.config.getBoardScale());
		boardView.setAlignmentX(Component.CENTER_ALIGNMENT);
		boardView.addMouseListener(new BoardMouseListener(board, boardView, window));
		add(boardView);

		saveButton = new JButton("Guardar tablero");
		saveButton.setToolTipText("Guardar tablero");
		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveButton.addActionListener(this);
		add(saveButton);

		window.config.addAppConfigChangeListener(this);
	}

	private final void save() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Guardar tablero");
		FileNameExtensionFilter tsb = new FileNameExtensionFilter("Tablero de galaxias (." + Board.FILE_EXT + ")",
				Board.FILE_EXT);
		fileChooser.addChoosableFileFilter(tsb);
		fileChooser.setFileFilter(tsb);
		fileChooser.setSelectedFile(new File(board.width + "x" + board.height + "." + Board.FILE_EXT));

		int userSelection = fileChooser.showSaveDialog(this);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			String fileName = file.getName();
			if (!fileName.endsWith("." + Board.FILE_EXT))
				file = new File(file.getParent() + "/" + file.getName() + "." + Board.FILE_EXT);

			if (!file.exists())
				file.createNewFile();

			board.save(file);
		}
	}

	@Override
	public final void actionPerformed(ActionEvent event) {
		Object eventSource = event.getSource();

		if (eventSource == saveButton)
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	public void appConfigChange(AppConfig config, ConfigParameter parameter) {
		if (parameter == ConfigParameter.BOARD_SCALE) {
			boardView.setScale(config.getBoardScale());
			window.packAndCenter();
		}
	}

	@Override
	public void release() {
		window.config.removeAppConfigChangeListener(this);
	}
}
