package src.sersanleo.galaxies.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import src.sersanleo.galaxies.AppConfig;

public final class SwingUtil {
	public static final ImageIcon getIconResource(String name) {
		try {
			return new HighQualityIcon(ImageIO.read(SwingUtil.class.getResource(name)));
		} catch (IOException e) {
			if (AppConfig.DEBUG)
				e.printStackTrace();
			return new ImageIcon();
		}
	}

	public static final ImageIcon icon(String name) {
		return getIconResource("/icons/" + name);
	}

	public static final JLabel imageLabel(String name) {
		JLabel label = new JLabel(getIconResource("/images/" + name));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		return label;
	}

	public static final JButton iconButton(String iconName, String tooltip) {
		ImageIcon icon = icon(iconName);
		JButton button = new JButton(icon);
		button.setDisabledIcon(new HighQualityIcon(button.getDisabledIcon()));
		button.setToolTipText(tooltip);
		return button;
	}

	public static JLabel link(String text, String link) {
		JLabel label = new JLabel("<html><a href='" + link + "'>" + text + "</a></html");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(link));
				} catch (Exception ex) {
				}
			}
		});
		return label;
	}

	public static JLabel link(String link) {
		return link(link, link);
	}
}
