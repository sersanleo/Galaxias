package src.sersanleo.galaxies.window.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.game.Game;
import src.sersanleo.galaxies.game.rendering.GameRenderer;
import src.sersanleo.galaxies.util.ExtFileInputStream;
import src.sersanleo.galaxies.util.ExtFileOutputStream;
import src.sersanleo.galaxies.util.Vector2i;
import src.sersanleo.galaxies.window.GameWindow;

public class RankingDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final JTable table;
	private final JScrollPane scrollPane;

	public RankingDialog(GameWindow window) {
		super(window, "Cuadro de honor", true);

		Ranking ranking = Ranking.read();
		table = new JTable(ranking);
		table.setDefaultRenderer(Object.class, ranking.new RankingRenderer());
		table.setAutoCreateRowSorter(true);
		table.getRowSorter().toggleSortOrder(3);

		scrollPane = new JScrollPane(table);
		add(scrollPane);
		pack();
		setLocationRelativeTo(window);
	}

	public final void addRow(Game game) {
		((Ranking) table.getModel()).addRow(game);
		table.requestFocus();
		table.editCellAt(0, 0);
		table.getRowSorter().setSortKeys(null);
	}

	public final static class Ranking extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		private final static String[] COLUMN_NAMES = new String[] { "Jugador", "Tamaño del tablero", "Movimientos",
				"Tiempo" };
		private final static File FILE = new File("./.ranking");

		private final File file;
		private final List<Object[]> rows;
		private boolean editable = false;

		private Ranking(File file, List<Object[]> rows) {
			this.file = file;
			this.rows = rows;
		}

		private Ranking(File file) {
			this(file, new ArrayList<Object[]>());
		}

		public String getColumnName(int col) {
			return COLUMN_NAMES[col].toString();
		}

		public int getRowCount() {
			return rows.size();
		}

		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public Object getValueAt(int row, int col) {
			return rows.get(row)[col];
		}

		public void setValueAt(Object value, int row, int col) {
			rows.get(row)[col] = value;
			fireTableCellUpdated(row, col);
			try {
				save();
			} catch (IOException e) {
				if (AppConfig.DEBUG)
					e.printStackTrace();
			}
		}

		public boolean isCellEditable(int row, int col) {
			return editable && row == 0 && col == 0;
		}

		public void addRow(Game game) {
			rows.add(0,
					new Object[] { System.getProperty("user.name", "Usuario"),
							new Vector2i(game.board.width, game.board.height), game.solution.getMoves(),
							game.elapsedSeconds() });
			this.fireTableRowsInserted(0, 0);
			editable = true;
			try {
				save();
			} catch (IOException e) {
				if (AppConfig.DEBUG)
					e.printStackTrace();
			}
		}

		protected final void save(File file) throws IOException {
			if (!file.exists())
				file.createNewFile();

			ExtFileOutputStream stream = new ExtFileOutputStream(file);

			for (Object[] row : rows) {
				stream.writeString((String) row[0]);
				stream.writeVector2i((Vector2i) row[1]);
				stream.writeInt((int) row[2]);
				stream.writeLong((long) row[3]);
			}

			stream.flush();
			stream.close();
		}

		protected final void save() throws IOException {
			save(file);
		}

		private final static Ranking read(File file) throws IOException {
			if (!file.exists())
				return new Ranking(file);

			ExtFileInputStream stream = new ExtFileInputStream(file);

			List<Object[]> rows = new ArrayList<Object[]>();
			while (stream.available() > 0)
				rows.add(new Object[] { stream.readString(), stream.readVector2i(), stream.readInt(),
						stream.readLong() });

			stream.close();

			return new Ranking(file, rows);
		}

		public final static Ranking read() {
			try {
				return read(FILE);
			} catch (IOException e) {
				return new Ranking(FILE);
			}
		}

		private final class RankingRenderer extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected,
					boolean hasFocus, int row, int col) {
				Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, col);
				
				row = table.convertRowIndexToModel(row);
				
				cell.setForeground(Color.BLACK);
				if (isCellEditable(row, 0)) {
					cell.setBackground(GameRenderer.WIN_COLOR);
					cell.setFont(cell.getFont().deriveFont(Font.BOLD));
				} else {
					cell.setBackground(Color.WHITE);
					cell.setFont(cell.getFont().deriveFont(Font.PLAIN));
				}

				return cell;
			}

			@Override
			public void setValue(Object value) {
				if (value instanceof Vector2i) {
					Vector2i v = (Vector2i) value;
					super.setValue(v.x + "x" + v.y);
				} else if (value instanceof Long) {
					long elapsedSeconds = (long) value;
					int seconds = (int) (elapsedSeconds % 60);
					int minutes = (int) (elapsedSeconds / 60) % 60;
					int hours = (int) (elapsedSeconds / 3600);
					super.setValue(hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
				} else
					super.setValue(value);
			}
		}
	}
}