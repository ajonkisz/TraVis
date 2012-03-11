package travis.view.color;

import java.awt.Color;

public class ModifiableColor extends ModifiableStructColor {

	private Color packageColor = Color.WHITE;

	private Color ordinaryClassColor = Color.WHITE;
	private Color abstractClassColor = Color.WHITE;
	private Color interfaceColor = Color.WHITE;
	private Color enumColor = Color.WHITE;

	private Color publicMethodColor = Color.WHITE;
	private Color privateMethodColor = Color.WHITE;
	private Color protectedMethodColor = Color.WHITE;
	private Color defaultMethodColor = Color.WHITE;
	
	private Color executionColor = Color.MAGENTA;
	
	public ModifiableColor() {
	}
	
	public ModifiableColor(StructColor color) {
		setColors(color);
	}

	public Color getPackageColor() {
		return packageColor;
	}

	public void setPackageColor(Color packageColor) {
		this.packageColor = packageColor;
	}

	public Color getOrdinaryClassColor() {
		return ordinaryClassColor;
	}

	public void setOrdinaryClassColor(Color ordinaryClassColor) {
		this.ordinaryClassColor = ordinaryClassColor;
	}

	public Color getAbstractClassColor() {
		return abstractClassColor;
	}

	public void setAbstractClassColor(Color abstractClassColor) {
		this.abstractClassColor = abstractClassColor;
	}

	public Color getInterfaceColor() {
		return interfaceColor;
	}

	public void setInterfaceColor(Color interfaceColor) {
		this.interfaceColor = interfaceColor;
	}

	public Color getEnumColor() {
		return enumColor;
	}

	public void setEnumColor(Color enumColor) {
		this.enumColor = enumColor;
	}

	public Color getPublicMethodColor() {
		return publicMethodColor;
	}

	public void setPublicMethodColor(Color publicMethodColor) {
		this.publicMethodColor = publicMethodColor;
	}

	public Color getPrivateMethodColor() {
		return privateMethodColor;
	}

	public void setPrivateMethodColor(Color privateMethodColor) {
		this.privateMethodColor = privateMethodColor;
	}

	public Color getProtectedMethodColor() {
		return protectedMethodColor;
	}

	public void setProtectedMethodColor(Color protectedMethodColor) {
		this.protectedMethodColor = protectedMethodColor;
	}

	public Color getDefaultMethodColor() {
		return defaultMethodColor;
	}

	public void setDefaultMethodColor(Color defaultMethodColor) {
		this.defaultMethodColor = defaultMethodColor;
	}

	@Override
	public void setExecutionPointColor(Color executionPointColor) {
		executionColor = executionPointColor;
	}

	@Override
	public Color getExecutionPointColor() {
		return executionColor;
	}
}
