package travis.view.settings;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import travis.util.Messages;
import travis.view.Util;

public class LayersPanel extends SettingsTab implements ChangeListener {

	private static final long serialVersionUID = -8784652780361264724L;

	private final Settings sets = Settings.getInstance();

	private final JSlider packageHeightSlider;
	private final JSlider classHeightSlider;
	private final JSlider methodHeightSlider;

	private final JSlider packageGapSlider;
	private final JSlider classGapSlider;
	private final JSlider layerGapSlider;

	public LayersPanel() {
		super(new MigLayout("wrap 1, insets 0, fillx"));

		packageHeightSlider = Util.createSlider(0, 85, 0, 10, 5);
		packageHeightSlider.addChangeListener(this);
		classHeightSlider = Util.createSlider(0, 85, 0, 10, 5);
		classHeightSlider.addChangeListener(this);
		methodHeightSlider = Util.createSlider(0, 85, 0, 10, 5);
		methodHeightSlider.addChangeListener(this);

		packageGapSlider = Util.createSlider(0, 80, 0, 10, 5);
		packageGapSlider.addChangeListener(this);
		classGapSlider = Util.createSlider(0, 80, 0, 10, 5);
		classGapSlider.addChangeListener(this);
		layerGapSlider = Util.createSlider(0, 80, 0, 10, 5);
		layerGapSlider.addChangeListener(this);

		add(Util.createBorderedPanel(Messages.get("heights"), "wrap 1",
				new JLabel(Messages.get("packages"), SwingConstants.CENTER),
				packageHeightSlider, new JLabel(Messages.get("classes"),
						SwingConstants.CENTER), classHeightSlider, new JLabel(
						Messages.get("methods"), SwingConstants.CENTER),
				methodHeightSlider), "grow");

		add(Util.createBorderedPanel(Messages.get("gaps"), "wrap 1",
				new JLabel(Messages.get("packages"), SwingConstants.CENTER),
				packageGapSlider, new JLabel(Messages.get("classes"),
						SwingConstants.CENTER), classGapSlider, new JLabel(
						Messages.get("layers"), SwingConstants.CENTER),
				layerGapSlider), "grow");

		updateValues();
	}

	@Override
	public void updateValues() {
		packageHeightSlider.setValue(sets.getPackageHeightPercent());
		classHeightSlider.setValue(sets.getClassHeightPercent());
		methodHeightSlider.setValue(sets.getMethodHeightPercent());

		packageGapSlider.setValue(sets.getPackageGap());
		classGapSlider.setValue(sets.getClassGap());
		layerGapSlider.setValue(sets.getLayerGap());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == packageHeightSlider) {
			if (sets.getPackageHeightPercent() == packageHeightSlider
					.getValue())
				return;

			sets.setPackageHeightPercent(packageHeightSlider.getValue());
		} else if (e.getSource() == classHeightSlider) {
			if (sets.getClassHeightPercent() == classHeightSlider.getValue())
				return;

			sets.setClassHeightPercent(classHeightSlider.getValue());
		} else if (e.getSource() == methodHeightSlider) {
			if (sets.getMethodHeightPercent() == methodHeightSlider.getValue())
				return;

			sets.setMethodHeightPercent(methodHeightSlider.getValue());
		} else if (e.getSource() == packageGapSlider) {
			if (sets.getPackageGap() == packageGapSlider.getValue())
				return;

			sets.setPackageGap(packageGapSlider.getValue());
		} else if (e.getSource() == classGapSlider) {
			if (sets.getClassGap() == classGapSlider.getValue())
				return;

			sets.setClassGap(classGapSlider.getValue());
		} else if (e.getSource() == layerGapSlider) {
			if (sets.getLayerGap() == layerGapSlider.getValue())
				return;

			sets.setLayerGap(layerGapSlider.getValue());
		}
	}

}
