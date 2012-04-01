/*
 * ColorsPanel.java
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

package travis.view.settings;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import travis.controller.UIHelper;
import travis.util.Messages;
import travis.view.Util;
import travis.view.color.Eclipse2Color;
import travis.view.color.EclipseColor;
import travis.view.color.ModifiableColor;
import travis.view.color.StructColor;
import travis.view.color.VividColor;

public class ColorsPanel extends SettingsTab implements ActionListener {

    private static final long serialVersionUID = -8944582429320845020L;
    private static final String COLOR_CONSTRAINTS = "align right, h 20!, w 45!";
    public static final StructColor DIMMED = new EclipseColor();
    public static final StructColor BRIGHT = new Eclipse2Color();
    public static final StructColor VIVID = new VividColor();
    public static final StructColor WHITE = new ModifiableColor();

    public static final int DIMMED_INDEX = 0;
    public static final int BRIGHT_INDEX = 1;
    public static final int VIVID_INDEX = 2;
    public static final int WHITE_INDEX = 3;
    public static final int USER_INDEX = 4;

    private final JComboBox themesBox;

    private final ColorRow packageColor;

    private final ColorRow ordinaryClassColor;
    private final ColorRow abstractClassColor;
    private final ColorRow interfaceColor;
    private final ColorRow enumColor;

    private final ColorRow publicMethodColor;
    private final ColorRow privateMethodColor;
    private final ColorRow protectedMethodColor;
    private final ColorRow defaultMethodColor;

    private final ColorRow executionPointColor;

    private ModifiableColor userColors;

    public ColorsPanel() {
        super(new MigLayout("wrap 1, insets 0, fillx"));

        themesBox = new JComboBox(new String[]{"Eclipse", "Eclipse 2",
                "Vivid", "White", "User Defined"});
        themesBox.addActionListener(this);
        Settings.getInstance().setColors(DIMMED);
        add(Util.createBorderedPanel(Messages.get("themes"), themesBox), "grow");

        packageColor = new ColorRow(Messages.get("packages"));

        ordinaryClassColor = new ColorRow(Messages.get("ordinary.classes"));
        abstractClassColor = new ColorRow(Messages.get("abstract.classes"));
        interfaceColor = new ColorRow(Messages.get("interfaces"));
        enumColor = new ColorRow(Messages.get("enums"));

        publicMethodColor = new ColorRow(Messages.get("public.methods"));
        privateMethodColor = new ColorRow(Messages.get("private.methods"));
        protectedMethodColor = new ColorRow(Messages.get("protected.methods"));
        defaultMethodColor = new ColorRow(Messages.get("default.methods"));

        executionPointColor = new ColorRow(Messages.get("execution.point"));

        add(Util.createBorderedPanel(Messages.get("colors"), "wrap 1",
                packageColor, new JSeparator(SwingConstants.HORIZONTAL),
                ordinaryClassColor, abstractClassColor, interfaceColor,
                enumColor, new JSeparator(SwingConstants.HORIZONTAL),
                publicMethodColor, privateMethodColor, protectedMethodColor,
                defaultMethodColor, new JSeparator(SwingConstants.HORIZONTAL),
                executionPointColor), "grow");

        updateValues();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == themesBox) {
            if (themesBox.getSelectedIndex() == DIMMED_INDEX) {
                Settings.getInstance().setColors(DIMMED);
            } else if (themesBox.getSelectedIndex() == BRIGHT_INDEX) {
                Settings.getInstance().setColors(BRIGHT);
            } else if (themesBox.getSelectedIndex() == VIVID_INDEX) {
                Settings.getInstance().setColors(VIVID);
            } else if (themesBox.getSelectedIndex() == WHITE_INDEX) {
                Settings.getInstance().setColors(WHITE);
            } else if (themesBox.getSelectedIndex() == USER_INDEX) {
                if (userColors == null)
                    userColors = Settings.getInstance().getColors();

                Settings.getInstance().setColors(userColors);
            }

        }
    }

    @Override
    public void updateValues() {
        ModifiableColor colors = Settings.getInstance().getColors();
        packageColor.setColor(colors.getPackageColor());

        ordinaryClassColor.setColor(colors.getOrdinaryClassColor());
        abstractClassColor.setColor(colors.getAbstractClassColor());
        interfaceColor.setColor(colors.getInterfaceColor());
        enumColor.setColor(colors.getEnumColor());

        publicMethodColor.setColor(colors.getPublicMethodColor());
        privateMethodColor.setColor(colors.getPrivateMethodColor());
        protectedMethodColor.setColor(colors.getProtectedMethodColor());
        defaultMethodColor.setColor(colors.getDefaultMethodColor());

        executionPointColor.setColor(colors.getExecutionPointColor());
    }

    public void updateColors() {
        if (userColors == null)
            userColors = Settings.getInstance().getColors();

        userColors.setPackageColor(packageColor.getColor());

        userColors.setOrdinaryClassColor(ordinaryClassColor.getColor());
        userColors.setAbstractClassColor(abstractClassColor.getColor());
        userColors.setInterfaceColor(interfaceColor.getColor());
        userColors.setEnumColor(enumColor.getColor());

        userColors.setPublicMethodColor(publicMethodColor.getColor());
        userColors.setPrivateMethodColor(privateMethodColor.getColor());
        userColors.setProtectedMethodColor(protectedMethodColor.getColor());
        userColors.setDefaultMethodColor(defaultMethodColor.getColor());

        userColors.setExecutionPointColor(executionPointColor.getColor());

        Settings.getInstance().setColors(userColors);
    }

    private class ColorRow extends JPanel {
        private static final long serialVersionUID = -7582222745117660040L;

        private final JPanel panel;
        private final JLabel label;

        public ColorRow(final String text) {
            super(new MigLayout("wrap 2, insets 2"));

            panel = createColorPanel();
            label = new JLabel(text);

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String title = String.format("%s %s", text,
                            Messages.get("color"));

                    Color c = JColorChooser.showDialog(UIHelper.getInstance()
                            .getMainFrame(), title, panel.getBackground());
                    if (c != null) {
                        panel.setBackground(c);
                        updateColors();
                        themesBox.setSelectedIndex(USER_INDEX);
                    }
                }
            };

            panel.addMouseListener(mouseAdapter);
            label.addMouseListener(mouseAdapter);

            add(panel, COLOR_CONSTRAINTS);
            add(label);
        }

        public Color getColor() {
            return panel.getBackground();
        }

        public void setColor(Color color) {
            panel.setBackground(color);
        }

        private JPanel createColorPanel() {
            JPanel panel = new JPanel();
            panel.setBorder(BorderFactory.createRaisedBevelBorder());
            return panel;
        }

    }

}
