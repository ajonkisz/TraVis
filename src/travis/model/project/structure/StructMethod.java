/*
 * StructMethod.java
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

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import travis.model.project.StructStub;
import travis.model.project.structure.StructUtil.Visibility;
import travis.util.Messages;

public class StructMethod extends StructComponent {

    private static final long serialVersionUID = 3139286677120020833L;

    private final String descriptor;

    public StructMethod(MethodNode methodNode, StructClass parent) {
        super(methodNode.name, methodNode.access, parent, State.YES);
        this.descriptor = methodNode.desc;
        isPartOfClassPath = State.YES;
    }

    public StructMethod(StructClass parent, StructStub stub) {
        super(parent, stub);
        this.descriptor = stub.getDescriptor();
        isPartOfClassPath = State.YES;
    }

    @Override
    public boolean isPartOfClassPath() {
        return true;
    }

    @Override
    public boolean containsAnyClasses() {
        return true;
    }

    @Override
    protected String getClassPath() {
        return getParent().getClassPath();
    }

    @Override
    public boolean isMainMethod() {
        return getName().equals("main")
                && descriptor.equals("([Ljava/lang/String;)V") && isStatic()
                && getVisibility() == Visibility.PUBLIC;
    }

    @Override
    public String getDescriptor() {
        return descriptor.replaceAll("/", ".");
    }

    @Override
    public String getReturnType() {
        return StructUtil.getLastPartOfClassName(Type.getReturnType(descriptor)
                .getClassName());
    }

    @Override
    public String getParameters(boolean fullClassNames) {
        StringBuilder sb = new StringBuilder();
        Type[] types = Type.getArgumentTypes(descriptor);
        for (int i = 0; i < types.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }

            if (fullClassNames) {
                sb.append(types[i].getClassName());
            } else {
                sb.append(StructUtil.getLastPartOfClassName(types[i]
                        .getClassName()));
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isConstructor() {
        String name = getName();
        return name.equals("<clinit>") || name.equals("<init>");
    }

    @Override
    public boolean addStructComponent(StructComponent sComponent) {
        throw new IllegalArgumentException(
                Messages.get("smethod.component.exception"));
    }

    @Override
    public boolean removeStructComponent(StructComponent sComponent) {
        throw new IllegalArgumentException(
                Messages.get("smethod.component.exception"));
    }

    @Override
    public String getTooltipFriendlyName() {
        StringBuilder sb = new StringBuilder();
        sb.append(getParent().getTooltipFriendlyName());
        sb.append('.');
        sb.append(getName());
        addParameters(sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        addParameters(sb);
        if (!isConstructor()) {
            sb.append(" : ");
            String returnType = StructUtil.getLastPartOfClassName(Type
                    .getReturnType(descriptor).getClassName());
            sb.append(returnType);
        }
        return sb.toString();
    }

    private void addParameters(StringBuilder sb) {
        sb.append('(');
        sb.append(getParameters(false));
        sb.append(')');
    }

}
