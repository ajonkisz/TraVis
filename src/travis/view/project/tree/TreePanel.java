/*
 * TreePanel.java
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

import java.awt.Dimension;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import net.miginfocom.swing.MigLayout;

public class TreePanel extends JPanel {

    private static final long serialVersionUID = -1199916802849919758L;

    private final ProjectTree tree;

    public TreePanel() {
        super(new MigLayout("fill, insets 0"));
        tree = new ProjectTree();

        JScrollPane treeView = new JScrollPane(tree.getTree());
        treeView.setPreferredSize(new Dimension(300, 500));
        this.add(treeView, "span, grow");
    }

    public ProjectTree getTree() {
        return tree;
    }

    public JTree getJTree() {
        return tree.getTree();
    }

    public void buildTree(File directory) {
        tree.buildTree(directory);
    }

    public void attachTree(File directory) {
        tree.attachTree(directory);
    }

}
