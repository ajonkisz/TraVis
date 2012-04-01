/*
 * AttacherPanel.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import travis.controller.UIHelper;
import travis.model.attach.Attacher;
import travis.model.attach.AttacherFactory;
import travis.model.attach.JavaProcess;
import travis.model.project.structure.StructComponent;
import travis.util.Messages;
import travis.view.project.tree.NewProcessPane;

public class AttacherPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -4423926752303119759L;

    private final JButton attachButton;
    private final JButton startButton;
    private final JButton killAllButton;
    private final AttachersTable attachersTable;

    public AttacherPanel() {
        super(new MigLayout("fill, insets 0", "",
                "[growprio 0][growprio 0][growprio 100]"));

        attachButton = new JButton(Messages.get("attach"));
        attachButton.addActionListener(this);
        startButton = new JButton(Messages.get("start"));
        startButton.addActionListener(this);
        killAllButton = new JButton(Messages.get("kill.all"));
        killAllButton.addActionListener(this);

        attachersTable = new AttachersTable();
        JScrollPane scrollPane = new JScrollPane(attachersTable);

        this.add(attachButton, "span, split 2");
        this.add(startButton, "wrap");
        this.add(killAllButton, "span, wrap");
        this.add(scrollPane, "span, grow");
    }

    public AttachersTable getAttachersTable() {
        return attachersTable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(attachButton)) {
            attachToProcess();
        } else if (e.getSource().equals(startButton)) {
            startNewProcess();
        } else if (e.getSource().equals(killAllButton)) {
            UIHelper.getInstance().askToKillAllIfConnected();
        }
    }

    private void attachToProcess() {
        JavaProcess process = getProcess();
        if (process == null)
            return;

        Attacher attacher = AttacherFactory.newAttacher(process);
        UIHelper.getInstance().addAndStartAttacher(attacher);
    }

    private JavaProcess getProcess() {
        Collection<JavaProcess> processes = JavaProcess.getVMList();
        if (processes.isEmpty()) {
            UIHelper.getInstance().displayMessage(
                    Messages.get("no.processes.found"),
                    UIHelper.MessageType.INFORMATION);
            return null;
        }

        Object[] possibilities = processes.toArray();
        JavaProcess jp = (JavaProcess) JOptionPane.showInputDialog(UIHelper
                .getInstance().getMainFrame(), Messages.get("process.choose"),
                Messages.get("process"), JOptionPane.PLAIN_MESSAGE, null,
                possibilities, possibilities[0]);

        if (jp != null) {
            return jp;
        }

        return null;
    }

    private void startNewProcess() {
        StructComponent root = UIHelper.getInstance().getProjectTree()
                .getRoot();
        NewProcessPane processPane = new NewProcessPane(
                root.getMainMethodComponents());
        if (!processPane.getValue().equals(JOptionPane.OK_OPTION))
            return;

        Attacher attacher = AttacherFactory.newAttacher(
                processPane.getJavaOptions(), processPane.getMain(),
                processPane.getProgramArgs(), processPane.getClassPath());
        UIHelper.getInstance().addAndStartAttacher(attacher);
    }

}
