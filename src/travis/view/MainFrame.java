/*
 * MainFrame.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;
import travis.controller.UIGraphicsHelper;
import travis.controller.UIHelper;
import travis.util.Messages;

public class MainFrame extends JFrame implements ActionListener {

    private static final long serialVersionUID = -5320515317070072419L;

    private static final int OPTION_KEY;

    static {
        boolean isMacOs = System.getProperty("os.name").matches("Mac.*");
        OPTION_KEY = isMacOs ? ActionEvent.META_MASK : ActionEvent.CTRL_MASK;
    }

    private final JMenuBar menuBar;
    private final JTabbedPane tabbedPane;

    private JMenuItem saveSubtrace;
    private JMenuItem saveAs;

    public MainFrame() {
        super(Messages.get("app.name"));
        this.setLayout(new MigLayout("fill, hidemode 2"));
        this.setBackground(Color.WHITE);

        menuBar = new JMenuBar();
        tabbedPane = new JTabbedPane();

        setupMenuBar();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1125, 600));
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(d);
        this.pack();
    }

    public void populateFrame() {
        tabbedPane.addTab(Messages.get("monitors"), UIHelper.getInstance()
                .getAttacherPanel());
        tabbedPane.addTab(Messages.get("console"), UIHelper.getInstance()
                .getConsolePanel());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                UIHelper.getInstance().getTreePanel(), tabbedPane);

        this.add(splitPane, "w 20%!, grow");
        this.add(UIHelper.getInstance().getGraphLayeredPane(), "w 80%, grow");
        this.add(UIHelper.getInstance().getSettingsPane(), "w 20%!, grow");
        this.add(UIHelper.getInstance().getPlaybackPanel(), "south, h 50px!");

        validate();
        this.setVisible(true);
    }

    public void setAttachMode() {
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.setSelectedIndex(0);

        UIHelper.getInstance().getPlaybackPanel().setEnabled(false);
        saveSubtrace.setEnabled(false);
        saveAs.setEnabled(true);
    }

    public void setPlaybackMode() {
        tabbedPane.setEnabledAt(0, false);
        tabbedPane.setSelectedIndex(1);

        UIHelper.getInstance().getPlaybackPanel().setEnabled(true);
        saveSubtrace.setEnabled(true);
        saveAs.setEnabled(false);
    }

    private void setupMenuBar() {
        // File
        JMenu menu = new JMenu(Messages.get("file"));
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem(Messages.get("open.classpath"),
                KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                OPTION_KEY));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem(Messages.get("attach.classpath"),
                KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                OPTION_KEY | ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(Messages.get("open.file"), KeyEvent.VK_F);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                OPTION_KEY | ActionEvent.SHIFT_MASK));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        saveAs = new JMenuItem(Messages.get("save.as"), KeyEvent.VK_S);
        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, OPTION_KEY));
        saveAs.addActionListener(this);
        menu.add(saveAs);

        menu.addSeparator();

        saveSubtrace = new JMenuItem(Messages.get("save.subtrace"),
                KeyEvent.VK_T);
        saveSubtrace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                OPTION_KEY));
        saveSubtrace.addActionListener(this);
        menu.add(saveSubtrace);

        // Graph
        menu = new JMenu(Messages.get("graph"));
        menu.setMnemonic(KeyEvent.VK_G);
        menuBar.add(menu);

        menuItem = new JMenuItem(Messages.get("reset.traces"), KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                OPTION_KEY));
        menuItem.addActionListener(this);
        menu.add(menuItem);

        this.setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(Messages.get("open.classpath"))) {
            UIHelper.getInstance().openClasspath();
        } else if (e.getActionCommand()
                .equals(Messages.get("attach.classpath"))) {
            UIHelper.getInstance().attachClasspath();
        } else if (e.getActionCommand().equals(Messages.get("open.file"))) {
            UIHelper.getInstance().openFile();
        } else if (e.getActionCommand().equals(Messages.get("save.as"))) {
            UIHelper.getInstance().saveFileAs();
        } else if (e.getActionCommand().equals(Messages.get("save.subtrace"))) {
            UIHelper.getInstance().saveSubtrace();
        } else if (e.getActionCommand().equals(Messages.get("reset.traces"))) {
            UIGraphicsHelper.getInstance().resetConnectionsAndRepaintGraph();
        }
    }

}
