package travis.view.project.tree.popup;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.TreePath;

import travis.controller.UIHelper;
import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.project.structure.StructPackage;
import travis.util.Messages;
import travis.view.Util;
import travis.view.project.tree.ProjectTree;
import travis.view.project.tree.ProjectTreeNode;
import travis.view.project.tree.TreeAction;

public class TreePopupMenu extends JPopupMenu implements ActionListener,
		MouseListener {

	private static final long serialVersionUID = 977905446115791437L;

	private static final int PACKAGE = 1;
	private static final int CLASS = 1 << 1;
	private static final int METHOD = 1 << 2;
	private static final int DELETE = 1 << 3;

	private final JMenuItem newPackageItem;
	private final JMenuItem newClassItem;
	private final JMenuItem newMethodItem;
	private final JMenuItem deleteItem;
	private final ProjectTree projectTree;
	private final JTree tree;

	public TreePopupMenu(ProjectTree projectTree) {
		super();
		this.projectTree = projectTree;
		this.tree = projectTree.getTree();
		newPackageItem = createMenuItem(Messages.get("new.package") + "...");
		newClassItem = createMenuItem(Messages.get("new.class") + "...");
		newMethodItem = createMenuItem(Messages.get("new.method") + "...");
		deleteItem = createMenuItem(Messages.get("delete"));
	}

	private JMenuItem createMenuItem(String text) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(this);
		add(item);
		return item;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newPackageItem)) {
			createNewPackage();
		} else if (e.getSource().equals(newClassItem)) {
			createNewClass();
		} else if (e.getSource().equals(newMethodItem)) {
			createNewMethod();
		} else if (e.getSource().equals(deleteItem)) {
			deleteItems();
		}
	}

	private void createNewPackage() {
		projectTree.treeAction(new TreeAction() {
			@Override
			public void act() {
				DialogPanel dialog = new PackageDialogPanel();
				if (!userConfirmedDialog(dialog, Messages.get("new.package")))
					return;

				StructComponent parent = getSelectedComponent();
				if (!parent.getName().equals("")) {
					dialog.getStub()
							.setName(
									parent.getName() + '.'
											+ dialog.getStub().getName());
				}

				parent.addStructComponent(new StructPackage(parent, dialog
						.getStub()));
			}
		});
	}

	private void createNewClass() {
		projectTree.treeAction(new TreeAction() {
			@Override
			public void act() {
				DialogPanel dialog = new ClassDialogPanel();
				if (!userConfirmedDialog(dialog, Messages.get("new.class")))
					return;

				StructComponent parent = getSelectedComponent();
				if (!parent.getName().equals("")) {
					dialog.getStub()
							.setName(
									parent.getName() + '.'
											+ dialog.getStub().getName());
				}

				parent.addStructComponent(new StructClass(parent, dialog
						.getStub()));
			}
		});
	}

	private void createNewMethod() {
		projectTree.treeAction(new TreeAction() {
			@Override
			public void act() {
				DialogPanel dialog = new MethodDialogPanel();
				if (!userConfirmedDialog(dialog, Messages.get("new.method")))
					return;

				StructComponent parent = getSelectedComponent();

				parent.addStructComponent(new StructMethod(
						(StructClass) parent, dialog.getStub()));
			}
		});
	}

	private void deleteItems() {
		projectTree.treeAction(new TreeAction() {
			@Override
			public void act() {
				TreePath[] paths = tree.getSelectionPaths();
				for (TreePath path : paths) {
					ProjectTreeNode parent = (ProjectTreeNode) path
							.getParentPath().getLastPathComponent();
					ProjectTreeNode node = (ProjectTreeNode) path
							.getLastPathComponent();

					parent.getUserObject().removeStructComponent(
							node.getUserObject());
				}
			}
		});
	}

	private StructComponent getSelectedComponent() {
		ProjectTreeNode node = (ProjectTreeNode) tree
				.getLastSelectedPathComponent();
		if (node == null)
			return projectTree.getRoot();
		else
			return node.getUserObject();
	}

	private boolean userConfirmedDialog(final DialogPanel dialogPanel,
			String title) {
		JOptionPane pane = new JOptionPane(dialogPanel.getPanel(),
				JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);
		JDialog dialog = pane.createDialog(UIHelper.getInstance()
				.getMainFrame(), title);
		// This window listener is added because there is a bug in JDialog
		// that does not allow to get focus on text field. The same bug is the
		// reason why I do not use JOptionPane.show...
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent ev) {
				Timer timer = new Timer(50, new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						dialogPanel.focusField();
					}
				});
				timer.setRepeats(false);
				timer.start();
			}
		});
		dialog.setVisible(true);
		Integer status = (Integer) pane.getValue();
		dialogPanel.closing();
		if (status == null)
			return false;
		return status == JOptionPane.OK_OPTION;
	}

	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int[] rows = tree.getSelectionRows();
			int row = tree.getRowForLocation(e.getX(), e.getY());
			rows = selectedRows(rows, row);
			tree.setSelectionRows(rows);

			show(rows, e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * This method is overridden to do nothing as
	 * {@link #show(int[], Component, int, int)} should be used instead.
	 */
	@Override
	public void show(Component invoker, int x, int y) {
		// Do nothing as only show with rows should be used.
	}

	private void show(int[] rows, Component invoker, int x, int y) {
		if (rows.length > 1) {
			setVisibleItems(DELETE);
		} else {
			determineVisibleItems();
		}

		super.show(invoker, x, y);
	}

	private void determineVisibleItems() {
		ProjectTreeNode node = (ProjectTreeNode) tree
				.getLastSelectedPathComponent();
		if (node == null) {
			setVisibleItems(PACKAGE | CLASS);
		} else if (node.getUserObject() instanceof StructPackage) {
			setVisibleItems(PACKAGE | CLASS | DELETE);
		} else if (node.getUserObject() instanceof StructClass) {
			setVisibleItems(METHOD | DELETE);
		} else if (node.getUserObject() instanceof StructMethod) {
			setVisibleItems(DELETE);
		}
	}

	private void setVisibleItems(int flag) {
		newPackageItem.setVisible(Util.containsFlag(flag, PACKAGE));
		newClassItem.setVisible(Util.containsFlag(flag, CLASS));
		newMethodItem.setVisible(Util.containsFlag(flag, METHOD));
		deleteItem.setVisible(Util.containsFlag(flag, DELETE));
	}

	private int[] selectedRows(int[] rows, int selectedRow) {
		if (rows == null || rows.length == 0)
			return new int[] { selectedRow };

		boolean contains = false;
		for (int i : rows) {
			if (i == selectedRow) {
				contains = true;
				break;
			}
		}

		if (contains) {
			return rows;
		} else {
			return new int[] { selectedRow };
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
