package travis.view.color;

import java.awt.Color;

public abstract class ModifiableStructColor extends StructColor {

	public static final ModifiableStructColor getModifiableColor() {
		return new ModifiableColor();
	}

	public static final ModifiableStructColor getModifiableColor(
			StructColor color) {
		return new ModifiableColor(color);
	}
	
	public void setColors(StructColor color) {
		setPackageColor(color.getPackageColor());
		setOrdinaryClassColor(color.getOrdinaryClassColor());
		setAbstractClassColor(color.getAbstractClassColor());
		setInterfaceColor(color.getInterfaceColor());
		setEnumColor(color.getEnumColor());
		setPublicMethodColor(color.getPublicMethodColor());
		setPrivateMethodColor(color.getPrivateMethodColor());
		setProtectedMethodColor(color.getProtectedMethodColor());
		setDefaultMethodColor(color.getDefaultMethodColor());
		setExecutionPointColor(color.getExecutionPointColor());
	}

	public abstract void setPackageColor(Color packageColor);

	public abstract void setOrdinaryClassColor(Color ordinaryClassColor);

	public abstract void setAbstractClassColor(Color abstractClassColor);

	public abstract void setInterfaceColor(Color interfaceColor);

	public abstract void setEnumColor(Color enumColor);

	public abstract void setPublicMethodColor(Color publicMethodColor);

	public abstract void setPrivateMethodColor(Color privateMethodColor);

	public abstract void setProtectedMethodColor(Color protectedMethodColor);

	public abstract void setDefaultMethodColor(Color defaultMethodColor);
	
	public abstract void setExecutionPointColor(Color executionPointColor);

}