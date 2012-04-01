/*
 * SettingsPane.java
 *
 * Copyright (C) 2011-2012, Artur Jonkisz, <travis.source@gmail.com>
 *
 * This file is part of TraVis.
 * See https://github.com/ajonkisz/TraVis for more info.
 *
 * TraVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with TraVis.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    private final Set<SettingsTab> tabs;

    public SettingsPane() {
        super(JTabbedPane.LEFT);

        tabs = new HashSet<SettingsTab>();

        SettingsTab visibilityPanel = new VisibilityPanel();
        SettingsTab layersPanel = new LayersPanel();
        SettingsTab graphPanel = new GraphPanel();
        SettingsTab tracesPanel = new TracesPanel();
        SettingsTab colorsPanel = new ColorsPanel();

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
