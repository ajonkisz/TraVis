/*
 * TracesPanel.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormatSymbols;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import travis.util.Messages;
import travis.view.Util;

public class TracesPanel extends SettingsTab implements ChangeListener,
        ItemListener, ActionListener {

    private static final long serialVersionUID = 8898812372626112784L;

    private final Settings sets = Settings.getInstance();

    private final JSlider cachedTracesSlider;

    private final JSlider curvesNoSlider;
    private final JLabel curvesNoLabel;
    private final JCheckBox maxCurvesBox;
    private final JRadioButton uniqueTraces;
    private final JRadioButton latestTraces;

    private final JSlider curvesPerSecSlider;

    public TracesPanel() {
        super(new MigLayout("wrap 1, insets 0, fillx"));

        cachedTracesSlider = Util.createSlider(0, 100, 0, 10, 5);
        cachedTracesSlider.addChangeListener(this);

        curvesNoSlider = Util.createSlider(0, 200, 0, 50, 25);
        curvesNoSlider.addChangeListener(this);

        curvesNoLabel = new JLabel();

        maxCurvesBox = new JCheckBox(Messages.get("no.max"));
        maxCurvesBox.addItemListener(this);

        ButtonGroup group = new ButtonGroup();
        uniqueTraces = new JRadioButton(Messages.get("unique.traces"));
        uniqueTraces.addActionListener(this);
        group.add(uniqueTraces);
        latestTraces = new JRadioButton(Messages.get("latest.traces"));
        latestTraces.addActionListener(this);
        group.add(latestTraces);

        curvesPerSecSlider = Util.createSlider(0, 25, 0, 5, 1);
        curvesPerSecSlider.addChangeListener(this);

        add(Util.createBorderedPanel(Messages.get("cached.traces.thousands"),
                cachedTracesSlider), "grow");

        JPanel panel = new JPanel(new MigLayout("fillx, insets 0"));
        panel.add(maxCurvesBox);
        panel.add(curvesNoLabel, "align right, wrap");
        panel.add(uniqueTraces, "wrap");
        panel.add(latestTraces);
        add(Util.createBorderedPanel(Messages.get("max.drawn.traces"),
                "wrap 1", curvesNoSlider, panel), "grow");

        add(Util.createBorderedPanel(Messages.get("traces.persec"),
                curvesPerSecSlider), "grow");

        updateValues();
    }

    @Override
    public void updateValues() {
        cachedTracesSlider.setValue(sets.getCachedTracesNo() / 1000);
        curvesNoSlider.setValue(sets.getMaxCurvesNo());
        curvesPerSecSlider.setValue(sets.getCurvesPerSec());

        uniqueTraces.setSelected(sets.isDrawingUniqueTraces());
        latestTraces.setSelected(!sets.isDrawingUniqueTraces());

        updateCurvesNoLabel();
    }

    private void updateCurvesNoLabel() {
        if (maxCurvesBox.isSelected()) {
            curvesNoLabel.setText(DecimalFormatSymbols.getInstance()
                    .getInfinity());
        } else {
            curvesNoLabel.setText(Integer.toString(sets.getMaxCurvesNo()));
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == maxCurvesBox) {
            curvesNoSlider.setEnabled(!maxCurvesBox.isSelected());
            if (maxCurvesBox.isSelected()) {
                sets.setMaxCurvesNo(Integer.MAX_VALUE - 1);
            } else {
                sets.setMaxCurvesNo(curvesNoSlider.getValue());
            }
            updateCurvesNoLabel();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == curvesNoSlider) {
            if (sets.getMaxCurvesNo() == curvesNoSlider.getValue())
                return;

            sets.setMaxCurvesNo(curvesNoSlider.getValue());
        } else if (e.getSource() == curvesPerSecSlider) {
            if (sets.getCurvesPerSec() == curvesPerSecSlider.getValue())
                return;

            sets.setCurvesPerSec(curvesPerSecSlider.getValue());
        } else if (e.getSource() == cachedTracesSlider) {
            int value = cachedTracesSlider.getValue() * 1000;
            if (sets.getCachedTracesNo() == value)
                return;

            sets.setCachedTracesNo(value);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uniqueTraces || e.getSource() == latestTraces) {
            if (sets.isDrawingUniqueTraces() != uniqueTraces.isSelected())
                sets.setDrawingUniqueTraces(uniqueTraces.isSelected());
        }
    }

}
