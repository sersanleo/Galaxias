package src.sersanleo.galaxies.window.dialog;

import static src.sersanleo.galaxies.util.SwingUtil.imageLabel;
import static src.sersanleo.galaxies.util.SwingUtil.link;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import src.sersanleo.galaxies.window.GameWindow;

public class InfoDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final JTabbedPane tabbedPane;
	private final ScrollHelper scrollHelper = new ScrollHelper();

	public InfoDialog(GameWindow window) {
		super(window, "Información sobre Galaxias", false);

		tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Información", tutorialPanel());

		add(tabbedPane);

		setResizable(true);
		setPreferredSize(new Dimension(500, 480));
		pack();
		setLocationRelativeTo(window);
	}

	private final JScrollPane tutorialPanel() {
		JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(scrollPane.getViewport().getWidth(), 420));
		panel.setBorder(new EmptyBorder(8, 8, 8, 8));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.addComponentListener(scrollHelper);

		JLabel label = new JLabel(
				"<html><b>Galaxias</b> es un pasatiempo lógico de origen japonés. Su nombre original, <i>Tentai Show</i>, está formado a partir de la combinación de las palabras “punto”, “simetría” y “astronómica” en su lengua de origen (relacionadas con la estrategia del juego). Fue creado por la compañía editora nipona Nikoli, especializada en juegos, acertijos y rompecabezas lógicos. Nikoli se ha hecho mundialmente conocida gracias a la popularidad de su Sudoku.<br><br>\r\n"
						+ "Cada tablero del juego consiste en una rejilla cuadrada o rectangular con círculos (también llamados galaxias) situados sobre la misma. El objetivo es dividir dicha rejilla en áreas, de forma que cada subdivisión contenga una única galaxia y tenga una simetría rotacional de 180º. \r\n"
						+ "</html>");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);

		panel.add(Box.createVerticalStrut(8));
		panel.add(imageLabel("solved_puzzle.jpg"));
		panel.add(Box.createVerticalStrut(8));

		label = new JLabel("<html><div>Página oficial:</div></html>");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);
		panel.add(link("https://www.nikoli.co.jp/en/puzzles/astronomical_show.html"));

		scrollPane.getViewport().add(panel);
		return scrollPane;
	}

	private final JScrollPane examplesPanel() {
		JScrollPane scrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(scrollPane.getViewport().getWidth(), 100));
		panel.setBorder(new EmptyBorder(8, 8, 8, 8));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		scrollPane.getViewport().add(panel);
		return scrollPane;
	}

	private final static class ScrollHelper extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			JPanel panel = (JPanel) e.getSource();
			Component last = panel.getComponent(panel.getComponentCount() - 1);
			int height = last.getLocation().y + last.getSize().height
					+ ((EmptyBorder)panel.getBorder()).getBorderInsets().bottom;
			panel.setPreferredSize(new Dimension((int) panel.getPreferredSize().getWidth(), height));
			System.out.println(e.toString());
		}
	}
}
