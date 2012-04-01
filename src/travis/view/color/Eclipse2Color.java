/*
 * Eclipse2Color.java
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

public class Eclipse2Color extends StructColor {

    private static final Color PACKAGE_COLOR = new Color(237, 209, 166);

    private static final Color ORDINARY_CLASS_COLOR = new Color(51, 130, 77);
    private static final Color ABSTRACT_CLASS_COLOR = new Color(97, 160, 103);
    private static final Color INTERFACE_COLOR = new Color(89, 71, 158);
    private static final Color ENUM_COLOR = new Color(146, 97, 43);

    private static final Color PUBLIC_METHOD_COLOR = new Color(3, 128, 72);
    private static final Color PRIVATE_METHOD_COLOR = new Color(200, 25, 42);
    private static final Color PROTECTED_METHOD_COLOR = new Color(254, 207, 108);
    private static final Color DEFAULT_METHOD_COLOR = new Color(16, 92, 156);

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
