package src.sersanleo.galaxies.util;

import java.awt.Color;

public final class ColorUtil {
	public final static Color interpolate(Color start, Color end, float factor) {
		float invertFactor = 1 - factor;
		return new Color(Math.round(invertFactor * start.getRed() + factor * end.getRed()),
				Math.round(invertFactor * start.getBlue() + factor * end.getBlue()),
				Math.round(invertFactor * start.getGreen() + factor * end.getGreen()));
	}

	public final static Color add(Color base, Color color, float factor) {
		return new Color(Math.min(250, Math.round(base.getRed() + color.getRed() * factor)),
				Math.min(250, Math.round(base.getGreen() + color.getGreen() * factor)),
				Math.min(250, Math.round(base.getBlue() + color.getBlue() * factor)));
	}
}