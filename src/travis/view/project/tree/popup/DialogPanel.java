package travis.view.project.tree.popup;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import travis.model.project.StructStub;

public abstract class DialogPanel {

	private static final long serialVersionUID = -297606176170762799L;

	private final JPanel panel; 
	protected final StructStub stub;
	protected final JTextField nameField;

	public DialogPanel() {
		stub = new StructStub();
		panel = new JPanel(new MigLayout());

		nameField = new JTextField(14);
		nameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				stub.setName(nameField.getText());
			}
		});
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public void add(Component comp) {
		panel.add(comp);
	}
	
	public void add(Component comp, Object constraint) {
		panel.add(comp, constraint);
	}
	
	public StructStub getStub() {
		return stub;
	}

	public void focusField() {
		nameField.requestFocus();
	}

	public void closing() {
		stub.setName(nameField.getText());
	}

}
