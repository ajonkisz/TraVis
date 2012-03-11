package travis.view.project.tree;

/* Copyright (c) 2006-2007 Timothy Wall, All Rights Reserved
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.  
 */
import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.project.structure.StructPackage;
import travis.resources.IconFactory;

/**
 * Provides checkbox-based selection of tree nodes. Override the protected
 * methods to adapt this renderer's behavior to your local tree table flavor. No
 * change listener notifications are provided.
 */
public class CheckBoxTreeCellRenderer implements TreeCellRenderer {

	public static final int UNCHECKABLE = 0;
	public static final int FULLCHECKED = 1;
	public static final int UNCHECKED = 2;
	public static final int PARTIALCHECKED = 3;

	private static final String RETURN_TYPE = "((?!:).*)(: .*)";
	private static final String RETURN_TYPE_HIGHLIGHT = "<html>$1<font color='#666633'>$2</font></html>";

	private final TreeCellRenderer renderer;
	private final JCheckBox checkBox;
	private final Set<TreePath> checkedPaths;
	private final JTree tree;
	private Point mouseLocation;
	private int mouseRow = -1;
	private int pressedRow = -1;
	private boolean mouseInCheck;
	private int state = UNCHECKED;
	private MouseHandler handler;

	private final EventListenerList listenerList;

