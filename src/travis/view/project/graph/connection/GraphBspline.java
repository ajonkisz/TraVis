/*
 * GraphBspline.java
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

import java.awt.Point;

import travis.model.script.TraceInfo;
import travis.view.Bspline;

public class GraphBspline extends Bspline {

    private final TraceInfo callerTrace;
    private final TraceInfo calleeTrace;

    public GraphBspline(TraceInfo callerTrace, TraceInfo calleeTrace,
                        Point[] points) {
        super(points);
        this.callerTrace = callerTrace;
        this.calleeTrace = calleeTrace;
    }

    public TraceInfo getCallerTrace() {
        return callerTrace;
    }

    public TraceInfo getCalleeTrace() {
        return calleeTrace;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bspline) {
            Bspline s2 = (Bspline) obj;
            return getStartPointX() == s2.getStartPointX()
                    && getStartPointY() == s2.getStartPointY()
                    && getEndPointX() == s2.getEndPointX()
                    && getEndPointY() == s2.getEndPointY();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        int mult = 37;
        hash = hash * mult + getStartPointX();
        hash = hash * mult + getStartPointY();
        hash = hash * mult + getEndPointX();
        hash = hash * mult + getEndPointY();
        return hash;
    }

}
