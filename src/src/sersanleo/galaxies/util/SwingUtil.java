package src.sersanleo.galaxies.util;

import java.awt.Component;
import java.io.IOException;

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

	public static final ImageIcon image(String name) {
		return getIconResource("/images/" + name);
	}

	public static final String imgSrc(String name) {
		return SwingUtil.class.getResource("/images/" + name).toString();
	}

	public static final JLabel imageLabel(String name) {
		try {
			JLabel label = new JLabel(image(name));
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			return label;
		} catch (Exception e) {
			if (AppConfig.DEBUG)
				e.printStackTrace();
			return new JLabel();
		}
	}

	public static final JButton iconButton(String iconName, String tooltip) {
		ImageIcon icon = icon(iconName);
		JButton button = new JButton(icon);
		button.setDisabledIcon(new HighQualityIcon(button.getDisabledIcon()));
		button.setToolTipText(tooltip);
		return button;
	}
}
