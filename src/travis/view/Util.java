package travis.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import travis.resources.IconFactory;

import net.miginfocom.swing.MigLayout;

public class Util {
	
	public static final RenderingHints HINTS;
	static {
		HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	public static JButton getButtonWithIcon(String name) {
		JButton b = new JButton(IconFactory.createImageIcon(name + ".png"));
		b.setPressedIcon(IconFactory.createImageIcon(name + "_pressed.png"));
		return b;
	}

	public static int pow(int num, int power) {
		if (power < 1) {
			return 1;
		}
		return num * pow(num, --power);
	}

	public static boolean containsFlag(int flags, int flag) {
		return (flags & flag) == flag;
	}

	public static int toggleFlag(int flags, int flag, boolean enabled) {
		if (enabled) {
			flags |= flag;
		} else {
			flags &= ~flag;
		}
		return flags;
	}

	public static JSlider createSlider(int min, int max, int init,
			int majorTick, int minorTick) {
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, min, max, init);
		slider.setMajorTickSpacing(majorTick);
		slider.setMinorTickSpacing(minorTick);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		return slider;
	}

	public static JPanel createBorderedPanel(String title,
			Component... components) {
		return createBorderedPanel(title, "", components);
	}

	public static JPanel createBorderedPanel(String title, String constraints,
			Component... components) {
		JPanel panel = new JPanel(new MigLayout("fillx, insets 0, gap 0, "
				+ constraints));
		if (components == null || components.length == 0) {
			return panel;
		}
		panel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK), title,
				TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
		for (Component component : components) {
			panel.add(component, "grow");
		}
		return panel;
	}

}
