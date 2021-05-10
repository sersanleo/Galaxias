package src.sersanleo.galaxies.window;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import src.sersanleo.galaxies.game.Board;
import src.sersanleo.galaxies.window.listener.BoardMouseListener;

public class BoardCreatorPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;

	public final Board board;

	private final BoardPanel boardPanel;
	private final JButton saveButton;

	public BoardCreatorPanel(Board board) {
		this.board = board;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		boardPanel = new BoardPanel(board);
		boardPanel.addMouseListener(new BoardMouseListener(board, boardPanel));
		add(boardPanel);

		saveButton = new JButton("Guardar");
		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveButton.addActionListener(this);
		add(saveButton);
	}

	private final void save() throws IOException {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Guardar tablero");
		FileNameExtensionFilter tsb = new FileNameExtensionFilter("Tablero de galaxias (." + Board.FILE_EXT + ")",
				Board.FILE_EXT);
		fileChooser.addChoosableFileFilter(tsb);
		fileChooser.setFileFilter(tsb);

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
}
