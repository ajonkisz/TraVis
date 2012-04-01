/*
 * ConnectionPainter.java
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

package travis.view.project.graph.connection;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import travis.controller.UIHelper;
import travis.model.script.TraceInfo;
import travis.view.Util;
import travis.view.project.graph.ComponentData;
import travis.view.project.graph.ControlPoint;
import travis.view.project.graph.TreeRepresentation;
import travis.view.settings.Settings;

public class ConnectionPainter {

    private static final float MIN_ALPHA = 0.1f;
    private static final float MAX_ALPHA = 0.6f;

    private final TreeRepresentation treeRep;
    private final LinkedBlockingDeque<TraceInfo> traces;
    private Collection<GraphBspline> oldSplines;
    private volatile ExecutionPoint execPoint;

    private volatile BufferedImage image;
    private volatile boolean needRepaint;

    public ConnectionPainter(TreeRepresentation treeRep) {
        this.treeRep = treeRep;
        traces = new LinkedBlockingDeque<TraceInfo>();
        oldSplines = new LinkedHashSet<GraphBspline>();
        needRepaint = true;
    }

    public ExecutionPoint getExecutionPoint() {
        return execPoint;
    }

    public void reset() {
        traces.clear();
        oldSplines.clear();
        needRepaint = true;
    }

    public void setNeedRepaint(boolean needRepaint) {
        this.needRepaint = needRepaint;
    }

    public Collection<GraphBspline> getSplines() {
        return oldSplines;
    }

    public void lineTo(TraceInfo trace) {
        needRepaint = true;
        traces.addFirst(trace);
        while (traces.size() > Settings.getInstance().getCachedTracesNo()) {
            traces.removeLast();
        }
    }

    public void createConnections(ControlPoint cpStart,
                                  TraceInfo previousTrace, Iterator<TraceInfo> it,
                                  ConnectionData data, boolean isFirst) {
        for (; it.hasNext(); ) {
            TraceInfo trace = it.next();
            ComponentData cd = treeRep.getMethods()[trace.getMethodId()];

            // cd (method) is null only when not selected / visible
            if (cd == null)
                continue;
            data.finishedOnReturn = true;
            if (isFirst) {
                cpStart = null;
                previousTrace = null;
            }

            if (trace.isReturnCall()) {
                if (cpStart == null) {
                    createConnections(null, null, it, data, false);
                    continue;
                } else {
                    return;
                }
            } else {
                ControlPoint cpEnd = cd.getControlPoint();
                data.ep.setCenter(cpEnd);
                data.ep.setTrace(trace);
                data.ep.setComponentData(cd);
                if (cpStart == null) {
                    cpStart = cpEnd;
                    previousTrace = trace;
                    createConnections(cpStart, trace, it, data, false);
                    continue;
                } else {
                    // The commented out part causes graph to not be drawn correctly
                    // TODO All threads should be drawn separately from a different collection
//					if (previousTrace.getThreadId() != trace.getThreadId())
//						continue;

                    data.finishedOnReturn = false;
                    Point[] path = cpStart.getPathTo(cpEnd);
                    // TODO Deal with recursive calls (path.length == 1).
                    GraphBspline spline = new GraphBspline(previousTrace,
                            trace, path);
                    data.addSpline(spline);
                    createConnections(cpEnd, trace, it, data, false);
                    continue;
                }
            }
        }
    }

    public BufferedImage getImage(int width, int height) {
        if (!needRepaint)
            return image;

        needRepaint = false;
        BufferedImage img = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHints(Util.HINTS);

        ConnectionData data = new ConnectionData();
        populateSplines(data);

        int size = Math.min(data.splines.size(), Settings.getInstance()
                .getMaxCurvesNo());

        int counter = data.splines.size();
        Set<GraphBspline> trimmedSplines = new LinkedHashSet<GraphBspline>(size);
        for (GraphBspline spline : data.splines) {
            // Skip splines that are outside max curve limit.
            if (counter > size) {
                counter--;
                continue;
            }
            if (!data.ignoreOldTraces)
                trimmedSplines.remove(spline);
            trimmedSplines.add(spline);
        }

        size = Math.min(trimmedSplines.size(), Settings.getInstance()
                .getMaxCurvesNo());
        int power = 1;
        int sizePowered = Util.pow(size, power);
        int i = 1;
        for (GraphBspline spline : trimmedSplines) {
            if (i == size) {
                if (data.finishedOnReturn)
                    spline.draw(g2, MAX_ALPHA);
                else
                    spline.draw(g2, 1f);
            } else {
                int iPowered = Util.pow(++i, power);
                float alpha = MIN_ALPHA + ((float) iPowered / sizePowered)
                        * (MAX_ALPHA - MIN_ALPHA);
                spline.draw(g2, alpha);
            }
        }

        data.ep.draw(g2);
        execPoint = data.ep;

        displayExecPointTooltip(data.ep);

        g2.dispose();
        image = img;
        return image;
    }

    private void displayExecPointTooltip(ExecutionPoint ep) {
        UIHelper.getInstance().getTooltip().displayExecutionPointTooltip(ep);
    }

    private void populateSplines(ConnectionData data) {
        Iterator<TraceInfo> it;
        Iterator<TraceInfo> it2;
        synchronized (traces) {
            if (traces.isEmpty()) {
                oldSplines = data.splines;
            }

            it = traces.descendingIterator();
            it2 = traces.iterator();
        }

        createConnections(null, null, it, data, true);
        if (data.finishedOnReturn)
            moveExecutionToReturn(it2, data);

        this.oldSplines = data.splines;
    }

    private void moveExecutionToReturn(Iterator<TraceInfo> it,
                                       ConnectionData data) {
        int returns = 0;
        for (; it.hasNext(); ) {
            TraceInfo trace = it.next();
            ComponentData cd = treeRep.getMethods()[trace.getMethodId()];
            if (cd == null) {
                continue;
            }
            if (!trace.isReturnCall()) {
                if (returns != 0) {
                    returns--;
                    continue;
                }
                data.ep.setCenter(cd.getControlPoint());
                data.ep.setTrace(trace);
                data.ep.setComponentData(cd);
                return;
            } else {
                returns++;
            }
        }
        // Could only be achieved if didn't find a point
        data.ep.setCenter(null);
        data.ep.setTrace(null);
        data.ep.setComponentData(null);
    }

    private class ConnectionData {
        private Collection<GraphBspline> splines;
        private boolean finishedOnReturn;
        private ExecutionPoint ep;
        private final boolean ignoreOldTraces;

        public ConnectionData() {
            ignoreOldTraces = Settings.getInstance().isDrawingUniqueTraces();
            if (ignoreOldTraces) {
                splines = new LinkedHashSet<GraphBspline>();
            } else {
                splines = new LinkedList<GraphBspline>();
            }
            finishedOnReturn = false;
            ep = new ExecutionPoint();
        }

        public void addSpline(GraphBspline spline) {
            if (ignoreOldTraces) {
                splines.remove(spline);
            }
            splines.add(spline);
        }
    }

}
