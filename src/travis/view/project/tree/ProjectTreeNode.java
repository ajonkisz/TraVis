/*
 * ProjectTreeNode.java
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

package travis.view.project.tree;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;

public class ProjectTreeNode extends DefaultMutableTreeNode implements
        Comparable<ProjectTreeNode> {

    private static final long serialVersionUID = -7665836012973040092L;

    private final StructComponent root;

    public ProjectTreeNode(StructComponent root) {
        super(root);
        this.root = root;
        addChildren();
    }

    private void addChildren() {
        for (StructComponent comp : root.getChildren()) {
            add(new ProjectTreeNode(comp));
        }
    }

    public static ProjectTreeNode findMethod(ProjectTreeNode root,
                                             StructMethod method) {
        if (root.getUserObject().equals(method))
            return root;

        LinkedList<StructComponent> parents = new LinkedList<StructComponent>();
        StructComponent temp = method;
        while ((temp = temp.getParent()) != null) {
            parents.addFirst(temp);
        }
        // Poll default.package
        parents.poll();
        if (parents.size() == 0)
            return null;
        parents.add(method);

        ProjectTreeNode level = root;
        for (StructComponent comp : parents) {
            @SuppressWarnings("unchecked")
            Vector<ProjectTreeNode> rootChildren = level.children;
            if (rootChildren != null) {
                ProjectTreeNode[] nodes;
                synchronized (rootChildren) {
                    nodes = new ProjectTreeNode[rootChildren.size()];
                    rootChildren.toArray(nodes);
                }
                level = binarySearch(nodes, comp, 0, nodes.length - 1);
                if (level == null) {
                    return null;
                }
            }
        }

        return level;
    }

    private static ProjectTreeNode binarySearch(ProjectTreeNode[] nodes,
                                                StructComponent value, int low, int high) {
        if (high < low)
            return null;
        int mid = low + (high - low) / 2;
        if (nodes[mid].getUserObject().compareTo(value) > 0)
            return binarySearch(nodes, value, low, mid - 1);
        else if (nodes[mid].getUserObject().compareTo(value) < 0)
            return binarySearch(nodes, value, mid + 1, high);
        else
            return nodes[mid];
    }

    public StructComponent getRootStructComp() {
        if (getParent() != null)
            return getParent().getRootStructComp();
        else
            return root;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<ProjectTreeNode> depthFirstEnumeration() {
        return super.depthFirstEnumeration();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<ProjectTreeNode> breadthFirstEnumeration() {
        return super.breadthFirstEnumeration();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<ProjectTreeNode> children() {
        return super.children();
    }

    @Override
    public ProjectTreeNode getParent() {
        return (ProjectTreeNode) super.getParent();
    }

    @Override
    public int getDepth() {
        if (parent == null)
            return 0;
        else {
            return 1 + ((ProjectTreeNode) parent).getDepth();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProjectTreeNode) {
            return root.equals(((ProjectTreeNode) obj).root);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }

    @Override
    public StructComponent getUserObject() {
        return root;
    }

    @Override
    public int compareTo(ProjectTreeNode o) {
        return root.compareTo(o.getUserObject());
    }

}
