/*
 * StructStub.java
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

package travis.model.project;

import org.objectweb.asm.Opcodes;

public class StructStub {

    public enum Visibility {
        DEFAULT(0), PUBLIC(Opcodes.ACC_PUBLIC), PRIVATE(Opcodes.ACC_PRIVATE), PROTECTED(
                Opcodes.ACC_PROTECTED);

        private final int opcode;

        private Visibility(int opcode) {
            this.opcode = opcode;
        }

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum Modifier {
        NONE(0), FINAL(Opcodes.ACC_FINAL), ABSTRACT(Opcodes.ACC_ABSTRACT), STATIC(
                Opcodes.ACC_STATIC);

        private final int opcode;

        private Modifier(int opcode) {
            this.opcode = opcode;
        }

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public enum Inheritance {
        NONE(0), INTERFACE(Opcodes.ACC_INTERFACE), ABSTRACT(
                Opcodes.ACC_ABSTRACT), FINAL(Opcodes.ACC_FINAL), ENUM(
                Opcodes.ACC_ENUM);

        private final int opcode;

        private Inheritance(int opcode) {
            this.opcode = opcode;
        }

        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private String name;
    private String descriptor;
    private int accessFlag;

    public StructStub() {
        this("");
    }

    public StructStub(String name) {
        this.name = name;
        this.descriptor = "";
    }

    public void setVisibility(Visibility visibility) {
        accessFlag |= visibility.opcode;
    }

    public void setModifier(Modifier modifier) {
        accessFlag |= modifier.opcode;
    }

    public void setInheritance(Inheritance inheritance) {
        accessFlag |= inheritance.opcode;
    }

    public int getAccessFlag() {
        return accessFlag;
    }

    public void setAccessFlag(int accessFlag) {
        this.accessFlag = accessFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor.replace('.', '/');
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

}
