/*
 * GraphPanel.java
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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

import travis.view.Util;
import travis.view.project.graph.connection.ConnectionPainter;
import travis.view.settings.Settings;

public class GraphPanel extends JPanel {

    private static final long serialVersionUID = -2719889219523140214L;
    private static final int MARGIN_X = 5;
    private static final int MARGIN_Y = 5;

    private final TreeRepresentation treeRep;
    private final ConnectionPainter connectPainter;

    private int alignX;
    private int alignY;
    private Dimension oldD;

    public GraphPanel() {
        super();
        setOpaque(true);

        treeRep = new TreeRepresentation();
        connectPainter = new ConnectionPainter(treeRep);
    }

    public TreeRepresentation getTreeRepRepresentation() {
        return treeRep;
    }

    public ConnectionPainter getConnectionPainter() {
        return connectPainter;
    }

    public void updateImage() {
        connectPainter.setNeedRepaint(true);
        int radius = Math.min(getWidth() - MARGIN_X * 2, getHeight() - MARGIN_Y
                * 2) / 2;
        treeRep.createImage(radius, Settings.getInstance().getGraphRotate());
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (oldD == null || !oldD.equals(getSize())) {
            oldD = getSize();
            int diameter = Math.min(getWidth() - MARGIN_X * 2, getHeight()
                    - MARGIN_Y * 2);
            alignX = (getWidth() - diameter) / 2;
            alignY = (getHeight() - diameter) / 2;
            updateImage();
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(Util.HINTS);
        g2.translate(alignX, alignY);

        g2.drawImage(treeRep.getImage(), null, 0, 0);

        g2.drawImage(connectPainter.getImage(getWidth(), getHeight()), null, 0,
                0);
    }

    public int getAlignX() {
        return alignX;
    }

    public int getAlignY() {
        return alignY;
    }

}
