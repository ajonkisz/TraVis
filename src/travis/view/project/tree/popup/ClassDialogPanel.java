package travis.view.project.tree.popup;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import travis.model.project.StructStub;
import travis.util.Messages;

public class ClassDialogPanel extends DialogPanel {

	private final JComboBox visibility;
	private final JComboBox inheritance;

	public ClassDialogPanel() {
		super();

		add(new JLabel(Messages.get("new.class.desc")), "span, wrap");

		add(new JLabel(Messages.get("visibility")), "center");
		add(new JLabel(Messages.get("modifier")), "center");
		add(new JLabel(Messages.get("name")), "center, span, wrap");

		visibility = new JComboBox(StructStub.Visibility.values());
		visibility.setSelectedItem(StructStub.Visibility.PUBLIC);
		add(visibility, "gap para");

		inheritance = new JComboBox(StructStub.Inheritance.values());
		inheritance.setSelectedItem(StructStub.Inheritance.NONE);
		add(inheritance, "gap para");

		add(nameField, "span, growx");

		add(new JLabel("<html><font style='font-weight: bold; color: #ff0000'>"
				+ "<center>" + Messages.get("new.class.warning")
				+ "</center></font></html>"), "newline, span, center");
	}

	@Override
	public void closing() {
		super.closing();
		stub.setVisibility((StructStub.Visibility) visibility
				.getSelectedItem());
		stub.setInheritance((StructStub.Inheritance) inheritance
				.getSelectedItem());
	}

}
