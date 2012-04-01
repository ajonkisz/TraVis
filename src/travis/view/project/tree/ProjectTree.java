/*
 * ProjectTree.java
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

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import travis.controller.UIGraphicsHelper;
import travis.controller.UIHelper;
import travis.controller.UIHelper.Mode;
import travis.model.project.Builder;
import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.project.structure.StructPackage;
import travis.model.script.FileParser;
import travis.model.script.ScriptGenerator;
import travis.util.Messages;
import travis.view.project.tree.popup.TreePopupMenu;
import travis.view.settings.Settings;

public class ProjectTree implements ChangeListener {

    private final ScriptGenerator scriptGen;
    private final JTree tree;
    private final CheckBoxTreeCellRenderer renderer;
    private StructComponent root;
    private ProjectTreeNode rootNode;
    private volatile boolean changed;
    private volatile boolean needRebuildScript;

    private final Semaphore painterSemaphore = new Semaphore(1);
    private final ExecutorService painterWaiter = Executors
            .newSingleThreadExecutor();

    public ProjectTree() {
        scriptGen = new ScriptGenerator();
        tree = new JTree(new Object[]{});
        renderer = new CheckBoxTreeCellRenderer(tree);
        tree.setCellRenderer(renderer);
        renderer.addChangeListener(this);
        tree.addMouseListener(new TreePopupMenu(this));
        needRebuildScript = true;
        buildEmptyTree();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!painterSemaphore.tryAcquire()) {
            return;
        }
        waitForMoreNotifications(10, e);
    }

    private void waitForMoreNotifications(final long waitTime,
                                          final ChangeEvent evt) {
        painterWaiter.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(waitTime);
                    repaintTree(evt);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    painterSemaphore.release();
                }
            }
        });
    }

    private void repaintTree(ChangeEvent e) {
        if (e.getSource().equals(renderer)) {
            changed = true;
            needRebuildScript = true;
            UIGraphicsHelper.getInstance().repaintTreeGraph();
            if (UIHelper.getInstance().getMode() == Mode.PLAYBACK)
                UIHelper.getInstance().updatePlaybackMode();
        }
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void consumeChenge() {
        changed = false;
    }

    public void buildEmptyTree() {
        Builder builder = new Builder();
        root = builder.build();
        rebuildTree();
    }

    public void setRoot(FileParser fp) {
        renderer.resetCheckedPaths();
        this.root = fp.getRoot();
        rebuildTree();
        restoreSelections(fp.getTreePaths());
    }

    public void buildTree(File directory) {
        renderer.resetCheckedPaths();
        Builder builder = new Builder();
        try {
            root = builder.build(directory);
            if (!isValidClassPath(root)) {
                if (UIHelper.getInstance().displayConfirmation(
                        Messages.get("incorrect.classpath")))
                    UIHelper.getInstance().openClasspath();
                return;
            }

            rebuildTree();
        } catch (Exception e) {
            UIHelper.getInstance().displayException(e);
        }
    }

    public void attachTree(final File directory) {
        treeAction(new TreeAction() {
            @Override
            public void act() {
                Builder builder = new Builder();
                try {
                    StructComponent classpathRoot = builder.build(directory);
                    if (!isValidClassPath(classpathRoot)) {
                        if (UIHelper.getInstance().displayConfirmation(
                                Messages.get("incorrect.classpath")))
                            UIHelper.getInstance().attachClasspath();
                        return;
                    }

                    root.addStructComponents(classpathRoot.getChildren());
                    rebuildTree();
                } catch (Exception e) {
                    UIHelper.getInstance().displayException(e);
                }
            }
        });
    }

    public void treeAction(TreeAction treeAction) {
        Enumeration<TreePath> expanded = getExpandedPaths();
        TreePath[] checkedPaths = getCheckedPaths();

        treeAction.act();

        restoreExpandedPathsAndSelections(expanded, checkedPaths);
    }

    private void restoreExpandedPathsAndSelections(
            Enumeration<TreePath> expanded, TreePath[] checkedPaths) {
        rebuildTree();

        restoreSelections(checkedPaths);
        restoreExpandedPaths(expanded);
    }

    private void restoreSelections(TreePath[] checkedPaths) {
        renderer.resetCheckedPaths();
        if (checkedPaths != null && checkedPaths.length > 0) {
            for (TreePath path : checkedPaths) {
                toggleChecked(path);
            }
        }
        renderer.fireStateChanged();
    }

    private void restoreExpandedPaths(Enumeration<TreePath> expanded) {
        if (expanded != null) {
            while (expanded.hasMoreElements()) {
                TreePath treePath = (TreePath) expanded.nextElement();
                tree.expandPath(treePath);
            }
        }
    }

    private Enumeration<TreePath> getExpandedPaths() {
        TreePath rootPath = new TreePath(rootNode);
        return tree.getExpandedDescendants(rootPath);
    }

    private boolean isValidClassPath(StructComponent root) {
        for (StructComponent comp : root.getChildren()) {
            if (comp.isPartOfClassPath()) {
                if (comp instanceof StructClass) {
                    if (!comp.getName().contains(".")) {
                        return true;
                    }
                } else if (comp instanceof StructPackage) {
                    return true;
                }
            }
        }
        return false;
    }

    public void rebuildTree() {
        changed = true;
        needRebuildScript = true;
        rootNode = new ProjectTreeNode(root);
        tree.setModel(new DefaultTreeModel(rootNode));
        tree.requestFocusInWindow();
    }

    public void generateScript() {
        setupScriptGenerator();
        scriptGen.buildScript();
    }

    public void setupScriptGenerator() {
        if (!needRebuildScript)
            return;

        TreePath[] checkedPaths = renderer.getCheckedPaths();
        Set<StructComponent> comps = new TreeSet<StructComponent>();
        for (TreePath tp : checkedPaths) {
            ProjectTreeNode node = (ProjectTreeNode) tp.getLastPathComponent();
            StructComponent comp = node.getUserObject();
            comps.add(comp);
        }
        try {
            scriptGen.generateForComponents(root, comps);
        } catch (IOException e) {
            UIHelper.getInstance().displayException(e);
        }

        needRebuildScript = false;
        // Set<StructComponent> comps2 = new TreeSet<StructComponent>();
        // comps2.add(root);
        // scriptGen.generateForComponents(root, comps2);
    }

    public JTree getTree() {
        return tree;
    }

    public StructComponent getRoot() {
        return root;
    }

    public ProjectTreeNode getRootNode() {
        return rootNode;
    }

    public boolean isSelected(ProjectTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        return renderer.isChecked(path) || renderer.isPartiallyChecked(path);
    }

    public TreePath[] getCheckedPaths() {
        return renderer.getCheckedPaths();
    }

    public void toggleChecked(TreePath path) {
        renderer.toggleCheckedIgnoreChanged(path);
    }

    public int getTotalNumberOfSelectedMethods() {
        return getNumberOfSelectedMethods(rootNode);
    }

    public int getNumberOfSelectedMethods(ProjectTreeNode node) {
        int i = 0;
        if (node.getUserObject() instanceof StructMethod
                && Settings.getInstance().isDrawingMethod(node.getUserObject())) {
            TreePath path = new TreePath(node.getPath());
            if (renderer.isChecked(path))
                i++;
        } else {
            Enumeration<ProjectTreeNode> children = node.children();
            while (children.hasMoreElements()) {
                ProjectTreeNode child = children.nextElement();
                if (Settings.getInstance()
                        .isDrawingClass(child.getUserObject()))
                    i += getNumberOfSelectedMethods(child);
            }
        }
        return i;
    }

    public Map<Integer, StructMethod> getSelectedMethodsIds() {
        Map<Integer, StructMethod> idMap = new TreeMap<Integer, StructMethod>();

        Map<StructMethod, Integer> methods = getStructMethods();
        for (Entry<StructMethod, Integer> e : methods.entrySet()) {
            StructMethod method = e.getKey();
            ProjectTreeNode treeNode = ProjectTreeNode.findMethod(rootNode,
                    method);
            // ProjectTreeNode treeNode = rootNode.find(method);
            if (treeNode != null
                    && isSelected(treeNode)
                    && Settings.getInstance()
                    .isDrawingClass(method.getParent())
                    && Settings.getInstance().isDrawingMethod(method)) {
                idMap.put(e.getValue(), method);
            }
        }
        return idMap;
    }

    public Map<StructMethod, Integer> getStructMethods() {
        setupScriptGenerator();
        return scriptGen.getMethods();
    }

}
