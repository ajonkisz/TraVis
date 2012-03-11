package travis.view.settings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import travis.util.Messages;
import travis.view.Util;

public class VisibilityPanel extends SettingsTab implements ItemListener,
		ChangeListener {

	private static final long serialVersionUID = -7394851137632080201L;

	private final Settings sets = Settings.getInstance();
	private final JSlider packageLayerHideSlider;

	private final JCheckBox packagesVis;
	private final JCheckBox classesVis;
	private final JCheckBox methodsVis;

	private final JCheckBox ordinaryClassesVis;
	private final JCheckBox abstractClassesVis;
	private final JCheckBox interfacesVis;
	private final JCheckBox enumsVis;

	private final JCheckBox publicMethodVis;
	private final JCheckBox privateMethodVis;
	private final JCheckBox protectedMethodVis;
	private final JCheckBox defaultMethodVis;

	public VisibilityPanel() {
		super(new MigLayout("wrap 1, insets 0, fillx"));

		packageLayerHideSlider = Util.createSlider(0, 10, 0, 2, 1);
		packageLayerHideSlider.addChangeListener(this);

		packagesVis = new JCheckBox(Messages.get("packages"), true);
		packagesVis.addItemListener(this);
		classesVis = new JCheckBox(Messages.get("classes"), true);
		classesVis.addItemListener(this);
		methodsVis = new JCheckBox(Messages.get("methods"), true);
		methodsVis.addItemListener(this);

		ordinaryClassesVis = new JCheckBox(Messages.get("ordinary.classes"),
				true);
		ordinaryClassesVis.addItemListener(this);
		abstractClassesVis = new JCheckBox(Messages.get("abstract.classes"),
				true);
		abstractClassesVis.addItemListener(this);
		interfacesVis = new JCheckBox(Messages.get("interfaces"), true);
		interfacesVis.addItemListener(this);
		enumsVis = new JCheckBox(Messages.get("enums"), true);
		enumsVis.addItemListener(this);

		publicMethodVis = new JCheckBox(Messages.get("public"), true);
		publicMethodVis.addItemListener(this);
		privateMethodVis = new JCheckBox(Messages.get("private"), true);
		privateMethodVis.addItemListener(this);
		protectedMethodVis = new JCheckBox(Messages.get("protected"), true);
		protectedMethodVis.addItemListener(this);
		defaultMethodVis = new JCheckBox(Messages.get("default"), true);
		defaultMethodVis.addItemListener(this);

		JPanel classGroup = Util.createBorderedPanel(
				Messages.get("class.group"), "wrap 1", ordinaryClassesVis,
				abstractClassesVis, interfacesVis, enumsVis);
		JPanel methodGroup = Util.createBorderedPanel(
				Messages.get("methods.group"), "wrap 1", publicMethodVis,
				privateMethodVis, protectedMethodVis, defaultMethodVis);
		JPanel visibility = Util.createBorderedPanel(
				Messages.get("visibility"), "wrap 1", packagesVis, classesVis,
				methodsVis);
		visibility.add(classGroup, "grow");
		visibility.add(methodGroup, "grow");
		add(visibility, "grow");

		add(Util.createBorderedPanel(Messages.get("layer.package.hide"),
				packageLayerHideSlider), "grow");

		updateValues();
	}

	@Override
	public void updateValues() {
		packageLayerHideSlider.setValue(sets.getGraphPackageLayersToHide());

		if (sets.isPackageEnabled()) {
			packagesVis.setSelected(sets
					.isDrawingStruct(Settings.STRUCT_PACKAGE));
			packagesVis.setEnabled(true);
			checkVisibilityFlags(packagesVis, classesVis, methodsVis);
		} else {
			packagesVis.setSelected(false);
			packagesVis.setEnabled(false);
			enablePackageLayerSlider(false);
		}

		classesVis.setSelected(sets.isDrawingStruct(Settings.STRUCT_CLASS));
		methodsVis.setSelected(sets.isDrawingStruct(Settings.STRUCT_METHOD));

		ordinaryClassesVis.setSelected(sets
				.isDrawingStruct(Settings.STRUCT_ORDINARY_CLASS));
		abstractClassesVis.setSelected(sets
				.isDrawingStruct(Settings.STRUCT_ABSTRACT_CLASS));
		interfacesVis.setSelected(sets
				.isDrawingStruct(Settings.STRUCT_INTERFACE));
		enumsVis.setSelected(sets.isDrawingStruct(Settings.STRUCT_ENUM));

		publicMethodVis.setSelected(sets
				.isDrawingStruct(Settings.STRUCT_PUBLIC_METHOD));
		privateMethodVis.setSelected(sets
				.isDrawingStruct(Settings.STRUCT_PRIVATE_METHOD));
		protectedMethodVis.setSelected(sets
				.isDrawingStruct(Settings.STRUCT_PROTECTED_METHOD));
		defaultMethodVis.setSelected(sets
				.isDrawingStruct(Settings.STRUCT_DEFAULT_METHOD));
	}

	private void checkVisibilityFlags(JCheckBox... boxes) {
		checkVisibilityFlags(true, boxes);
	}

	private void checkVisibilityFlags(boolean isParentEnabled,
			JCheckBox... boxes) {
		int selected = 0;
		for (JCheckBox box : boxes) {
			if (box.isSelected())
				selected++;
		}

		if (selected <= 0) {
			throw new IllegalStateException(
					Messages.get("visibility.flag.exception"));
		} else if (selected == 1) {
			for (JCheckBox box : boxes) {
				if (box.isSelected())
					box.setEnabled(false);
			}
		} else if (selected > 1) {
			for (JCheckBox box : boxes) {
				if (box.isSelected() && isParentEnabled)
					box.setEnabled(true);
			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED
				|| e.getStateChange() == ItemEvent.SELECTED) {
			checkVisibilityFlags(packagesVis, classesVis, methodsVis);
			checkVisibilityFlags(classesVis.isSelected(), ordinaryClassesVis,
					abstractClassesVis, interfacesVis, enumsVis);
			checkVisibilityFlags(methodsVis.isSelected(), publicMethodVis,
					privateMethodVis, protectedMethodVis, defaultMethodVis);
		}

		if (e.getSource() == packagesVis) {
			if (!sets.isPackageEnabled())
				return;
			sets.drawStruct(Settings.STRUCT_PACKAGE, packagesVis.isSelected());
		} else if (e.getSource() == classesVis) {
			sets.drawStruct(Settings.STRUCT_CLASS, classesVis.isSelected());
			setClassGroupEnabled(classesVis.isSelected());
		} else if (e.getSource() == methodsVis) {
			sets.drawStruct(Settings.STRUCT_METHOD, methodsVis.isSelected());
			setMethodGroupEnabled(methodsVis.isSelected());
		} else if (e.getSource() == ordinaryClassesVis) {
			sets.drawStruct(Settings.STRUCT_ORDINARY_CLASS,
					ordinaryClassesVis.isSelected());
		} else if (e.getSource() == abstractClassesVis) {
			sets.drawStruct(Settings.STRUCT_ABSTRACT_CLASS,
					abstractClassesVis.isSelected());
		} else if (e.getSource() == interfacesVis) {
			sets.drawStruct(Settings.STRUCT_INTERFACE,
					interfacesVis.isSelected());
		} else if (e.getSource() == enumsVis) {
			sets.drawStruct(Settings.STRUCT_ENUM, enumsVis.isSelected());
		} else if (e.getSource() == publicMethodVis) {
			sets.drawStruct(Settings.STRUCT_PUBLIC_METHOD,
					publicMethodVis.isSelected());
		} else if (e.getSource() == privateMethodVis) {
			sets.drawStruct(Settings.STRUCT_PRIVATE_METHOD,
					privateMethodVis.isSelected());
		} else if (e.getSource() == protectedMethodVis) {
			sets.drawStruct(Settings.STRUCT_PROTECTED_METHOD,
					protectedMethodVis.isSelected());
		} else if (e.getSource() == defaultMethodVis) {
			sets.drawStruct(Settings.STRUCT_DEFAULT_METHOD,
					defaultMethodVis.isSelected());
		}

		enablePackageLayerSlider(packagesVis.isEnabled());
	}

	private void enablePackageLayerSlider(boolean enabled) {
		if (enabled) {
			packageLayerHideSlider.setEnabled(true);
		} else {
			packageLayerHideSlider.setValue(0);
			packageLayerHideSlider.setEnabled(false);
		}
	}

	private void setGroupEnabled(boolean enabled, JCheckBox... boxes) {
		for (JCheckBox box : boxes) {
			box.setEnabled(enabled);
		}
	}

	private void setMethodGroupEnabled(boolean enabled) {
		if (!enabled) {
			sets.drawStruct(Settings.STRUCT_PUBLIC_METHOD
					| Settings.STRUCT_PRIVATE_METHOD
					| Settings.STRUCT_PROTECTED_METHOD
					| Settings.STRUCT_DEFAULT_METHOD, true);
		}

		setGroupEnabled(enabled, publicMethodVis, privateMethodVis,
				protectedMethodVis, defaultMethodVis);
	}

	private void setClassGroupEnabled(boolean enabled) {
		if (!enabled) {
			sets.drawStruct(Settings.STRUCT_ORDINARY_CLASS
					| Settings.STRUCT_ABSTRACT_CLASS
					| Settings.STRUCT_INTERFACE | Settings.STRUCT_ENUM, true);
		}

		setGroupEnabled(enabled, ordinaryClassesVis, abstractClassesVis,
				interfacesVis, enumsVis);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == packageLayerHideSlider) {
			if (sets.getGraphPackageLayersToHide() == packageLayerHideSlider
					.getValue())
				return;

			sets.setGraphPackageLayersToHide(packageLayerHideSlider.getValue());
		}
	}

}
