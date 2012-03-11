package travis.view.project.tree;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;
import travis.controller.UIHelper;
import travis.util.Messages;

public class NewProcessPane extends JOptionPane implements ActionListener,
		ChangeListener {

	private static final long serialVersionUID = -7968048896714105721L;

	private final JDialog dialog;
	private final Set<String> mains;

	private JTextField optionsField;
	private JRadioButton mainsButton;
	private JComboBox mainsComboBox;
	private JLabel noMainsLabel;
	private JRadioButton customButton;
	private JTextField customStartField;
	private JTextField argumentsField;
	private JTextField classPathField;
	private JButton classpathButton;

	public NewProcessPane(Set<String> mains) {
		super("", PLAIN_MESSAGE, OK_CANCEL_OPTION, null);
		this.mains = mains;

		populatePane();

		dialog = createDialog(UIHelper.getInstance().getMainFrame(),
				Messages.get("new.process"));

		dialog.setVisible(true);
		dialog.dispose();
	}

	private void setMainsEnabled(boolean enabled) {
		noMainsLabel.setEnabled(enabled);
		mainsComboBox.setEnabled(enabled);
	}

	private void setCustomStartEnabled(boolean enabled) {
		customStartField.setEnabled(enabled);
	}

	private void populatePane() {
		JPanel panel = new JPanel(
				new MigLayout("fill, insets 0", "[right]", ""));
		JLabel label = new JLabel(Messages.get("java.options"));
		optionsField = new JTextField(20);
		panel.add(label, "gap para");
		panel.add(optionsField, "span, growx, wrap");

		ButtonGroup group = new ButtonGroup();

		mainsButton = new JRadioButton(Messages.get("main.method"),
				!mains.isEmpty());
		panel.add(mainsButton, "gap para");
		mainsComboBox = new JComboBox(mains.toArray());
		noMainsLabel = new JLabel(Messages.get("no.main.methods"));
		if (mains.isEmpty()) {
			panel.add(noMainsLabel, "span, growx, wrap");
			noMainsLabel.setEnabled(false);
			mainsButton.setEnabled(false);
		} else {
			panel.add(mainsComboBox, "span, growx, wrap");
		}
		setMainsEnabled(mainsButton.isSelected());
		mainsButton.addChangeListener(this);
		group.add(mainsButton);

		customButton = new JRadioButton(Messages.get("custom.start"),
				mains.isEmpty());
		customStartField = new JTextField(20);
		panel.add(customButton, "gap para");
		panel.add(customStartField, "span, growx, wrap");
		setCustomStartEnabled(customButton.isSelected());
		customButton.addChangeListener(this);
		group.add(customButton);

		label = new JLabel(Messages.get("program.arguments"));
		argumentsField = new JTextField(20);
		panel.add(label, "gap para");
		panel.add(argumentsField, "span, growx, wrap");

		label = new JLabel(Messages.get("working.dir"));
		panel.add(label, "gap para");
		classPathField = new JTextField(UIHelper.getInstance()
				.getCurrentDirectory().toString(), 20);
		classPathField.setBackground(Color.WHITE);
		panel.add(classPathField, "growx");
		classpathButton = new JButton(Messages.get("change"));
		panel.add(classpathButton, "wrap");
		classpathButton.addActionListener(this);

		setMessage(panel);
	}

	public String getJavaOptions() {
		return optionsField.getText();
	}

	public String getMain() {
		return mainsButton.isSelected() ? (String) mainsComboBox
				.getSelectedItem() : customStartField.getText();
	}

	public String getProgramArgs() {
		return argumentsField.getText();
	}

	public File getClassPath() {
		return new File(classPathField.getText());
	}

	private boolean isFormValid() {
		if ((customButton.isSelected() && customStartField.getText().isEmpty())) {
			UIHelper.getInstance().displayMessage(
					Messages.get("custom.start.empty"),
					UIHelper.MessageType.ERROR);
			return false;
		}
		File classPathDir = new File(classPathField.getText());
		if (!classPathDir.canRead() || !classPathDir.isDirectory()) {
			UIHelper.getInstance().displayMessage(
					Messages.get("file.not.valid"), UIHelper.MessageType.ERROR);
			return false;
		}
		UIHelper.getInstance().setCurrentDirectory(classPathDir);
		return true;
	}

	@Override
	public void setValue(Object newValue) {
		if (newValue == null) {
			newValue = CANCEL_OPTION;
		}
		if (newValue.equals(OK_OPTION)) {
			if (!isFormValid())
				return;
		}
		super.setValue(newValue);
	}

	private void changeClassPath() {
		JFileChooser fc = new JFileChooser(UIHelper.getInstance()
				.getCurrentDirectory());
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return Messages.get("directories.only");
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}
		});

		if (fc.showOpenDialog(UIHelper.getInstance().getMainFrame()) != JFileChooser.APPROVE_OPTION)
			return;

		classPathField.setText(fc.getSelectedFile().toString());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == classpathButton) {
			changeClassPath();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == mainsButton) {
			setMainsEnabled(mainsButton.isSelected());
		} else if (e.getSource() == customButton) {
			setCustomStartEnabled(customButton.isSelected());
		}
	}
}
