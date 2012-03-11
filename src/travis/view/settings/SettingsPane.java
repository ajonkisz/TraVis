package travis.view.settings;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JTabbedPane;

import travis.util.Messages;

public class SettingsPane extends JTabbedPane implements Observer {

	private static final long serialVersionUID = -6658851623888731026L;

	private static final Settings SETS = Settings.getInstance();

	private final SettingsTab visibilityPanel;
	private final SettingsTab layersPanel;
	private final SettingsTab graphPanel;
	private final SettingsTab tracesPanel;
	private final SettingsTab colorsPanel;
	private final Set<SettingsTab> tabs;

	public SettingsPane() {
		super(JTabbedPane.LEFT);
		
		tabs = new HashSet<SettingsTab>();
		
		visibilityPanel = new VisibilityPanel();
		layersPanel = new LayersPanel();
		graphPanel = new GraphPanel();
		tracesPanel = new TracesPanel();
		colorsPanel = new ColorsPanel();

		addTab(Messages.get("visibility"), visibilityPanel);
		tabs.add(visibilityPanel);
		addTab(Messages.get("layers"), layersPanel);
		tabs.add(layersPanel);
		addTab(Messages.get("graph"), graphPanel);
		tabs.add(graphPanel);
		addTab(Messages.get("traces"), tracesPanel);
		tabs.add(tracesPanel);
		addTab(Messages.get("colors"), colorsPanel);
		tabs.add(colorsPanel);
		
		SETS.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		for (SettingsTab tab : tabs) {
			tab.updateValues();
		}
	}

}
