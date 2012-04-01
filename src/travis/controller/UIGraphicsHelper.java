/*
 * UIGraphicsHelper.java
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

package travis.controller;

import javax.swing.SwingUtilities;

import travis.controller.UIHelper.Mode;

public class UIGraphicsHelper {

    private static final UIGraphicsHelper INSTANCE = new UIGraphicsHelper();

    private UIGraphicsHelper() {
    }

    public static UIGraphicsHelper getInstance() {
        return INSTANCE;
    }

    public void resetConnectionsAndRepaintGraph() {
        resetConnections();
        repaintGraph();
    }

    public void resetConnectionsAndRepaintTree() {
        resetConnections();
        repaintTreeGraph();
    }

    public void resetConnections() {
        UIHelper.getInstance().getGraph().getConnectionPainter().reset();
    }

    public void repaintTreeGraph() {
        Runnable repainter = new Runnable() {
            @Override
            public void run() {
                UIHelper.getInstance().getGraph().updateImage();
            }
        };
        SwingUtilities.invokeLater(repainter);
    }

    public void repaintGraph() {
        Runnable repainter = new Runnable() {
            @Override
            public void run() {
                UIHelper.getInstance().getGraph().getConnectionPainter()
                        .setNeedRepaint(true);
                UIHelper.getInstance().getGraph().repaint();
                if (UIHelper.getInstance().getMode() == Mode.PLAYBACK) {
                    UIHelper.getInstance().getPlaybackPanel().repaint();
                }
            }
        };
        SwingUtilities.invokeLater(repainter);
    }

}
