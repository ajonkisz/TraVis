/*
 * ArcRectangle.java
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

package travis.view.project.graph.arcrectangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public class ArcRectangle {

    private final GeneralPath path;
    private double centerX;
    private double centerY;
    private double startAngle;
    private double degreesWidth;
    private double radius;
    private double innerRadius;
    private double height;
    private Point2D innerCenter;

    private double minWidth;
    private double maxWidth;
    private Color fillColor;

    private Arc2D outerArc;
    private Arc2D innerArc;

    public ArcRectangle(double centerX, double centerY, double radius,
                        double startAngle, double degreesWidth, double height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.startAngle = -270 - startAngle;
        this.degreesWidth = -degreesWidth;
        this.height = height;
        this.path = new GeneralPath();

        setup();
    }

    public ArcRectangle(Ellipse2D ellipse, double startAngle,
                        double degreesWidth, double height) {
        this(ellipse.getCenterX(), ellipse.getCenterY(),
                ellipse.getWidth() / 2, startAngle, degreesWidth, height);
    }

    private void setup() {
        innerRadius = radius - height;
        setupPath();
        innerCenter = calculatePointOnCircle(innerRadius, getCenterAngle());
    }

    private void setupPath() {
        path.reset();

        // Outer arc
        Ellipse2D ellipse = createEllipseForRadius(radius);
        outerArc = new Arc2D.Double(ellipse.getBounds2D(), startAngle,
                degreesWidth, Arc2D.OPEN);
        // Move to start position so inner arc can be connected
        Point2D startPointOuterArc = outerArc.getStartPoint();
        Point2D endPointOuterArc = outerArc.getEndPoint();
        path.moveTo(startPointOuterArc.getX(), startPointOuterArc.getY());
        path.append(outerArc, false);

        // Inner arc
        ellipse = createEllipseForRadius(innerRadius);
        innerArc = new Arc2D.Double(ellipse.getBounds2D(), startAngle
                + degreesWidth, -degreesWidth, Arc2D.OPEN);
        path.append(innerArc, true);
        path.closePath();

        // Set min and max width
        maxWidth = startPointOuterArc.distance(endPointOuterArc);
        minWidth = innerArc.getStartPoint().distance(innerArc.getEndPoint());
    }

    public Arc2D getOuterArc() {
        return outerArc;
    }

    public Arc2D getInnerArc() {
        return innerArc;
    }

    public boolean contains(Point2D p) {
        return path.contains(p);
    }

    public Point2D calculatePointOnCircle(double r, double angle) {
        return calculatePointOnCircle(centerX, centerY, r, angle);
    }

    public Ellipse2D createEllipseForRadius(double radius) {
        double r2 = radius * 2;
        return new Ellipse2D.Double(centerX - radius, centerY - radius, r2, r2);
    }

    public void draw(Graphics2D g2) {
        setup();
        if (fillColor != null) {
            g2.setColor(fillColor);
            g2.fill(path);
        }
        g2.setColor(Color.BLACK);
        g2.draw(path);
        // g2.setColor(Color.RED);
        // g2.fillRect((int) innerCenter.getX(), (int) innerCenter.getY(), 3,
        // 3);
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    public double getActualStartAngle() {
        return Math.abs(startAngle - 90) % 360;
    }

    public double getMinWidth() {
        return minWidth;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public Point2D getCenter() {
        double distance = radius - height / 2;
        return calculatePointOnCircle(distance, getCenterAngle());
    }

    public double getCircleCenterX() {
        return centerX;
    }

    public void setCircleCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCircleCenterY() {
        return centerY;
    }

    public void setCircleCenterY(double centerY) {
        this.centerY = centerY;
    }

    public double getCenterAngle() {
        return startAngle + degreesWidth / 2;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(double startAngle) {
        this.startAngle = startAngle;
    }

    public double getDegreesWidth() {
        return degreesWidth;
    }

    public void setDegreesWidth(double degreeWidth) {
        this.degreesWidth = degreeWidth;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getInnerRadius() {
        return innerRadius;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Point2D getInnerCenter() {
        return innerCenter;
    }

    public double getInnerCenterX() {
        return innerCenter.getX();
    }

    public double getInnerCenterY() {
        return innerCenter.getY();
    }

    public static double calculateAngle(double length, double radius) {
        return (length * 360) / (2 * Math.PI * radius);
    }

    public static Point2D calculatePointOnCircle(RectangularShape circle,
                                                 double angle) {
        double centerX = circle.getCenterX();
        double centerY = circle.getCenterY();
        double r = circle.getWidth() / 2;
        return calculatePointOnCircle(centerX, centerY, r, angle);
    }

    public static Point2D calculatePointOnCircle(double centerX,
                                                 double centerY, double r, double angle) {
        double x = centerX + r * Math.cos(Math.toRadians(angle));
        double y = centerY - r * Math.sin(Math.toRadians(angle));
        return new Point2D.Double(x, y);
    }

}
