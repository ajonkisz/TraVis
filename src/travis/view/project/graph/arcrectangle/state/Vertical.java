/*
 * Vertical.java
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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import travis.view.TextStroke;
import travis.view.project.graph.arcrectangle.LabelledArcRectangle;

public class Vertical extends TextOrientationState {

    private final Line2D textLine;

    protected Vertical(LabelledArcRectangle rect, Font font) {
        super(rect, font);
        textLine = createLine();
    }

    private Line2D createLine() {
        double innerTheta = getFontCenterAngle(rect.getInnerRadius());
        double outerTheta = getFontCenterAngle(rect.getRadius());
        Point2D pInner = rect.calculatePointOnCircle(rect.getInnerRadius()
                + MARGIN, rect.getCenterAngle() + innerTheta);
        Point2D pOuter = rect.calculatePointOnCircle(rect.getRadius() - MARGIN,
                rect.getStartAngle() + rect.getDegreesWidth() / 2 + outerTheta);
        if (isFlipped()) {
            return new Line2D.Double(pOuter, pInner);
        } else {
            return new Line2D.Double(pInner, pOuter);
        }
    }

    private double getFontCenterAngle(double angle) {
        double theta = calculateAngle(fontCenterOffset, angle) / 2;
        return isFlipped() ? theta : -theta;
    }

    @Override
    protected boolean isFlipped() {
        return getActualAngle() > 180;
    }

    @Override
    protected double getPathLength() {
        if (rect.getHeight() < MARGIN * 2)
            return 0;
        return textLine.getP1().distance(textLine.getP2());
    }

    @Override
    protected double getAvailableHeight() {
        Ellipse2D textEllipse = rect
                .createEllipseForRadius(getRadiusToCenter());
        Arc2D arc = new Arc2D.Double(textEllipse.getBounds2D(),
                rect.getStartAngle(), rect.getDegreesWidth(), Arc2D.OPEN);

        return TextStroke.measurePathLength(arc);
    }

    @Override
    protected Shape getCenterAlignedShape() {
        AffineTransform at = new AffineTransform();

        double r = getRadiusToCenter();
        Point2D start = rect.calculatePointOnCircle(rect.getInnerRadius()
                + MARGIN, rect.getCenterAngle());
        Point2D center = rect.calculatePointOnCircle(r, rect.getCenterAngle());

        double tx = center.getX() - start.getX();
        double ty = center.getY() - start.getY();
        if (isFlipped())
            at.translate(-tx, -ty);
        else
            at.translate(tx, ty);

        return at.createTransformedShape(textLine);
    }

    private double getRadiusToCenter() {
        Point2D start = isFlipped() ? textLine.getP2() : textLine.getP1();
        double freeSpace = (getPathLength() - textWidth) / 2;
        return start.distance(rect.getCircleCenterX(), rect.getCircleCenterY())
                + freeSpace;
    }

    @Override
    protected Shape getShape() {
        return textLine;
    }

}
