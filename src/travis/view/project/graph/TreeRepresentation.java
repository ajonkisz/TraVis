/*
 * TreeRepresentation.java
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import travis.controller.UIHelper;
import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.project.structure.StructPackage;
import travis.view.Util;
import travis.view.project.graph.arcrectangle.ArcRectangle;
import travis.view.project.graph.arcrectangle.LabelledArcRectangle;
import travis.view.project.tree.ProjectTree;
import travis.view.project.tree.ProjectTreeNode;
import travis.view.settings.Settings;

public class TreeRepresentation {

    private static final int INNER_CIRCLES_NO = 3;
    private static final int CENTER_MIN_RADIUS = 50;

    private final Settings sets = Settings.getInstance();

    private volatile BufferedImage image;

    private final Set<ComponentData> roots;
    private ComponentData[] methods;

    private Ellipse2D methodCircle;
    private Ellipse2D latestInnerCircle;
    private Ellipse2D[] innerCircles;
    private int maxPackageDepth;

    private int maxHeight;

    public TreeRepresentation() {
        this.roots = new TreeSet<ComponentData>();
    }

    public ComponentData[] getMethods() {
        return methods == null ? new ComponentData[]{} : methods;
    }

    private void configureRepresentaion() {
        ProjectTree tree = UIHelper.getInstance().getProjectTree();

        if (!tree.isChanged())
            return;

        tree.consumeChenge();
        roots.clear();

        ProjectTreeNode root = tree.getRootNode();
        if (root == null) {
            return;
        }
        findSelectedRoots(root);

        Map<StructMethod, Integer> structMethods = tree.getStructMethods();
        if (structMethods == null) {
            methods = new ComponentData[]{};
        }
        methods = new ComponentData[structMethods.size()];
        for (ComponentData cd : roots) {
            populateRoots(cd, structMethods);
        }
    }

    private void populateRoots(ComponentData parent,
                               Map<StructMethod, Integer> structMethods) {
        ProjectTree tree = UIHelper.getInstance().getProjectTree();
        ProjectTreeNode node = parent.getNode();
        Enumeration<ProjectTreeNode> children = node.children();

        while (children.hasMoreElements()) {
            ProjectTreeNode child = children.nextElement();
            if (tree.isSelected(child)
                    && child.getUserObject().isPartOfClassPath()) {
                StructComponent comp = child.getUserObject();
                if (!sets.isDrawingClass(comp))
                    continue;
                int selectedMethods = tree.getNumberOfSelectedMethods(child);
                if (selectedMethods <= 0)
                    continue;
                ComponentData newChild = new ComponentData(child, parent,
                        selectedMethods);
                parent.addChild(newChild);
                if (child.getUserObject() instanceof StructMethod
                        && sets.isDrawingMethod(child.getUserObject())) {
                    int i = structMethods.get(child.getUserObject());
                    methods[i] = newChild;
                }
                populateRoots(newChild, structMethods);
            }
        }
    }

    private void findSelectedRoots(ProjectTreeNode root) {
        ProjectTree tree = UIHelper.getInstance().getProjectTree();
        Enumeration<ProjectTreeNode> children = root.children();

        while (children.hasMoreElements()) {
            ProjectTreeNode child = children.nextElement();
            if (tree.isSelected(child)
                    && child.getUserObject().isPartOfClassPath()) {
                roots.add(new ComponentData(child, null, tree
                        .getNumberOfSelectedMethods(child)));
            }
        }

        if (roots.isEmpty()) {
            children = root.children();
            while (children.hasMoreElements()) {
                ProjectTreeNode child = children.nextElement();
                if (tree.isSelected(child))
                    findSelectedRoots(child);
            }
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void createImage(int radius, double rotate) {
        maxHeight = radius;
        latestInnerCircle = null;

        BufferedImage newGraph = new BufferedImage(radius * 2 + 2,
                radius * 2 + 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) newGraph.getGraphics();
        g2.setRenderingHints(Util.HINTS);

        configureRepresentaion();
        if (roots.size() == 0) {
            g2.dispose();
            image = newGraph;
            return;
        }

        Ellipse2D circle = new Ellipse2D.Double(1, 1, radius * 2, radius * 2);
        setMethodCircle(circle);
        int totalMethods = 0;
        int depth = 0;
        for (ComponentData cd : roots) {
            depth = Math.max(depth, cd.getMaximumPackageDepth());
            totalMethods += cd.getTotalSelectedMethods();
        }
        if (sets.isPackageEnabled() != depth > 0)
            sets.setPackageEnabled(depth > 0);

        drawData(circle, rotate, g2, roots, 0, totalMethods);

        // Deal with radial layout and control points
        int innerCirclesNo = sets.isMinDepth() ? INNER_CIRCLES_NO - 1
                : INNER_CIRCLES_NO;
        boolean hiddenAll = depth - sets.getGraphPackageLayersToHide() <= 0;
        if (!sets.isDrawingStruct(Settings.STRUCT_PACKAGE) || hiddenAll)
            innerCirclesNo -= 1;
        createInnerCircles(innerCirclesNo);
        createInnerLayout(innerCirclesNo);

        boolean drawInnerLayout = sets.isDrawingInnerLayout();
        if (drawInnerLayout) {
            g2.setPaint(Color.YELLOW);
            for (Ellipse2D c : innerCircles) {
                g2.draw(c);
            }
            g2.setPaint(Color.BLUE);
            drawInnerLayout(g2);
            boolean drawInnerStruct = false;
            if (drawInnerStruct) {
                for (ComponentData cd : roots) {
                    drawStructInnerLayout(cd, g2);
                }
            }
        }

        g2.dispose();
        image = newGraph;
    }

    private void createInnerCircles(int innerCirclesNo) {
        innerCircles = new Ellipse2D[innerCirclesNo];
        double innerRadius = latestInnerCircle.getWidth() / 2;

        for (int i = 1; i <= innerCircles.length; i++) {
            double newRadius = innerRadius
                    * ((double) i / (innerCircles.length + 1));
            double diff = innerRadius - newRadius;
            innerCircles[i - 1] = new Ellipse2D.Double(latestInnerCircle.getX()
                    + diff, latestInnerCircle.getY() + diff, newRadius * 2,
                    newRadius * 2);
        }
    }

    private void createInnerLayout(int innerCirclesNo) {
        for (ComponentData cd : methods) {
            // This one could be skipped as the method wasn't selected.
            if (cd == null) {
                continue;
            }

            ControlPoint cpRoot = new ControlPoint((int) cd.getArcRect()
                    .getInnerCenterX(), (int) cd.getArcRect().getInnerCenterY());
            cd.setControlPoint(cpRoot);

            Point2D p;
            if (cd.getFirstVisibleParent() == null) {
                p = ArcRectangle.calculatePointOnCircle(
                        innerCircles[innerCirclesNo - 1], cd.getCenterAngle());
            } else {
                if (cd.getFirstVisibleParent().getComp() instanceof StructPackage
                        && !sets.isDrawingStruct(Settings.STRUCT_METHOD)) {
                    ComponentData parent = cd.getFirstVisibleParent();
                    Point2D p2 = ArcRectangle.calculatePointOnCircle(
                            cd.getFirstVisibleParent().getArcRect()
                                    .getInnerArc(), parent.getCenterAngle());
                    cpRoot = new ControlPoint((int) p2.getX(), (int) p2.getY());
                    cd.setControlPoint(cpRoot);
                }
                p = ArcRectangle.calculatePointOnCircle(
                        innerCircles[innerCirclesNo - 1], cd
                        .getFirstVisibleParent().getCenterAngle());
            }

            ControlPoint cp = new ControlPoint((int) p.getX(), (int) p.getY());
            cpRoot.setParentPoint(cp);

            createRemainingLevelsOfInnerLayout(innerCirclesNo - 1,
                    cd.getParent(), cp);
        }
    }

    private void createRemainingLevelsOfInnerLayout(int depth,
                                                    ComponentData cd, ControlPoint cp) {
        if (depth < 1)
            return;

        boolean cannotPassOnControl = cd.getParent() == null
                || !cd.getParent().isVisible()
                || cd.getComp() instanceof StructPackage;

        Point2D p;
        if (cannotPassOnControl) {
            p = ArcRectangle.calculatePointOnCircle(innerCircles[depth - 1],
                    cd.getCenterAngle());
        } else {
            p = ArcRectangle.calculatePointOnCircle(innerCircles[depth - 1], cd
                    .getParent().getCenterAngle());
        }
        ControlPoint cpChild = new ControlPoint((int) p.getX(), (int) p.getY());
        cp.setParentPoint(cpChild);

        if (cannotPassOnControl) {
            createRemainingLevelsOfInnerLayout(depth - 1, cd, cpChild);
        } else {
            createRemainingLevelsOfInnerLayout(depth - 1, cd.getParent(),
                    cpChild);
        }
    }

    private void drawInnerLayout(Graphics2D g2) {
        for (ComponentData cd : methods) {
            // This one could be skipped as the method wasn't selected.
            if (cd == null)
                continue;
            ControlPoint p = cd.getControlPoint();
            drawPoint(p, g2);
            drawControlPoint(p, g2);
        }
    }

    private void drawControlPoint(ControlPoint p, Graphics2D g2) {
        if (p.getParentPoint() == null)
            return;

        g2.setPaint(Color.BLUE);
        g2.drawLine((int) p.getParentPoint().getX(), (int) p.getParentPoint()
                .getY(), (int) p.getX(), (int) p.getY());
        drawPoint(p.getParentPoint(), g2);
        drawControlPoint(p.getParentPoint(), g2);
    }

    private void drawPoint(ControlPoint p, Graphics2D g2) {
        g2.setPaint(Color.MAGENTA);
        g2.fillRect((int) p.getX() - 2, (int) p.getY() - 2, 4, 4);
    }

    private void drawStructInnerLayout(ComponentData parent, Graphics2D g2) {
        Point2D center = parent.getInnerCenterPoint();
        int x1 = (int) center.getX();
        int y1 = (int) center.getY();
        for (ComponentData child : parent.getChildren()) {
            center = parent.getInnerCenterPoint();
            int x2 = (int) center.getX();
            int y2 = (int) center.getY();
            g2.setPaint(Color.BLUE);
            g2.drawLine(x1, y1, x2, y2);
            drawStructInnerLayout(child, g2);
        }
    }

    private void drawData(Ellipse2D circle, double rotate, Graphics2D g2,
                          Set<ComponentData> children, double startAngle, int totalMethods) {
        double radius = circle.getWidth() / 2;
        int i = 0;
        for (ComponentData cd : children) {
            i++;
            double gapSizeAngle = ArcRectangle.calculateAngle(cd.getGap(),
                    radius);
            double maxAngle = cd.getParent() == null ? 360d : cd.getParent()
                    .getDegreesWidth();
            double degreeWidth = (maxAngle * cd.getTotalSelectedMethods())
                    / totalMethods - gapSizeAngle;
            // If a package and not drawing a first layer use up the available
            // space to not leave a gap.
            if (i == children.size() && cd.getComp() instanceof StructPackage
                    && cd.getParent() != null && cd.getParent().isVisible())
                degreeWidth += gapSizeAngle;

            double height = determineHeight(cd, radius);

            String name = getUndotedName(cd);
            double rotateFix = cd.getParent() != null ? rotate : 0;
            LabelledArcRectangle arcRect = new LabelledArcRectangle(circle,
                    rotate + startAngle - rotateFix, degreeWidth < 0.1 ? 0.1
                    : degreeWidth, height, name);

            arcRect.setFillColor(sets.getColors().getColorForComp(cd.getComp()));
            cd.setArcRect(arcRect);
            if (cd.isVisible())
                arcRect.draw(g2);

            startAngle += degreeWidth + gapSizeAngle;

            calculateDifferenceAndDrawChildren(height, cd, radius, circle,
                    rotate, g2);
        }
    }

    private void calculateDifferenceAndDrawChildren(double height,
                                                    ComponentData cd, double radius, Ellipse2D circle, double rotate,
                                                    Graphics2D g2) {
        double diff = height + sets.getLayerGap();
        // If element is not visible do not add layer gap to maintain the
        // level how classes are drawn.
        if (!cd.isVisible())
            diff -= sets.getLayerGap();
        diff = getMaxRadiusForLength(radius, diff);
        double newRadius = radius - diff;
        Ellipse2D newCircle = new Ellipse2D.Double(circle.getX() + diff,
                circle.getY() + diff, newRadius * 2, newRadius * 2);

        if (latestInnerCircle == null
                || latestInnerCircle.getWidth() > newCircle.getWidth())
            latestInnerCircle = newCircle;

        drawData(newCircle, rotate, g2, cd.getChildren(), cd.getStartAngle(),
                cd.getTotalSelectedMethods());
    }

    private double determineHeight(ComponentData cd, double radius) {
        double height;
        if (cd.getComp() instanceof StructClass && cd.isVisible()) {
            height = radius - methodCircle.getWidth() / 2;
        } else if (cd.getComp() instanceof StructMethod && cd.isVisible()
                && !cd.getParent().isVisible()
                && sets.isDrawingStruct(Settings.STRUCT_PACKAGE)) {
            height = radius - methodCircle.getWidth() / 2;
            height += getHeightPercent(cd.getPercentHeight());
        } else {
            height = getHeightPercent(cd.getPercentHeight());
        }
        height = getMaxRadiusForLength(radius, height);
        return height;
    }

    private void setMethodCircle(Ellipse2D circle) {
        double diff = 0;

        if (sets.isDrawingStruct(Settings.STRUCT_PACKAGE)) {
            int depth = 0;
            for (ComponentData cd : roots) {
                depth = Math.max(depth, cd.getMaximumPackageDepth());
            }
            maxPackageDepth = depth;
            if (depth > 0)
                depth++;
            if (sets.isDrawingStruct(Settings.STRUCT_CLASS)
                    || sets.isDrawingStruct(Settings.STRUCT_METHOD)) {
                int min = Math.min(sets.getGraphPackageLayersToHide(),
                        maxPackageDepth + 1);
                depth -= min;
            }
            // Packages and Layers offset
            diff = depth
                    * (getHeightPercent(sets.getPackageHeightPercent()) + sets
                    .getLayerGap());
        }

        // Class offset
        if (sets.isDrawingStruct(Settings.STRUCT_CLASS)) {
            diff += getHeightPercent(sets.getClassHeightPercent());
        }

        double r = circle.getWidth() / 2 - diff;
        methodCircle = new Ellipse2D.Double(circle.getX() + diff, circle.getY()
                + diff, r * 2, r * 2);
    }

    public double getHeightPercent(int percent) {
        return maxHeight * 0.01 * percent;
    }

    private double getMaxRadiusForLength(double radius, double length) {
        return radius - CENTER_MIN_RADIUS > length ? length : radius
                - CENTER_MIN_RADIUS;
    }

    private String getUndotedName(ComponentData data) {
        String name = data.getComp().getName();
        int dotIndex = name.lastIndexOf('.');
        name = dotIndex == -1 ? name : name.substring(dotIndex + 1);
        return name;
    }

    public ComponentData getElementForCoord(Point p) {
        return getElementForCoord(p, roots);
    }

    private ComponentData getElementForCoord(Point p,
                                             Set<ComponentData> children) {
        for (ComponentData cd : children) {
            if (cd.getArcRect() != null && cd.isVisible()
                    && cd.getArcRect().contains(p))
                return cd;
        }
        for (ComponentData cd : children) {
            ComponentData c = getElementForCoord(p, cd.getChildren());
            if (c != null)
                return c;
        }
        return null;
    }

}
