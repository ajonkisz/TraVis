/*
 * StructColor.java
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

package travis.view.color;

import java.awt.Color;

import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.project.structure.StructPackage;
import travis.model.project.structure.StructUtil.Visibility;

public abstract class StructColor {

    public Color getColorForComp(StructComponent comp) {
        Color color = Color.WHITE;

        if (comp instanceof StructPackage)
            color = getPackageColor();

        if (comp instanceof StructClass) {
            if (comp.isOrdinaryClass())
                color = getOrdinaryClassColor();
            if (comp.isAbstract())
                color = getAbstractClassColor();
            if (comp.isInterface())
                color = getInterfaceColor();
            if (comp.isEnum())
                color = getEnumColor();
        }

        if (comp instanceof StructMethod) {
            if (comp.getVisibility() == Visibility.PUBLIC)
                color = getPublicMethodColor();
            if (comp.getVisibility() == Visibility.PRIVATE)
                color = getPrivateMethodColor();
            if (comp.getVisibility() == Visibility.PROTECTED)
                color = getProtectedMethodColor();
            if (comp.getVisibility() == Visibility.DEFAULT)
                color = getDefaultMethodColor();
        }

        return color;
    }

    public abstract Color getPackageColor();

    public abstract Color getOrdinaryClassColor();

    public abstract Color getAbstractClassColor();

    public abstract Color getInterfaceColor();

    public abstract Color getEnumColor();

    public abstract Color getPublicMethodColor();

    public abstract Color getPrivateMethodColor();

    public abstract Color getProtectedMethodColor();

    public abstract Color getDefaultMethodColor();

    public abstract Color getExecutionPointColor();

}