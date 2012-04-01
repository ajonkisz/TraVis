/*
 * MethodDialogPanel.java
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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import travis.model.project.StructStub;
import travis.util.Messages;

public class MethodDialogPanel extends DialogPanel {

    private static final String STYLE = "<style type='text/css'>"
            + "table {background-color: #000000; padding: 1px 0px 0px 1px}"
            + "th {background-color: #ffffff; margin:0px 0.5px 0.5px 0px}"
            + "td {background-color: #ffffff; margin:0.5px 0.5px 0px 0px; font-family: 'Monaco'}"
            + "</style>";
    private static final String INFO_RETURN = "<html>" + STYLE
            + "<body><table>" + "<tr><th>" + Messages.get("java.type")
            + "</th><th>" + Messages.get("type.desc") + "</th></tr>"
            + "<tr><td>void</td><td>V</td></tr>"
            + "<tr><td>boolean</td><td>Z</td></tr>"
            + "<tr><td>char</td><td>C</td></tr>"
            + "<tr><td>byte</td><td>B</td></tr>"
            + "<tr><td>short</td><td>S</td></tr>"
            + "<tr><td>int</td><td>I</td></tr>"
            + "<tr><td>float</td><td>F</td></tr>"
            + "<tr><td>long</td><td>J</td></tr>"
            + "<tr><td>double</td><td>D</td></tr>"
            + "<tr><td>Object</td><td>Ljava.lang.Object;</td></tr>"
            + "<tr><td>int</td><td>[I</td></tr>"
            + "<tr><td>Object[][]</td><td>[[Ljava.lang.Object;</td></tr>"
            + "</table></body></html>";
    private static final String INFO_PARAM = "<html>"
            + STYLE
            + "<body><table>"
            + "<tr><th>"
            + Messages.get("parameter.source")
            + "</th><th>"
            + Messages.get("method.descriptor")
            + "</th></tr>"
            + "<tr><td>int i, float f</td><td>IF</td></tr>"
            + "<tr><td>Object o</td><td>Ljava.lang.Object;</td></tr>"
            + "<tr><td>int i, String s</td><td>ILjava.lang.String;</td></tr>"
            + "<tr><td>int[] i</td><td>[I</td></tr>"
            + "<tr><td>Object o, String s</td><td>Ljava.lang.Object;Ljava.lang.String;</td></tr>"
            + "</table></body></html>";

    private final JComboBox visibility;
    private final JComboBox modifier;
    private final JTextField returnField;
    private final JTextField parametersField;

    public MethodDialogPanel() {
        super();

        add(new JLabel(Messages.get("new.class.desc")), "span, wrap");

        add(new JLabel(Messages.get("visibility")), "center");
        add(new JLabel(Messages.get("modifier")), "center");
        add(new JLabel(Messages.get("return.type")), "center");
        add(new JLabel(Messages.get("name")), "center");
        add(new JLabel(Messages.get("parameters")), "center, span, wrap");

        visibility = new JComboBox(StructStub.Visibility.values());
        visibility.setSelectedItem(StructStub.Visibility.PUBLIC);
        add(visibility);

        modifier = new JComboBox(StructStub.Modifier.values());
        modifier.setSelectedItem(StructStub.Modifier.NONE);
        add(modifier);

        returnField = new JTextField("V", 10);
        add(returnField);

        add(nameField);

        add(new JLabel("("));
        parametersField = new JTextField("", 14);
        add(parametersField);
        add(new JLabel(")"), "wrap");

        JTextPane infoArea = new JTextPane();
        infoArea.setEditable(false);

        add(new JLabel("<html><font style='font-weight: bold; color: #ff0000'>"
                + "<center>" + Messages.get("new.method.warning")
                + "</center></font></html>"), "span, center, wrap");
        JPanel panel = new JPanel(new MigLayout());
        add(panel, "span");
        panel.add(new JLabel(Messages.get("return.type.eg")), "center");
        panel.add(new JLabel(Messages.get("param.type.eg")), "center, wrap");

        JTextPane textPaneRet = new JTextPane();
        textPaneRet.setEditable(false);
        textPaneRet.setContentType("text/html");
        textPaneRet.setText(INFO_RETURN);
        panel.add(textPaneRet, "top, center");

        JTextPane textPaneParam = new JTextPane();
        textPaneParam.setEditable(false);
        textPaneParam.setContentType("text/html");
        textPaneParam.setText(INFO_PARAM);
        panel.add(textPaneParam, "top, center");
    }

    @Override
    public void closing() {
        super.closing();
        stub.setVisibility((StructStub.Visibility) visibility
                .getSelectedItem());
        stub.setModifier((StructStub.Modifier) modifier.getSelectedItem());
        stub.setDescriptor("(" + parametersField.getText() + ")"
                + returnField.getText());
    }

}
