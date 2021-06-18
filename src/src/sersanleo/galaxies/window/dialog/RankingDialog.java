package src.sersanleo.galaxies.window.dialog;

import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import src.sersanleo.galaxies.util.ExtFileInputStream;
import src.sersanleo.galaxies.util.ExtFileOutputStream;
import src.sersanleo.galaxies.util.Vector2i;
import src.sersanleo.galaxies.window.GameWindow;

public class RankingDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private static final int MAX_REGISTER = 30;

	private final static String[] columnNames = new String[] { "Jugador", "Tamaño del tablero", "Movimientos",
			"Tiempo" };

	private final JTable table;
	private final JScrollPane scrollPane;

	public RankingDialog(GameWindow window) {
		super(window, "Cuadro de honor", true);

		Object[][] data = new Object[][] { { "Sergio", "14x14", 3, 500 } };

		table = new JTable(data, columnNames);
		table.setAutoCreateRowSorter(true);
		scrollPane = new JScrollPane(table);

		add(scrollPane);
		pack();
		setLocationRelativeTo(window);
	}

	public final void save(File file) throws IOException {
		ExtFileOutputStream stream = new ExtFileOutputStream(file);

		stream.flush();
		stream.close();
	}

	private final static void read(File file) throws IOException {
		ExtFileInputStream stream = new ExtFileInputStream(file);

		while (stream.available() > 0) {
			String name = stream.readString();
			Vector2i dimensions = new Vector2i(stream.readInt(), stream.readInt());
			int movements = stream.readInt();
			long seconds = stream.readLong();
		}

		stream.close();
	}
}
