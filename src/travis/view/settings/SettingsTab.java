package travis.view.settings;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class SettingsTab extends JPanel {

	private static final long serialVersionUID = -7262972116201012199L;

	public SettingsTab() {
		super();
	}

	public SettingsTab(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public SettingsTab(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public SettingsTab(LayoutManager layout) {
		super(layout);
	}

	public abstract void updateValues();

}
