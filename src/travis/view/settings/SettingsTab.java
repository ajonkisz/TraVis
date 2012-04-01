/*
 * SettingsTab.java
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
