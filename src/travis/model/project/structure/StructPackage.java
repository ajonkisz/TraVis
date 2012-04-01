/*
 * StructPackage.java
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

package travis.model.project.structure;

import travis.model.project.StructStub;
import travis.util.Messages;

public class StructPackage extends StructComponent {

    private static final long serialVersionUID = -1589126374176458284L;

    public StructPackage(String name, StructComponent parent) {
        super(name, parent);
    }

    public StructPackage(StructComponent parent, StructStub stub) {
        super(parent, stub);
        super.isPartOfClassPath = State.YES;
    }

    @Override
    public String getName() {
        if (isDefaultPackage())
            return "";
        else
            return super.getName();
    }

    @Override
    public boolean isDefaultPackage() {
        return super.getName().equals(Messages.get("default.package"));
    }

    @Override
    public boolean addStructComponent(StructComponent sComponent) {
        if (sComponent instanceof StructMethod) {
            throw new IllegalArgumentException(
                    Messages.get("spackage.component.exception"));
        }
        return super.addStructComponent(sComponent);
    }

}
