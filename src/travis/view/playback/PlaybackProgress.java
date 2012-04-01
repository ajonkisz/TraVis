/*
 * PlaybackProgress.java
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

package travis.view.playback;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import travis.controller.UIGraphicsHelper;
import travis.model.attach.Playback;
import travis.util.Messages;

public class PlaybackProgress extends JPanel {

    private static final long serialVersionUID = -8725982120299231092L;

    private static final Color SELECTION = new Color(0xaa, 0xff, 0x99, 200);
    private static final Color CURRENT_POS = new Color(0x44, 0x44, 0xff);
    private static final int TOP_MARGIN = 3;

    private final PlaybackPanel playbackPanel;
    private final MouseHandler mouseHandler;

    private Image depthGraph;

    public PlaybackProgress(PlaybackPanel playbackPanel) {
        super();
        this.playbackPanel = playbackPanel;
        setBackground(new Color(0xdd, 0xdd, 0xdd));
        mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setupPlaybackGraph(List<Integer> depths, int maxDepth) {
        if (maxDepth == 0) {
            depthGraph = null;
            return;
        }

        int red = (255 << 24) - (1 << 23);
        int w = getWidth();
        int h = getHeight() - TOP_MARGIN;
        int[] pix = new int[w * h];
        int maxIndex = depths.size() - 1;

        for (int i = 0; i < w; i++) {
            float widthPercent = (float) i / (w - 1);
            float depth = depths.get((int) (widthPercent * maxIndex));
            float depthPercent = depth / maxDepth;
            int height = (int) (h - h * depthPercent);
            for (int j = height; j < h; j++) {
                pix[j * w + i] = red;
            }
        }

        depthGraph = createImage(new MemoryImageSource(w, h, pix, 0, w));
    }

    public Point getMouseDragStart() {
        return mouseHandler.mouseDragStart;
    }

    public Point getMouseDragEnd() {
        return mouseHandler.mouseDragEnd;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (getMouseDragStart() != null && getMouseDragEnd() != null) {
            g.setColor(SELECTION);
            int start = Math.min(getMouseDragStart().x, getMouseDragEnd().x);
            int end = Math.max(getMouseDragStart().x, getMouseDragEnd().x);
            g.fillRect(start, 0, end - start, getHeight());
        }

        if (depthGraph != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(depthGraph, 0, TOP_MARGIN, null);
        }

        Playback playback;
        if ((playback = playbackPanel.getPlayback()) != null) {
            g.setColor(CURRENT_POS);
            double currentPost = playback.getCurrentPosPercent();
            g.fillRect((int) (getWidth() * currentPost) - 1, 0, 2, getHeight());
        }
    }

    public void resetPlaybackRange() {
        mouseHandler.actionPerformed(new ActionEvent(this, 0, ""));
    }

    public double getPlaybackStart() {
        if (getMouseDragStart() == null)
            return 0d;

        double start = Math.min(getMouseDragStart().getX(), getMouseDragEnd()
                .getX())
                / getWidth();
        start = start < 0 ? 0d : start;
        return start;
    }

    public double getPlaybackEnd() {
        if (getMouseDragEnd() == null)
            return 1d;

        double end = Math.max(getMouseDragStart().getX(), getMouseDragEnd()
                .getX())
                / getWidth();
        end = end > 1 ? 1d : end;
        return end;
    }

    private void updatePlaybackRange() {
        Playback playback;
        if ((playback = playbackPanel.getPlayback()) != null) {
            playback.setPlaybackStart(getPlaybackStart());
            playback.setPlaybackEnd(getPlaybackEnd());
        }
    }

    private void updateCurrentPosition() {
        Playback playback;
        if ((playback = playbackPanel.getPlayback()) != null) {
            UIGraphicsHelper.getInstance().resetConnectionsAndRepaintGraph();
            double pos = mouseHandler.mousePressCoord.getX() / getWidth();
            // Bit defensive ;)
            pos = pos < 0 ? 0 : pos;
            pos = pos > 1 ? 1 : pos;
            playback.setCurrentPos(pos);
        }
    }

    private class MouseHandler extends MouseAdapter implements ActionListener {
        private boolean dragged;
        private volatile Point mousePressCoord = new Point();
        private Point mouseDragStart;
        private Point mouseDragEnd;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (Math.abs(mousePressCoord.x - e.getPoint().x) < 3)
                return;

            dragged = true;
            mouseDragStart = mousePressCoord;
            mouseDragEnd = e.getPoint();
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!dragged) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item = new JMenuItem(
                            Messages.get("clear.selection"));
                    item.addActionListener(this);
                    menu.add(item);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                } else {
                    updateCurrentPosition();
                    repaint();
                }
            } else {
                updatePlaybackRange();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dragged = false;
            mousePressCoord = e.getPoint();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mouseDragStart = null;
            mouseDragEnd = null;
            updatePlaybackRange();
            repaint();
        }
    }

}
