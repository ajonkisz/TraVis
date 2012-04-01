/*
 * GraphLayeredPane.java
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

package travis.view.project.graph;

import java.awt.BorderLayout;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GraphLayeredPane extends JPanel {

    private static final long serialVersionUID = 2855872910533934574L;

    private final GraphPanel graph;
    private final GraphTooltip tooltip;

    public GraphLayeredPane(GraphPanel graph, GraphTooltip tooltip) {
        super(new BorderLayout());
        this.graph = graph;
        this.tooltip = tooltip;

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setOpaque(true);

        layeredPane.add(graph, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(tooltip, JLayeredPane.POPUP_LAYER);

        add(layeredPane, BorderLayout.CENTER);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        graph.setBounds(0, 0, width, height);
        tooltip.setBounds(0, 0, width, height);
    }
}
