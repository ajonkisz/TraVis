package travis.view.color;

import java.awt.Color;

public class Eclipse2Color extends StructColor {

	private static final Color PACKAGE_COLOR = new Color(237, 209, 166);

	private static final Color ORDINARY_CLASS_COLOR = new Color(51, 130, 77);
	private static final Color ABSTRACT_CLASS_COLOR = new Color(97, 160, 103);
	private static final Color INTERFACE_COLOR = new Color(89, 71, 158);
	private static final Color ENUM_COLOR = new Color(146, 97, 43);

	private static final Color PUBLIC_METHOD_COLOR = new Color(3, 128, 72);
	private static final Color PRIVATE_METHOD_COLOR = new Color(200, 25, 42);
	private static final Color PROTECTED_METHOD_COLOR = new Color(254, 207, 108);
	private static final Color DEFAULT_METHOD_COLOR = new Color(16, 92, 156);

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
