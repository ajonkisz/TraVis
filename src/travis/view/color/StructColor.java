package travis.view.color;

import java.awt.Color;

import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.project.structure.StructPackage;
import travis.model.project.structure.StructUtil.Visibility;

public abstract class StructColor {
	
	public Color getColorForComp(StructComponent comp) {
		Color color = Color.WHITE;

		if (comp instanceof StructPackage)
			color = getPackageColor();

		if (comp instanceof StructClass) {
			if (comp.isOrdinaryClass())
				color = getOrdinaryClassColor();
			if (comp.isAbstract())
				color = getAbstractClassColor();
			if (comp.isInterface())
				color = getInterfaceColor();
			if (comp.isEnum())
				color = getEnumColor();
		}

		if (comp instanceof StructMethod) {
			if (comp.getVisibility() == Visibility.PUBLIC)
				color = getPublicMethodColor();
			if (comp.getVisibility() == Visibility.PRIVATE)
				color = getPrivateMethodColor();
			if (comp.getVisibility() == Visibility.PROTECTED)
				color = getProtectedMethodColor();
			if (comp.getVisibility() == Visibility.DEFAULT)
				color = getDefaultMethodColor();
		}

		return color;
	}

	public abstract Color getPackageColor();

	public abstract Color getOrdinaryClassColor();

	public abstract Color getAbstractClassColor();

	public abstract Color getInterfaceColor();

	public abstract Color getEnumColor();

	public abstract Color getPublicMethodColor();

	public abstract Color getPrivateMethodColor();

	public abstract Color getProtectedMethodColor();

	public abstract Color getDefaultMethodColor();
	
	public abstract Color getExecutionPointColor();

}