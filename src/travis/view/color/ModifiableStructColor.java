/*
 * ModifiableStructColor.java
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

public abstract class ModifiableStructColor extends StructColor {

    public static final ModifiableStructColor getModifiableColor() {
        return new ModifiableColor();
    }

    public static final ModifiableStructColor getModifiableColor(
            StructColor color) {
        return new ModifiableColor(color);
    }

    public void setColors(StructColor color) {
        setPackageColor(color.getPackageColor());
        setOrdinaryClassColor(color.getOrdinaryClassColor());
        setAbstractClassColor(color.getAbstractClassColor());
        setInterfaceColor(color.getInterfaceColor());
        setEnumColor(color.getEnumColor());
        setPublicMethodColor(color.getPublicMethodColor());
        setPrivateMethodColor(color.getPrivateMethodColor());
        setProtectedMethodColor(color.getProtectedMethodColor());
        setDefaultMethodColor(color.getDefaultMethodColor());
        setExecutionPointColor(color.getExecutionPointColor());
    }

    public abstract void setPackageColor(Color packageColor);

    public abstract void setOrdinaryClassColor(Color ordinaryClassColor);

    public abstract void setAbstractClassColor(Color abstractClassColor);

    public abstract void setInterfaceColor(Color interfaceColor);

    public abstract void setEnumColor(Color enumColor);

    public abstract void setPublicMethodColor(Color publicMethodColor);

    public abstract void setPrivateMethodColor(Color privateMethodColor);

    public abstract void setProtectedMethodColor(Color protectedMethodColor);

    public abstract void setDefaultMethodColor(Color defaultMethodColor);

    public abstract void setExecutionPointColor(Color executionPointColor);

}