/*
 * Settings.java
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

package travis.view.settings;

import java.util.Observable;

import travis.controller.UIHelper;
import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.project.structure.StructPackage;
import travis.model.project.structure.StructUtil.Visibility;
import travis.util.Messages;
import travis.view.Util;
import travis.view.color.ModifiableColor;
import travis.view.color.StructColor;

public class Settings extends Observable {

    private static final Settings INSTANCE = new Settings();

    public enum Type {
        GRAPH, GRAPH_CONNECTION, PLAYBACK_SPEED, PLAYBACK_MODE
    }

    public static final int STRUCT_PACKAGE = 1;
    public static final int STRUCT_CLASS = 1 << 1;
    public static final int STRUCT_METHOD = 1 << 2;
    public static final int STRUCT_ORDINARY_CLASS = 1 << 3;
    public static final int STRUCT_ABSTRACT_CLASS = 1 << 4;
    public static final int STRUCT_INTERFACE = 1 << 5;
    public static final int STRUCT_ENUM = 1 << 6;
    public static final int STRUCT_PUBLIC_METHOD = 1 << 7;
    public static final int STRUCT_PRIVATE_METHOD = 1 << 8;
    public static final int STRUCT_PROTECTED_METHOD = 1 << 9;
    public static final int STRUCT_DEFAULT_METHOD = 1 << 10;
    private int structsToDraw;

    private int graphRotate;
    private int graphPackageLayersToHide;
    private boolean packageEnabled;

    private int packageGap;
    private int classGap;
    private int layerGap;

    private int packageHeightPercent;
    private int classHeightPercent;
    private int methodHeightPercent;

    private double curveBundlingStrength;

    private int cachedTracesNo;
    private int maxCurvesNo;
    private int curvesPerSec;

    private boolean minDepth;
    private boolean drawingInnerLayout;

    private final ModifiableColor colors;

    private int executionPointSize;

    private boolean drawingUniqueTraces;

    private Settings() {
        structsToDraw = STRUCT_PACKAGE | STRUCT_CLASS | STRUCT_METHOD
                | STRUCT_ORDINARY_CLASS | STRUCT_ABSTRACT_CLASS
                | STRUCT_INTERFACE | STRUCT_ENUM | STRUCT_PUBLIC_METHOD
                | STRUCT_PRIVATE_METHOD | STRUCT_PROTECTED_METHOD
                | STRUCT_DEFAULT_METHOD;

        graphRotate = 0;
        graphPackageLayersToHide = 0;
        packageEnabled = true;

        packageGap = 6;
        classGap = 3;
        layerGap = 5;

        packageHeightPercent = 5;
        classHeightPercent = 10;
        methodHeightPercent = 15;

        curveBundlingStrength = 0.75;

        cachedTracesNo = 50000;
        maxCurvesNo = 100;
        curvesPerSec = 1;

        minDepth = false;
        drawingInnerLayout = false;

        colors = new ModifiableColor();

        executionPointSize = 3;

        drawingUniqueTraces = true;
    }

    public int getExecutionPointSize() {
        return executionPointSize;
    }

    public void setExecutionPointSize(int executionPointSize) {
        this.executionPointSize = executionPointSize;
        setChanged(Type.GRAPH_CONNECTION);
    }

    public ModifiableColor getColors() {
        return colors;
    }

    public void setColors(StructColor colors) {
        this.colors.setColors(colors);
        setChanged(Type.GRAPH);
    }

    public boolean isDrawingClass(StructComponent comp) {
        if (!(comp instanceof StructClass)) return true;

        if (comp.isOrdinaryClass()
                && isDrawingStruct(STRUCT_ORDINARY_CLASS)) {
            return true;
        }
        else if (comp.isInterface() && isDrawingStruct(STRUCT_INTERFACE)) {
            return true;
        }
        // This extra check must be here as according to ASM and interface
        // is also an abstract class.
        else if (comp.isInterface() && !isDrawingStruct(STRUCT_INTERFACE)) {
            return false;
        }
        else if (comp.isAbstract() && isDrawingStruct(STRUCT_ABSTRACT_CLASS)) {
            return true;
        }
        else return comp.isEnum() && isDrawingStruct(STRUCT_ENUM);
    }

    public boolean isDrawingMethod(StructComponent comp) {
        if (!(comp instanceof StructMethod)) return true;

        if (comp.getVisibility() == Visibility.PUBLIC
                && isDrawingStruct(STRUCT_PUBLIC_METHOD)) {
            return true;
        }
        else if (comp.getVisibility() == Visibility.PRIVATE
                && isDrawingStruct(STRUCT_PRIVATE_METHOD)) {
            return true;
        }
        else if (comp.getVisibility() == Visibility.PROTECTED
                && isDrawingStruct(STRUCT_PROTECTED_METHOD)) {
            return true;
        }
        else return comp.getVisibility() == Visibility.DEFAULT
                && isDrawingStruct(STRUCT_DEFAULT_METHOD);
    }

    public boolean isDrawingStruct(StructComponent comp) {
        if (comp instanceof StructPackage) {
            return isDrawingStruct(STRUCT_PACKAGE);
        } else if (comp instanceof StructClass) {
            if (!isDrawingStruct(STRUCT_CLASS)) {
                return false;
            }
            if (comp.isOrdinaryClass()
                    && isDrawingStruct(STRUCT_ORDINARY_CLASS)) {
                return true;
            }
            if (comp.isAbstract() && isDrawingStruct(STRUCT_ABSTRACT_CLASS)) {
                return true;
            }
            if (comp.isInterface() && isDrawingStruct(STRUCT_INTERFACE)) {
                return true;
            }
            return comp.isEnum() && isDrawingStruct(STRUCT_ENUM);
        } else if (comp instanceof StructMethod) {
            if (!isDrawingStruct(STRUCT_METHOD)) {
                return false;
            }
            if (comp.getVisibility() == Visibility.PUBLIC
                    && isDrawingStruct(STRUCT_PUBLIC_METHOD)) {
                return true;
            }
            if (comp.getVisibility() == Visibility.PRIVATE
                    && isDrawingStruct(STRUCT_PRIVATE_METHOD)) {
                return true;
            }
            if (comp.getVisibility() == Visibility.PROTECTED
                    && isDrawingStruct(STRUCT_PROTECTED_METHOD)) {
                return true;
            }
            return comp.getVisibility() == Visibility.DEFAULT
                    && isDrawingStruct(STRUCT_DEFAULT_METHOD);
        } else {
            return true;
        }
    }

    public boolean isDrawingStruct(int struct) {
        return Util.containsFlag(structsToDraw, struct);
    }

    public boolean isMinDepth() {
        return minDepth;
    }

    public void setMinDepth(boolean minDepth) {
        this.minDepth = minDepth;
        setChanged(Type.GRAPH);
    }

    public boolean isDrawingInnerLayout() {
        return drawingInnerLayout;
    }

    public void setDrawingInnerLayout(boolean drawInnerLayout) {
        this.drawingInnerLayout = drawInnerLayout;
        setChanged(Type.GRAPH);
    }

    public int getMaxCurvesNo() {
        return maxCurvesNo;
    }

    public void setMaxCurvesNo(int maxCurvesNo) {
        if (maxCurvesNo < 1)
            maxCurvesNo = 1;
        this.maxCurvesNo = maxCurvesNo;
        setChanged(Type.GRAPH_CONNECTION);
    }

    public boolean isDrawingUniqueTraces() {
        return drawingUniqueTraces;
    }

    public void setDrawingUniqueTraces(boolean drawingUniqueTraces) {
        this.drawingUniqueTraces = drawingUniqueTraces;
        setChanged(Type.GRAPH_CONNECTION);
    }

    public int getCurvesPerSec() {
        return curvesPerSec;
    }

    public void setCurvesPerSec(int curvesPerSec) {
        if (curvesPerSec < 1)
            curvesPerSec = 1;
        this.curvesPerSec = curvesPerSec;
        setChanged(Type.PLAYBACK_SPEED);
    }

    public int getCachedTracesNo() {
        return cachedTracesNo;
    }

    public void setCachedTracesNo(int cachedTracesNo) {
        this.cachedTracesNo = cachedTracesNo;
        setChanged(Type.GRAPH_CONNECTION);
    }

    public double getCurveBundlingStrength() {
        return curveBundlingStrength;
    }

    public void setCurveBundlingStrength(double curveBundlingStrength) {
        if (curveBundlingStrength < 0 || curveBundlingStrength > 1)
            throw new IllegalArgumentException(
                    Messages.get("bundling.strength.exception"));
        this.curveBundlingStrength = curveBundlingStrength;
        setChanged(Type.GRAPH_CONNECTION);
    }

    public static Settings getInstance() {
        return INSTANCE;
    }

    public void setChanged(Type type) {
        setChanged();
        notifyObservers(type);
    }

    public void drawStruct(int struct, boolean enabled) {
        int oldStruct = structsToDraw;
        structsToDraw = Util.toggleFlag(structsToDraw, struct, enabled);
        if (oldStruct != structsToDraw) {
            UIHelper.getInstance().getProjectTree().setChanged(true);
            setChanged(Type.GRAPH);
            setChanged(Type.PLAYBACK_MODE);
        }
    }

    public int getGraphRotate() {
        return graphRotate;
    }

    public void setGraphRotate(int graphRotate) {
        this.graphRotate = graphRotate;
        setChanged(Type.GRAPH);
    }

    public int getGraphPackageLayersToHide() {
        return graphPackageLayersToHide;
    }

    public void setGraphPackageLayersToHide(int graphLayersToHide) {
        this.graphPackageLayersToHide = graphLayersToHide;
        setChanged(Type.GRAPH);
    }

    public boolean isPackageEnabled() {
        return packageEnabled;
    }

    public void setPackageEnabled(boolean packageEnabled) {
        this.packageEnabled = packageEnabled;
        setChanged(Type.GRAPH);
    }

    public int getPackageGap() {
        return packageGap;
    }

    public void setPackageGap(int packageGap) {
        this.packageGap = packageGap;
        setChanged(Type.GRAPH);
    }

    public int getClassGap() {
        return classGap;
    }

    public void setClassGap(int classGap) {
        this.classGap = classGap;
        setChanged(Type.GRAPH);
    }

    public int getLayerGap() {
        return layerGap;
    }

    public void setLayerGap(int layerGap) {
        this.layerGap = layerGap;
        setChanged(Type.GRAPH);
    }

    public int getPackageHeightPercent() {
        return packageHeightPercent;
    }

    public void setPackageHeightPercent(int packageHeightPercent) {
        this.packageHeightPercent = packageHeightPercent;
        setChanged(Type.GRAPH);
    }

    public int getClassHeightPercent() {
        return classHeightPercent;
    }

    public void setClassHeightPercent(int classHeightPercent) {
        this.classHeightPercent = classHeightPercent;
        setChanged(Type.GRAPH);
    }

    public int getMethodHeightPercent() {
        return methodHeightPercent;
    }

    public void setMethodHeightPercent(int methodHeightPercent) {
        this.methodHeightPercent = methodHeightPercent;
        setChanged(Type.GRAPH);
    }

}
