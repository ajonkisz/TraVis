/*
 * ExecutionPoint.java
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
import java.awt.geom.Ellipse2D;

import travis.model.script.TraceInfo;
import travis.view.project.graph.ComponentData;
import travis.view.settings.Settings;

public class ExecutionPoint {

    private Point center;
    private TraceInfo trace;
    private ComponentData cd;
    private final Ellipse2D oval;

    public ExecutionPoint() {
        this.oval = new Ellipse2D.Double();
    }

    public ExecutionPoint(Point center) {
        this.center = center;
        this.oval = new Ellipse2D.Double();
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public void draw(Graphics2D g2) {
        if (center == null)
            return;

        int size = Settings.getInstance().getExecutionPointSize();
        g2.setColor(Settings.getInstance().getColors().getExecutionPointColor());
        oval.setFrame(center.x - size, center.y - size, size * 2, size * 2);
        g2.fill(oval);
    }

    public boolean contains(Point p) {
        return oval.contains(p);
    }

    public ComponentData getComponentData() {
        return cd;
    }

    public void setComponentData(ComponentData cd) {
        this.cd = cd;
    }

    public TraceInfo getTrace() {
        return trace;
    }

    public void setTrace(TraceInfo trace) {
        this.trace = trace;
    }

}
