/*
 * Horizontal.java
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

package travis.view.project.graph.arcrectangle.state;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import travis.view.TextStroke;
import travis.view.project.graph.arcrectangle.LabelledArcRectangle;

public class Horizontal extends TextOrientationState {

    private final Arc2D textArc;

    protected Horizontal(LabelledArcRectangle rect, Font font) {
        super(rect, font);
        textArc = createArc();
    }

    private Arc2D createArc() {
        double textEllipseRadius = getTextEllipseRadius();
        Ellipse2D textEllipse = rect.createEllipseForRadius(textEllipseRadius);
        double offset = calculateAngle(MARGIN, textEllipseRadius);
        if (isFlipped()) {
            return new Arc2D.Double(textEllipse.getBounds2D(),
                    rect.getStartAngle() + rect.getDegreesWidth() + offset,
                    -rect.getDegreesWidth() - offset * 2, Arc2D.OPEN);
        } else {
            return new Arc2D.Double(textEllipse.getBounds2D(),
                    rect.getStartAngle() - offset, rect.getDegreesWidth()
                    + offset * 2, Arc2D.OPEN);
        }
    }

    private double getTextEllipseRadius() {
        double r = rect.getRadius() - rect.getHeight() / 2;
        r = isFlipped() ? r + fontCenterOffset : r - fontCenterOffset;
        return r;
    }

    @Override
    protected boolean isFlipped() {
        double angle = getActualAngle();
        return angle > 90 && angle < 270;
    }

    @Override
    protected double getPathLength() {
        float length = TextStroke.measurePathLength(textArc);
        if (length < MARGIN * 2)
            return 0;
        return length;
    }

    @Override
    protected double getAvailableHeight() {
        return rect.getHeight();
    }

    @Override
    protected Shape getCenterAlignedShape() {
        double r = getTextEllipseRadius();
        double theta = calculateAngle(textWidth, r);
        theta = (calculateAngle(getPathLength(), r) - theta) / 2;
        theta = isFlipped() ? -theta : theta;

        AffineTransform at = AffineTransform.getRotateInstance(
                Math.toRadians(theta), rect.getCircleCenterX(), rect.getCircleCenterY());
        return at.createTransformedShape(textArc);
    }

    @Override
    protected Shape getShape() {
        return textArc;
    }

}
