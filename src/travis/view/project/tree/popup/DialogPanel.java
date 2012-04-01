/*
 * DialogPanel.java
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

package travis.view.project.tree.popup;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import travis.model.project.StructStub;

public abstract class DialogPanel {

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
