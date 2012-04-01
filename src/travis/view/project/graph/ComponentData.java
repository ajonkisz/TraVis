/*
 * ComponentData.java
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

import java.awt.geom.Point2D;
import java.util.Set;
import java.util.TreeSet;

import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructPackage;
import travis.view.project.graph.arcrectangle.ArcRectangle;
import travis.view.project.tree.ProjectTreeNode;
import travis.view.settings.Settings;

public class ComponentData implements Comparable<ComponentData> {

    private final Settings sets = Settings.getInstance();

    private final ProjectTreeNode node;
    private final StructComponent comp;
    private final ComponentData parent;
    private final Set<ComponentData> children;
    private final int totalSelectedMethods;

    private ArcRectangle arcRect;
    private double startAngle;
    private double degreesWidth;

    // This point is used for inner radial layout
    private ControlPoint controlPoint;

    public ComponentData(ProjectTreeNode node, ComponentData parent,
                         int totalSelectedMethods) {
        this.node = node;
        this.parent = parent;
        this.totalSelectedMethods = totalSelectedMethods;
        comp = node.getUserObject();
        children = new TreeSet<ComponentData>();
    }

    public int getMaximumPackageDepth() {
        int max = 0;
        boolean morePackages = false;
        for (ComponentData child : children) {
            if (child.getComp() instanceof StructPackage) {
                morePackages = true;
                max = Math.max(max, child.getMaximumPackageDepth());
            }
        }
        if (!morePackages)
            return max = Math.max(max, getDepth());
        return max;
    }

    public int getDepth() {
        if (parent == null)
            return 0;
        return 1 + parent.getDepth();
    }

    public int getMaxDepth() {
        int max = 1;
        for (ComponentData child : children) {
            max = Math.max(max, child.getMaxDepth() + 1);
        }
        return max;
    }

    public boolean addChild(ComponentData child) {
        return children.add(child);
    }

    public boolean removeChild(ComponentData child) {
        return children.remove(child);
    }

    public Set<ComponentData> getChildren() {
        return children;
    }

    public int getTotalSelectedMethods() {
        return totalSelectedMethods;
    }

    public ProjectTreeNode getNode() {
        return node;
    }

    public StructComponent getComp() {
        return comp;
    }

    public ComponentData getFirstVisibleParent() {
        if (parent == null)
            return null;
        return parent.isVisible() ? parent : parent.getFirstVisibleParent();
    }

    public ComponentData getParent() {
        return parent;
    }

    public ControlPoint getControlPoint() {
        return controlPoint;
    }

    public void setControlPoint(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
    }

    /**
     * @return ArcRectangle belonging to this ComponentData or if not visible
     *         then return ArcRectangle of a parent.
     */
    public ArcRectangle getArcRect() {
        return isVisible() ? arcRect : (parent == null ? null : parent
                .getArcRect());
    }

    public double getCenterAngleForPackage() {
        return arcRect.getCenterAngle();
    }

    public double getCenterAngle() {
        if (comp instanceof StructPackage && comp.containsAnyClasses()) {
            return calculateCenterAngleForPackageWithClasses();
        }

        return arcRect.getCenterAngle();
    }

    private double calculateCenterAngleForPackageWithClasses() {
        double startAngle = 0;
        double endAngle = 0;

        boolean first = true;
        for (ComponentData child : children) {
            if (child.comp instanceof StructClass) {
                if (first) {
                    startAngle = child.arcRect.getStartAngle();
                    first = false;
                }
                endAngle = child.arcRect.getStartAngle()
                        + child.arcRect.getDegreesWidth();
            }
        }

        double diff = (endAngle - startAngle) / 2;
        return startAngle + diff;
    }

    public Point2D getInnerCenterPoint() {
        return arcRect.getInnerCenter();
    }

    public void setArcRect(ArcRectangle arcRect) {
        this.arcRect = arcRect;
        startAngle = arcRect.getActualStartAngle();
        degreesWidth = -arcRect.getDegreesWidth();
    }

    public double getStartAngle() {
        return startAngle;
    }

    public double getDegreesWidth() {
        return degreesWidth;
    }

    public boolean isVisible() {
        if (comp instanceof StructPackage) {
            if (getDepth() < sets.getGraphPackageLayersToHide()
                    && (sets.isDrawingStruct(Settings.STRUCT_CLASS) || sets
                    .isDrawingStruct(Settings.STRUCT_METHOD)))
                return false;
            else
                return sets.isDrawingStruct(Settings.STRUCT_PACKAGE);
        }
        return sets.isDrawingStruct(comp);
    }

    public int getPercentHeight() {
        if (comp instanceof StructPackage) {
            return isVisible() ? sets.getPackageHeightPercent() : 0;
        } else if (comp instanceof StructClass) {
            return isVisible() ? sets.getClassHeightPercent() : 0;
        } else {
            return isVisible() ? sets.getMethodHeightPercent() : 0;
        }
    }

    public int getGap() {
        if (comp instanceof StructPackage) {
            return isVisible() ? sets.getPackageGap() : 0;
        } else if (comp instanceof StructClass) {
            return isVisible() ? sets.getClassGap() : 0;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComponentData) {
            return compareTo((ComponentData) obj) == 0 ? true : false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public int compareTo(ComponentData o) {
        return node.compareTo(o.getNode());
    }

}
