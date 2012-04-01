/*
 * StructUtil.java
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

import org.objectweb.asm.Opcodes;

public class StructUtil {

    public enum Visibility {
        PUBLIC, PROTECTED, DEFAULT, PRIVATE;

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static Visibility getVisibility(int access) {
        Visibility visibility = Visibility.DEFAULT;
        if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE)
            visibility = Visibility.PRIVATE;
        else if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED)
            visibility = Visibility.PROTECTED;
        else if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC)
            visibility = Visibility.PUBLIC;

        return visibility;
    }

    public static String getLastPartOfClassName(String className) {
        int dotIndex = className.lastIndexOf('.');
        return className.substring(dotIndex == -1 ? 0 : dotIndex + 1);
    }

    public static boolean isFlagSet(int access, int flag) {
        return (access & flag) == flag;
    }

}
