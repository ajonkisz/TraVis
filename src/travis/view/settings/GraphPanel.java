/*
 * GraphPanel.java
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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import travis.util.Messages;
import travis.view.Util;

public class GraphPanel extends SettingsTab implements ChangeListener,
        ItemListener {

    private static final long serialVersionUID = 135450811585232513L;

    private final Settings sets = Settings.getInstance();

    private final JSlider rotateSlider;
    private final JSlider bundlingSlider;

    private final JCheckBox minDepth;
    private final JCheckBox drawInnerLayout;

    private final JSlider executionPointSlider;

    public GraphPanel() {
        super(new MigLayout("wrap 1, insets 0, fillx"));

        rotateSlider = Util.createSlider(-360, 360, 0, 180, 90);
        rotateSlider.addChangeListener(this);
        bundlingSlider = Util.createSlider(0, 100, 0, 10, 5);
        bundlingSlider.addChangeListener(this);

        executionPointSlider = Util.createSlider(2, 10, 2, 2, 1);
        executionPointSlider.addChangeListener(this);

        minDepth = new JCheckBox(Messages.get("min.depth"));
        minDepth.addItemListener(this);

        drawInnerLayout = new JCheckBox(Messages.get("draw.inner.layout"));
        drawInnerLayout.addItemListener(this);

        add(Util.createBorderedPanel(Messages.get("rotate"), rotateSlider),
                "grow");
        add(Util.createBorderedPanel(Messages.get("bundling.strength"),
                bundlingSlider), "grow");

        add(Util.createBorderedPanel(Messages.get("inner.layout"), minDepth),
                "grow");
        add(Util.createBorderedPanel(Messages.get("debug"), drawInnerLayout),
                "grow");
        add(Util.createBorderedPanel(Messages.get("execution.point.size"),
                executionPointSlider), "grow");

        updateValues();
    }

    @Override
    public void updateValues() {
        rotateSlider.setValue(sets.getGraphRotate());
        bundlingSlider.setValue((int) (sets.getCurveBundlingStrength() * 100));

        minDepth.setSelected(sets.isMinDepth());
        drawInnerLayout.setSelected(sets.isDrawingInnerLayout());

        executionPointSlider.setValue(sets.getExecutionPointSize());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == drawInnerLayout) {
            sets.setDrawingInnerLayout(drawInnerLayout.isSelected());
        } else if (e.getSource() == minDepth) {
            sets.setMinDepth(minDepth.isSelected());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == rotateSlider) {
            if (sets.getGraphRotate() == rotateSlider.getValue())
                return;

            sets.setGraphRotate(rotateSlider.getValue());
        } else if (e.getSource() == bundlingSlider) {
            double value = (double) bundlingSlider.getValue() / 100;
            if (sets.getCurveBundlingStrength() == value)
                return;

            sets.setCurveBundlingStrength(value);
        } else if (e.getSource() == executionPointSlider) {
            if (sets.getExecutionPointSize() == executionPointSlider.getValue())
                return;

            sets.setExecutionPointSize(executionPointSlider.getValue());
        }
    }

}
