/*
 * ConsolePanel.java
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

package travis.view.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;
import travis.model.script.ScriptPrinter;
import travis.util.Messages;

public class ConsolePanel extends JPanel implements ActionListener, Printer {

    private static final long serialVersionUID = -8355796638620826169L;

    private final JTextArea console;
    private final JScrollPane pane;
    private final Timer scrollTimer;

    public ConsolePanel() {
        super(new MigLayout("fill, insets 0"));
        scrollTimer = new Timer(30, this);
        scrollTimer.setRepeats(false);

        ScriptPrinter.getInstance().addListeningPrinter(this);
        System.setErr(new ErrOut(System.err));

        console = new JTextArea();
        console.setEditable(false);
        console.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem item = new JMenuItem(
                            Messages.get("clear.console"));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            console.setText("");
                        }
                    });
                    menu.add(item);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        pane = new JScrollPane(console);
        add(pane, "grow");
    }

    private class ErrOut extends PrintStream {

        public ErrOut(OutputStream out) {
            super(out);
        }

        @Override
        public void write(byte[] buf, int off, int len) {
            super.write(buf, off, len);
            console.append(new String(buf, off, len));
            scrollTimer.restart();
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pane.getVerticalScrollBar().setValue(
                pane.getVerticalScrollBar().getMaximum());
    }

    @Override
    public void write(String s) {
        console.append(s);
        scrollTimer.restart();
    }

}
