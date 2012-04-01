/*
 * StructComponent.java
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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.objectweb.asm.Opcodes;
import travis.model.project.StructStub;
import travis.model.project.structure.StructUtil.Visibility;

public abstract class StructComponent implements Comparable<StructComponent>,
        Serializable {

    private static final long serialVersionUID = -7522901348550035402L;

    // This is used for lazy loading. Once calculated data is stored to avoid
    // future calculations.
    protected enum State {
        YES, NO, UNKNOWN
    }

    protected State isPartOfClassPath;
    private String classPath;

    private final Visibility visibility;
    private final String name;
    private final int access;
    private final TreeSet<StructComponent> children;
    private final StructComponent parent;

    protected StructComponent(StructComponent parent, StructStub stub) {
        this(stub.getName(), stub.getAccessFlag(), parent);
    }

    protected StructComponent(String name, StructComponent parent) {
        this(name, 0, parent);
    }

    protected StructComponent(String name, int access, StructComponent parent) {
        this(name, access, parent, State.UNKNOWN);
    }

    protected StructComponent(String name, int access, StructComponent parent,
                              State hasAnyClasses) {
        this.name = name;
        this.visibility = StructUtil.getVisibility(access);
        this.access = access;
        this.children = new TreeSet<StructComponent>();
        this.parent = parent;
        this.classPath = "";
        this.isPartOfClassPath = State.UNKNOWN;
    }

    public String getTooltipFriendlyName() {
        return toString();
    }

    public Map<StructMethod, Integer> getMethods() {
        Map<StructMethod, Integer> map = new HashMap<StructMethod, Integer>();
        getMethods(map, new AtomicInteger());
        return map;
    }

    private void getMethods(Map<StructMethod, Integer> methods, AtomicInteger i) {
        for (StructComponent child : children) {
            if (child instanceof StructMethod) {
                methods.put((StructMethod) child, i.getAndIncrement());
            } else {
                child.getMethods(methods, i);
            }
        }
    }

    public boolean containsAnyClasses() {
        for (Iterator<StructComponent> iterator = children.descendingIterator(); iterator
                .hasNext(); ) {
            StructComponent child = iterator.next();
            if (child instanceof StructClass) {
                return true;
            } else if (child instanceof StructPackage) {
                // This takes advantage of the fact that the tree is sorted and
                // classes are stored always after packages
                return false;
            }
        }
        return false;
    }

    public boolean isPartOfClassPath() {
        switch (isPartOfClassPath) {
            case YES:
                return true;
            case NO:
                return false;
            case UNKNOWN:
                if (parent != null && parent.isPartOfClassPath == State.YES) {
                    isPartOfClassPath = State.YES;
                    return true;
                }

                for (StructComponent child : children) {
                    if (child.isPartOfClassPath()) {
                        String childClassPath = child.getClassPath();
                        if (!childClassPath.equals("")
                                && name.endsWith(childClassPath)) {
                            setClassPath(removeLastDotWord(childClassPath));
                            isPartOfClassPath = State.YES;
                            return true;
                        }
                    }
                }
                isPartOfClassPath = State.NO;
                return false;
        }
        return false;
    }

    protected String getClassPath() {
        return classPath;
    }

    protected void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public Set<String> getMainMethodComponents() {
        Set<String> mainClasses = new TreeSet<String>();
        addMainMethodComponents(mainClasses);
        return mainClasses;
    }

    private void addMainMethodComponents(Set<String> mainClasses) {
        if (this.isMainMethod()) {
            mainClasses.add(getParentName());
        } else {
            for (StructComponent comp : children) {
                comp.addMainMethodComponents(mainClasses);
            }
        }
    }

    public boolean isMainMethod() {
        return false;
    }

    public boolean isDefaultPackage() {
        return false;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public String getName() {
        return name;
    }

    public int getAccessFlag() {
        return access;
    }

    public boolean isStatic() {
        return StructUtil.isFlagSet(access, Opcodes.ACC_STATIC);
    }

    public boolean isFinal() {
        return StructUtil.isFlagSet(access, Opcodes.ACC_FINAL);
    }

    public boolean isOrdinaryClass() {
        return false;
    }

    public boolean isAbstract() {
        return StructUtil.isFlagSet(access, Opcodes.ACC_ABSTRACT);
    }

    public boolean isEnum() {
        return StructUtil.isFlagSet(access, Opcodes.ACC_ENUM);
    }

    public boolean isInterface() {
        return StructUtil.isFlagSet(access, Opcodes.ACC_INTERFACE);
    }

    public String getDescriptor() {
        return "";
    }

    public StructComponent getParent() {
        return parent;
    }

    public String getParentName() {
        return parent == null ? "" : parent.name;
    }

    public String getReturnType() {
        return "";
    }

    public String getParameters(boolean fullClassNames) {
        return "";
    }

    public boolean isConstructor() {
        return false;
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public Collection<StructComponent> getChildren() {
        return children;
    }

    public boolean addStructComponent(StructComponent sComponent) {
        return children.add(sComponent);
    }

    public void addStructComponents(Collection<StructComponent> sComponents) {
        for (StructComponent comp : sComponents) {
            if (children.contains(comp)) {
                StructComponent child = getChild(comp);
                child.addStructComponents(comp.getChildren());
            } else {
                addStructComponent(comp);
            }
        }
    }

    /**
     * Searches only children in depth 1.
     *
     * @param structComponent child to be searched for
     * @return found child or null if child was not found in depth
     */
    public StructComponent getChild(StructComponent structComponent) {
        for (StructComponent child : children) {
            if (child.compareTo(structComponent) == 0)
                return child;
        }
        return null;
    }

    /**
     * Perform breadth first search.
     *
     * @param structComponent child to be searched for
     * @return found child or null if one was not found
     */
    public StructComponent findChild(StructComponent structComponent) {
        Queue<StructComponent> q = new LinkedList<StructComponent>();
        q.addAll(children);
        StructComponent foundChild = null;
        while (!q.isEmpty()) {
            StructComponent child = q.poll();
            if (child.compareTo(structComponent) == 0) {
                foundChild = child;
                break;
            } else {
                q.addAll(child.children);
            }
        }
        return foundChild;
    }

    public boolean removeStructComponent(StructComponent sComponent) {
        return children.remove(sComponent);
    }

    public static String removeLastDotWord(String string) {
        int i = string.lastIndexOf('.');
        return i == -1 ? "" : string.substring(0, i);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StructComponent) {
            int result = this.compareTo((StructComponent) obj);
            return result == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int code = name.hashCode();
        if (parent != null)
            code += parent.hashCode();
        return code;
    }

    @Override
    public int compareTo(StructComponent o) {
        if (this instanceof StructPackage && !(o instanceof StructPackage))
            return -1;
        else if (o instanceof StructPackage && !(this instanceof StructPackage))
            return 1;
        else if (this instanceof StructClass && !(o instanceof StructClass))
            return -1;
        else if (o instanceof StructClass && !(this instanceof StructClass))
            return 1;
        else if (this instanceof StructMethod && o instanceof StructMethod) {
            StructMethod m1 = (StructMethod) this;
            StructMethod m2 = (StructMethod) o;
            if (m1.isConstructor() && !m2.isConstructor())
                return -1;
            else if (m2.isConstructor() && !m1.isConstructor())
                return 1;
            String m1Name = m1.toString() + ' ' + m1.getParentName();
            String m2Name = m2.toString() + ' ' + m2.getParentName();
            return m1Name.compareTo(m2Name);
        }
        return this.name.compareTo(o.name);
    }

}
