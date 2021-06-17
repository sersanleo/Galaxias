package src.sersanleo.galaxies.util;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public final class SwingUtil {
	public static final ImageIcon icon(String name) {
		try {
			return new HighQualityIcon(ImageIO.read(SwingUtil.class.getResource("/icons/" + name)));
		} catch (IOException e) {
			e.printStackTrace();
			return new ImageIcon();
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
