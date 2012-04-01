/*
 * ClassDialogPanel.java
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
