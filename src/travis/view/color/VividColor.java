package travis.view.color;

import java.awt.Color;

public class VividColor extends StructColor {

	private static final Color PACKAGE_COLOR = Color.ORANGE;

	private static final Color ORDINARY_CLASS_COLOR = Color.GREEN;
	private static final Color ABSTRACT_CLASS_COLOR = new Color(0x99, 0xFF,
			0x99);
	private static final Color INTERFACE_COLOR = Color.BLUE;
	private static final Color ENUM_COLOR = new Color(0x80, 0x40, 0x00);

	private static final Color PUBLIC_METHOD_COLOR = Color.GREEN;
	private static final Color PRIVATE_METHOD_COLOR = Color.RED;
	private static final Color PROTECTED_METHOD_COLOR = Color.YELLOW;
	private static final Color DEFAULT_METHOD_COLOR = Color.BLUE;
	
	private static final Color DEFAULT_EXECUTION_COLOR = Color.MAGENTA;

	@Override
	public Color getPackageColor() {
		return PACKAGE_COLOR;
	}

	@Override
	public Color getOrdinaryClassColor() {
		return ORDINARY_CLASS_COLOR;
	}

	@Override
	public Color getAbstractClassColor() {
		return ABSTRACT_CLASS_COLOR;
	}

	@Override
	public Color getInterfaceColor() {
		return INTERFACE_COLOR;
	}

	@Override
	public Color getEnumColor() {
		return ENUM_COLOR;
	}

	@Override
	public Color getPublicMethodColor() {
		return PUBLIC_METHOD_COLOR;
	}

	@Override
	public Color getPrivateMethodColor() {
		return PRIVATE_METHOD_COLOR;
	}

	@Override
	public Color getProtectedMethodColor() {
		return PROTECTED_METHOD_COLOR;
	}

	@Override
	public Color getDefaultMethodColor() {
		return DEFAULT_METHOD_COLOR;
	}

	@Override
	public Color getExecutionPointColor() {
		return DEFAULT_EXECUTION_COLOR;
	}

}