	/** Create a per-tree instance of the checkbox renderer. 
	 */
	public CheckBoxTreeCellRenderer(final JTree tree) {
		this.tree = tree;
		this.tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					int[] rows = tree.getSelectionRows();
					if (rows == null)
						return;

					for (int i : rows) {
						toggleChecked(i);
					}
				}
			}
		});
		this.renderer = tree.getCellRenderer();
		checkedPaths = new HashSet<TreePath>();
		checkBox = new JCheckBox();
		checkBox.setOpaque(false);
		checkBox.setSize(checkBox.getPreferredSize());

		listenerList = new EventListenerList();
	}
	
	public void resetCheckedPaths() {
		checkedPaths.clear();
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	public void fireStateChanged() {
		ChangeListener[] listeners = listenerList
				.getListeners(ChangeListener.class);
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener listener : listeners) {
			listener.stateChanged(e);
		}
	}

	protected void installMouseHandler() {
		if (handler == null) {
			handler = new MouseHandler();
			addMouseHandler(handler);
		}
	}

	protected void addMouseHandler(MouseHandler handler) {
		tree.addMouseListener(handler);
		tree.addMouseMotionListener(handler);
	}

	private void updateMouseLocation(Point newLoc) {
		if (mouseRow != -1) {
			repaint(mouseRow);
		}
		mouseLocation = newLoc;
		if (mouseLocation != null) {
			mouseRow = getRow(newLoc);
			repaint(mouseRow);
		} else {
			mouseRow = -1;
		}
		if (mouseRow != -1 && mouseLocation != null) {
			Point mouseLoc = new Point(mouseLocation);
			Rectangle r = getRowBounds(mouseRow);
			if (r != null)
				mouseLoc.x -= r.x;
			mouseInCheck = isInCheckBox(mouseLoc);
		} else {
			mouseInCheck = false;
		}
	}

	protected int getRow(Point p) {
		return tree.getRowForLocation(p.x, p.y);
	}

	protected Rectangle getRowBounds(int row) {
		return tree.getRowBounds(row);
	}

	protected TreePath getPathForRow(int row) {
		return tree.getPathForRow(row);
	}

	protected int getRowForPath(TreePath path) {
		return tree.getRowForPath(path);
	}

	protected void repaint(Rectangle r) {
		tree.repaint(r);
	}

	public void repaint() {
		tree.repaint();
	}

	private void repaint(int row) {
		Rectangle r = getRowBounds(row);
		if (r != null)
			repaint(r);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		installMouseHandler();
		TreePath path = getPathForRow(row);
		state = UNCHECKABLE;
		if (path != null) {
			if (isChecked(path)) {
				state = FULLCHECKED;
			} else if (isPartiallyChecked(path)) {
				state = PARTIALCHECKED;
			} else if (isSelectable(path)) {
				state = UNCHECKED;
			}
		}
		checkBox.setSelected(state == FULLCHECKED);
		checkBox.getModel().setArmed(
				mouseRow == row && pressedRow == row && mouseInCheck);
		checkBox.getModel().setPressed(pressedRow == row && mouseInCheck);
		checkBox.getModel().setRollover(mouseRow == row && mouseInCheck);

		Component c = renderer.getTreeCellRendererComponent(tree, value,
				selected, expanded, leaf, row, hasFocus);
		checkBox.setForeground(c.getForeground());
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			// Augment the icon to include the checkbox
			setAppropriateLabelAndIcon(value, label, selected, expanded);
		}
		return c;
	}

	private void setAppropriateLabelAndIcon(Object value, JLabel label,
			boolean selected, boolean expanded) {
		if (!(value instanceof ProjectTreeNode)) {
			label.setIcon(new CompoundIcon(label.getIcon()));
			return;
		}

		ProjectTreeNode node = (ProjectTreeNode) value;
		StructComponent comp = node.getUserObject();

		if (comp instanceof StructPackage) {
			formatLabelTextForPackage(label);
			label.setIcon(new CompoundIcon(IconFactory.getPackageIcon(comp,
					expanded)));
		} else if (comp instanceof StructClass) {
			label.setIcon(new CompoundIcon(IconFactory.getClassIcon(comp)));
		} else if (comp instanceof StructMethod) {
			formatLabelTextForMethod(label, selected);
			label.setIcon(new CompoundIcon(IconFactory.getMethodIcon(comp)));
		}
	}

	private void formatLabelTextForPackage(JLabel label) {
		String text = label.getText();
		int dotIndex = text.lastIndexOf('.');
		label.setText(text.substring(dotIndex == -1 ? 0 : dotIndex + 1));
	}

	private void formatLabelTextForMethod(JLabel label, boolean selected) {
		String text = label.getText();
		if (text.contains(" : ") && !selected) {
			String htmlText = text.replaceFirst(RETURN_TYPE,
					RETURN_TYPE_HIGHLIGHT);
			label.setText(htmlText);
		}
	}

	private boolean isInCheckBox(Point where) {
		Insets insets = tree.getInsets();
		int right = checkBox.getWidth();
		int left = 0;
		if (insets != null) {
			left += insets.left;
			right += insets.left;
		}
		return where.x >= left && where.x < right;
	}

	public boolean isExplicitlyChecked(TreePath path) {
		return checkedPaths.contains(path);
	}

	/**
	 * Returns whether selecting the given path is allowed. The default returns
	 * true. You should return false if the given path represents a placeholder
	 * for a node that has not yet loaded, or anything else that doesn't
	 * represent a normal, operable object in the tree.
	 */
	public boolean isSelectable(TreePath path) {
		return true;
	}

	/** Returns whether the given path is currently checked. */
	public boolean isChecked(TreePath path) {
		if (isExplicitlyChecked(path)) {
			return true;
		} else {
			if (path.getParentPath() != null) {
				return isChecked(path.getParentPath());
			} else {
				return false;
			}
		}
	}

	public boolean isPartiallyChecked(TreePath path) {
		Object node = path.getLastPathComponent();
		for (int i = 0; i < tree.getModel().getChildCount(node); i++) {
			Object child = tree.getModel().getChild(node, i);
			TreePath childPath = path.pathByAddingChild(child);
			if (isChecked(childPath) || isPartiallyChecked(childPath)) {
				return true;
			}
		}
		return false;
	}

	private boolean isFullyChecked(TreePath parent) {
		Object node = parent.getLastPathComponent();
		for (int i = 0; i < tree.getModel().getChildCount(node); i++) {
			Object child = tree.getModel().getChild(node, i);
			TreePath childPath = parent.pathByAddingChild(child);
			if (!isExplicitlyChecked(childPath)) {
				return false;
			}
		}
		return true;
	}

	public void toggleChecked(int row) {
		TreePath path = getPathForRow(row);
		toggleChecked(path);
	}

	public void toggleChecked(TreePath path) {
		toggleCheckedIgnoreChanged(path);
		fireStateChanged();
	}
	
	public void toggleCheckedIgnoreChanged(TreePath path) {
		boolean isChecked = isChecked(path);
		removeDescendants(path);
		if (!isChecked) {
			checkedPaths.add(path);
		}
		setParent(path);
		repaint();
	}

	private void setParent(TreePath path) {
		TreePath parent = path.getParentPath();
		if (parent != null) {
			if (isFullyChecked(parent)) {
				removeChildren(parent);
				checkedPaths.add(parent);
			} else {
				if (isChecked(parent)) {
					checkedPaths.remove(parent);
					addChildren(parent);
					checkedPaths.remove(path);
				}
			}
			setParent(parent);
		}
	}

	private void addChildren(TreePath parent) {
		Object node = parent.getLastPathComponent();
		for (int i = 0; i < tree.getModel().getChildCount(node); i++) {
			Object child = tree.getModel().getChild(node, i);
			TreePath path = parent.pathByAddingChild(child);
			checkedPaths.add(path);
		}
	}

	private void removeChildren(TreePath parent) {
		for (Iterator<TreePath> i = checkedPaths.iterator(); i.hasNext();) {
			TreePath p = (TreePath) i.next();
			if (p.getParentPath() != null && parent.equals(p.getParentPath())) {
				i.remove();
			}
		}
	}

	private void removeDescendants(TreePath ancestor) {
		for (Iterator<TreePath> i = checkedPaths.iterator(); i.hasNext();) {
			TreePath path = (TreePath) i.next();
			if (ancestor.isDescendant(path)) {
				i.remove();
			}
		}
	}

	/** Returns all checked rows. */
	public int[] getCheckedRows() {
		TreePath[] paths = getCheckedPaths();
		int[] rows = new int[checkedPaths.size()];
		for (int i = 0; i < checkedPaths.size(); i++) {
			rows[i] = getRowForPath(paths[i]);
		}
		Arrays.sort(rows);
		return rows;
	}

	/** Returns all checked paths. */
	public TreePath[] getCheckedPaths() {
		return (TreePath[]) checkedPaths.toArray(new TreePath[checkedPaths
				.size()]);
	}

	protected class MouseHandler extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			updateMouseLocation(e.getPoint());
		}

		@Override
		public void mouseExited(MouseEvent e) {
			updateMouseLocation(null);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			updateMouseLocation(e.getPoint());
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			updateMouseLocation(e.getPoint());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			pressedRow = e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK ? getRow(e
					.getPoint()) : -1;
			updateMouseLocation(e.getPoint());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (pressedRow != -1) {
				int row = getRow(e.getPoint());
				if (row == pressedRow) {
					Point p = e.getPoint();
					Rectangle r = getRowBounds(row);
					p.x -= r.x;
					if (isInCheckBox(p)) {
						toggleChecked(row);
					}
				}
				pressedRow = -1;
				updateMouseLocation(e.getPoint());
			}
		}
	}

	/** Combine a JCheckBox's checkbox with another icon. */
	private final class CompoundIcon implements Icon {
		private final Icon icon;
		private final int w;
		private final int h;

		private CompoundIcon(Icon icon) {
			if (icon == null) {
				icon = new Icon() {
					@Override
					public int getIconHeight() {
						return 0;
					}

					@Override
					public int getIconWidth() {
						return 0;
					}

					@Override
					public void paintIcon(Component c, Graphics g, int x, int y) {
					}
				};
			}
			this.icon = icon;
			this.w = icon.getIconWidth();
			this.h = icon.getIconHeight();
		}

		@Override
		public int getIconWidth() {
			return checkBox.getPreferredSize().width + w;
		}

		@Override
		public int getIconHeight() {
			return Math.max(checkBox.getPreferredSize().height, h);
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (c.getComponentOrientation().isLeftToRight()) {
				int xoffset = checkBox.getPreferredSize().width;
				int yoffset = (getIconHeight() - icon.getIconHeight()) / 2;
				icon.paintIcon(c, g, x + xoffset, y + yoffset);
				if (state != UNCHECKABLE) {
					paintCheckBox(g, x, y);
				}
			} else {
				int yoffset = (getIconHeight() - icon.getIconHeight()) / 2;
				icon.paintIcon(c, g, x, y + yoffset);
				if (state != UNCHECKABLE) {
					paintCheckBox(g, x + icon.getIconWidth(), y);
				}
			}
		}

		private void paintCheckBox(Graphics g, int x, int y) {
			int yoffset;
			boolean db = checkBox.isDoubleBuffered();
			checkBox.setDoubleBuffered(false);
			try {
				yoffset = (getIconHeight() - checkBox.getPreferredSize().height) / 2;
				g = g.create(x, y + yoffset, getIconWidth(), getIconHeight());
				checkBox.paint(g);
				if (state == PARTIALCHECKED) {
					final int WIDTH = 2;
					g.setColor(UIManager.getColor("CheckBox.foreground"));
					Graphics2D g2d = (Graphics2D) g;
					g2d.setStroke(new BasicStroke(WIDTH, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND));
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					int w = checkBox.getWidth();
					int h = checkBox.getHeight();
					g.drawLine(w / 4 + 2, h / 2 - WIDTH / 2 + 1, w / 4 + w / 2
							- 3, h / 2 - WIDTH / 2 + 1);
				}
				g.dispose();
			} finally {
				checkBox.setDoubleBuffered(db);
			}
		}
	}
}