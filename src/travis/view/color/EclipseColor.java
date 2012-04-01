/*
 * EclipseColor.java
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

public class EclipseColor extends StructColor {

    private static final Color PACKAGE_COLOR = new Color(0xFF, 0xCC, 0x99);

    private static final Color ORDINARY_CLASS_COLOR = new Color(0x33, 0x66,
            0x33);
    private static final Color ABSTRACT_CLASS_COLOR = new Color(0x66, 0x99,
            0x66);
    private static final Color INTERFACE_COLOR = new Color(0x66, 0x66, 0xCC);
    private static final Color ENUM_COLOR = new Color(0x99, 0x66, 0x33);

    private static final Color PUBLIC_METHOD_COLOR = new Color(0x66, 0x99, 0x66);
    private static final Color PRIVATE_METHOD_COLOR = new Color(0xFF, 0x66,
            0x66);
    private static final Color PROTECTED_METHOD_COLOR = new Color(0xFF, 0xCC,
            0x66);
    private static final Color DEFAULT_METHOD_COLOR = new Color(0x33, 0x66,
            0x99);

    private static final Color DEFAULT_EXECUTION_COLOR = Color.MAGENTA;

    @Override
    public Color getPackageColor() {
        return PACKAGE_COLOR;
    }

    @Override
    public Color getOrdinaryClassColor() {
        return ORDINARY_CLASS_COLOR;
    }

    @Override
    public Color getAbstractClassColor() {
        return ABSTRACT_CLASS_COLOR;
    }

    @Override
    public Color getInterfaceColor() {
        return INTERFACE_COLOR;
    }

    @Override
    public Color getEnumColor() {
        return ENUM_COLOR;
    }

    @Override
    public Color getPublicMethodColor() {
        return PUBLIC_METHOD_COLOR;
    }

    @Override
    public Color getPrivateMethodColor() {
        return PRIVATE_METHOD_COLOR;
    }

    @Override
    public Color getProtectedMethodColor() {
        return PROTECTED_METHOD_COLOR;
    }

    @Override
    public Color getDefaultMethodColor() {
        return DEFAULT_METHOD_COLOR;
    }

    @Override
    public Color getExecutionPointColor() {
        return DEFAULT_EXECUTION_COLOR;
    }

}
