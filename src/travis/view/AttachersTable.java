package travis.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import travis.controller.UIHelper;
import travis.model.attach.Attacher;
import travis.util.Messages;

public class AttachersTable extends JPanel {

	private static final long serialVersionUID = 2392181370949593863L;

	private final JPanel tablePanel;

	public AttachersTable() {
		super(new MigLayout("fillx, insets 0, wrap 4", "center", "top"));
		tablePanel = new JPanel(new MigLayout("fillx, insets 1, wrap 4, gap 1",
				"center", "top"));
		tablePanel.setBackground(Color.BLACK);

		populateHeaders();

		add(tablePanel, "growx");
	}

	private void populateHeaders() {
		addHeaderForText(Messages.get("monitor"));
		addHeaderForText(Messages.get("pid"));
		addHeaderForText(Messages.get("name"));
		addHeaderForText(Messages.get("kill"));
	}

	private void addHeaderForText(String text) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);

		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);

		panel.add(label);
		tablePanel.add(panel, "growx");
	}

	private void addRow(final Attacher attacher) {
		addCell(attacher.getDescriptor());
		addCell(attacher.getPid());
		addCell(attacher.getName());

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		JButton button = new JButton(Messages.get("kill"));
		panel.add(button);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (UIHelper.getInstance().displayConfirmation(
						Messages.get("kill.confirm")))
					UIHelper.getInstance().removeAndKillAttacher(attacher);
			}
		});
		tablePanel.add(panel, "left, growx");
	}

	private void addCell(String text) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		JLabel label = new JLabel(text);
		panel.add(label);
		tablePanel.add(panel, "left, grow");
	}

	public void repaintTable() {
		tablePanel.removeAll();
		populateHeaders();

		Vector<Attacher> attachers = (Vector<Attacher>) UIHelper.getInstance()
				.getAttachers();
		for (Attacher attacher : attachers) {
			addRow(attacher);
		}
		validate();
	}

}
