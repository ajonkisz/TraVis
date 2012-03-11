package travis.view.project.tree.popup;

import javax.swing.JLabel;

import travis.util.Messages;

public class PackageDialogPanel extends DialogPanel {

	private static final long serialVersionUID = 7030713040914046613L;

	public PackageDialogPanel() {
		super();

		add(new JLabel(Messages.get("new.package.desc")), "wrap");
		
		add(nameField, "span, grow");
	}

}