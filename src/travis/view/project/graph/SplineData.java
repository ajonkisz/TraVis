/*
 * SplineData.java
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

import travis.model.script.TraceInfo;
import travis.view.project.graph.connection.GraphBspline;

public class SplineData {

    private final GraphBspline spline;
    private final ComponentData src;
    private final TraceInfo srcTrace;
    private final ComponentData dest;
    private final TraceInfo destTrace;
    private final double distance;

    public SplineData(GraphBspline spline, ComponentData src,
                      TraceInfo srcTrace, ComponentData dest, TraceInfo destTrace,
                      double distance) {
        this.spline = spline;
        this.src = src;
        this.srcTrace = srcTrace;
        this.dest = dest;
        this.destTrace = destTrace;
        this.distance = distance;
    }

    public GraphBspline getSpline() {
        return spline;
    }

    public ComponentData getSrc() {
        return src;
    }

    public TraceInfo getSrcTrace() {
        return srcTrace;
    }

    public ComponentData getDest() {
        return dest;
    }

    public TraceInfo getDestTrace() {
        return destTrace;
    }

    public double getDistance() {
        return distance;
    }

}
