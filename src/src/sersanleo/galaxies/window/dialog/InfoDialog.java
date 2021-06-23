package src.sersanleo.galaxies.window.dialog;

import static src.sersanleo.galaxies.util.SwingUtil.imgSrc;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import src.sersanleo.galaxies.AppConfig;
import src.sersanleo.galaxies.window.GameWindow;

public class InfoDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final JTabbedPane tabbedPane;

	public InfoDialog(GameWindow window) {
		super(window, "Informaci�n sobre Galaxias", false);

		tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Informaci�n", infoPanel());
		tabbedPane.addTab("Tutorial", tutorialPanel());

		add(tabbedPane);

		setResizable(true);
		setPreferredSize(new Dimension(575, 500));
		pack();
		setLocationRelativeTo(window);
	}

	private final Component htmlTextPane(String body) {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setOpaque(false);

		JTextPane textPane = new JTextPane();
		textPane.setBorder(new EmptyBorder(8, 8, 8, 8));
		textPane.setEditable(false);
		textPane.setOpaque(false);
		textPane.setContentType("text/html");
		textPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
					try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (Exception e1) {
						if (AppConfig.DEBUG)
							e1.printStackTrace();
					}
			}
		});

		textPane.setText(
				"<html> <head> <style> body { font-size: \"+panel.getFont().getSize()+\"; font-family: \"+panel.getFont().getFamily()+\"; } h3 { text-align: center; } </style> </head> <body> "
						+ body + " </body> </html>");
		textPane.setCaretPosition(0);

		scrollPane.getViewport().add(textPane);
		return scrollPane;
	}

	private final Component infoPanel() {
		return htmlTextPane(
				"<b>Galaxias</b> es un pasatiempo l�gico de origen japon�s. Su nombre original, <i>Tentai Show</i>, est� formado a partir de la combinaci�n de las palabras �punto�, �simetr�a� y �astron�mica� en su lengua de origen (relacionadas con la estrategia del juego). Fue creado por la compa��a editora nipona Nikoli, especializada en juegos, acertijos y rompecabezas l�gicos. Nikoli se ha hecho mundialmente conocida gracias a la popularidad de su Sudoku.<br><br> Cada tablero del juego consiste en una rejilla cuadrada o rectangular con c�rculos (tambi�n llamados galaxias) situados sobre la misma. El objetivo es dividir dicha rejilla en �reas, de forma que cada subdivisi�n contenga una �nica galaxia y tenga una simetr�a rotacional de 180�.<br> <center><img src='"
						+ imgSrc("solved_puzzle.jpg")
						+ "' /></center><br> P�gina oficial de Nikoli: <a href='https://www.nikoli.co.jp/en/puzzles/astronomical_show.html'>https://www.nikoli.co.jp/en/puzzles/astronomical_show.html</a><br><br> Los iconos usados han sido obtenidos de <a href='https://fontawesome.com/'>Font Awesome</a>.");
	}

	private final Component tutorialPanel() {
		return htmlTextPane(
				"<h3>Crear o cargar una partida</h3> Para comenzar una nueva partida, hay que dirigirse al men� <b>Juego > Nuevo</b>, y seleccionar una de las dos opciones:<br> - <b>Generar tablero</b> le permite jugar a un tablero generado autom�ticamente introduciendo las dimensiones del mismo y su dificultad.<br> - <b>Crear tablero</b> le permite crear un tablero a�adiendo visualmente las galaxias, introduciendo las dimensiones del mismo.<br> Si ya ha guardado una partida con anterioridad, puede cargar dicha partida en <b>Juego > Cargar partida</b>.<br> <h3>Jugar</h3> <center><img src='"
						+ imgSrc("game.png")
						+ "' /></center><br> Esta es la pantalla del juego, en la cual podr� colocar y eliminar aristas del tablero, ver su puntuaci�n actual y realizar distintas acciones con la botonera:<br> - <b>Guardar partida</b> (en un nuevo archivo o en el �ltimo archivo de guardado).<br> - <b>Deshacer y rehacer movimientos</b>.<br> - <b>Cargar y guardar estado del tablero</b>: permite retomar el estado actual del tablero m�s tarde.<br> - <b>Siguiente movimiento</b>: permite a la m�quina realizar el siguiente movimiento, penalizando 15 segundos la puntuaci�n.<br> - <b>Comprobar partida</b>: muestra cu�ntos errores hay y cu�ntas aristas faltan por colocar, penalizando 10 segundos la puntuaci�n.<br> - <b>Ver soluci�n</b>: muestra la soluci�n del tablero, eliminando la posibilidad de guardar la puntuaci�n.<br> Para cambiar el tama�o de visualizaci�n del tablero, en el men� <b>Vista</b> puede cambiarse la escala del mismo.<br> <center><img src='"
						+ imgSrc("example.jpg")
						+ "' /></center><br> El tablero se colorear� de gris marcando zonas con s�metria v�lida, aunque esto no quiere decir que dicha �rea se encuentre en la soluci�n. En rojo se mostrar�n las zonas sim�tricas pero que poseen en su interior m�s aristas marcadas de las necesarias. <h3>Cuadro de honor</h3> <center><img src='"
						+ imgSrc("ranking.png")
						+ "' /></center><br> El cuadro de honor aparecer� cuando complete un tablero, para introducir su nombre junto con la puntuaci�n conseguida. Puede accederse a �l en cualquier momento en <b>Juego > Cuadro de honor</b> para consultar las puntuaciones guardadas.");
	}
}
