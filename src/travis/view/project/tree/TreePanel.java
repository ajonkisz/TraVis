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
