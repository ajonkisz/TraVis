/*
 * AttachersTable.java
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

        Vector<Attacher> attachers = UIHelper.getInstance()
                .getAttachers();
        for (Attacher attacher : attachers) {
            addRow(attacher);
        }
        validate();
    }

}
