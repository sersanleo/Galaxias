package src.sersanleo.galaxies;

import java.util.LinkedHashSet;
import java.util.Set;

public class AppConfig {
	public final static boolean DEBUG = true;

	private float boardScale = 1;

	private final Set<AppConfigChangeListener> listeners = new LinkedHashSet<AppConfigChangeListener>();

	public final void addAppConfigChangeListener(AppConfigChangeListener listener) {
		listeners.add(listener);
	}

	public final void removeAppConfigChangeListener(AppConfigChangeListener listener) {
		listeners.remove(listener);
	}

	private final void propagateConfigChange(ConfigParameter parameter) {
		for (AppConfigChangeListener listener : listeners)
			listener.appConfigChange(this, parameter);
	}

	public final float getBoardScale() {
		return boardScale;
	}

	public final void setBoardScale(float boardScale) {
		this.boardScale = boardScale;
		propagateConfigChange(ConfigParameter.BOARD_SCALE);
	}

	public static enum ConfigParameter {
		BOARD_SCALE;
	}

	public static interface AppConfigChangeListener {
		public void appConfigChange(AppConfig config, ConfigParameter parameter);
	}
}