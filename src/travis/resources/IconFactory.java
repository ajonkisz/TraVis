package travis.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructUtil.Visibility;

public class IconFactory {

	private static final Icon PACKAGE;
	private static final Icon PACKAGE_EMPTY;
	private static final Icon FOLDER_CLOSED;
	private static final Icon FOLDER_OPENED;

	private static final Icon CLASS_PUBLIC;
	private static final Icon CLASS_PRIVATE;
	private static final Icon CLASS_PROTECTED;
	private static final Icon CLASS_DEFAULT;

	private static final Icon INTERFACE;
	private static final Icon ENUM;

	private static final Icon METHOD_PUBLIC;
	private static final Icon METHOD_PRIVATE;
	private static final Icon METHOD_PROTECTED;
	private static final Icon METHOD_DEFAULT;

	private static final Icon CONSTRUCTOR;
	private static final Icon ABSTRACT;
	private static final Icon STATIC;
	private static final Icon FINAL;

	static {
		PACKAGE = createImageIcon("package_obj.png");
		PACKAGE_EMPTY = createImageIcon("empty_pack_obj.png");
		FOLDER_CLOSED = createImageIcon("folder_closed.png");
		FOLDER_OPENED = createImageIcon("folder_opened.png");

		CLASS_PUBLIC = createImageIcon("class_obj.png");
		CLASS_PRIVATE = createImageIcon("innerclass_private_obj.png");
		CLASS_PROTECTED = createImageIcon("innerclass_protected_obj.png");
		CLASS_DEFAULT = createImageIcon("class_default_obj.png");

		INTERFACE = createImageIcon("int_obj.png");
		ENUM = createImageIcon("enum_obj.png");

		METHOD_PUBLIC = createImageIcon("methpub_obj.png");
		METHOD_PRIVATE = createImageIcon("methpri_obj.png");
		METHOD_PROTECTED = createImageIcon("methpro_obj.png");
		METHOD_DEFAULT = createImageIcon("methdef_obj.png");

		CONSTRUCTOR = createImageIcon("constr_ovr.png");
		ABSTRACT = createImageIcon("abstract_co.png");
		STATIC = createImageIcon("static_co.png");
		FINAL = createImageIcon("final_co.png");
	}

	public static ImageIcon createImageIcon(String name) {
		URL imageURL = IconFactory.class.getResource("icons/" + name);
		if (imageURL != null) {
			return new ImageIcon(imageURL);
		} else {
			return null;
		}
	}

	public static Icon getClassIcon(StructComponent comp) {
		if (comp.isEnum())
			return ENUM;
		else if (comp.isInterface())
			return INTERFACE;

		Visibility visibility = comp.getVisibility();
		Icon icon;
		switch (visibility) {
		case PUBLIC:
			icon = CLASS_PUBLIC;
			break;
		case PRIVATE:
			icon = CLASS_PRIVATE;
			break;
		case PROTECTED:
			icon = CLASS_PROTECTED;
			break;
		default:
			icon = CLASS_DEFAULT;
			break;
		}
		icon = addModiers(comp, icon);
		return icon;
	}

	public static Icon getPackageIcon(StructComponent comp, boolean expanded) {
		if (!comp.isPartOfClassPath())
			return expanded ? FOLDER_OPENED : FOLDER_CLOSED;
		if (!comp.containsAnyClasses())
			return PACKAGE_EMPTY;
		else
			return PACKAGE;
	}

	public static Icon getMethodIcon(StructComponent comp) {
		Visibility visibility = comp.getVisibility();
		Icon icon;
		switch (visibility) {
		case PUBLIC:
			icon = METHOD_PUBLIC;
			break;
		case PRIVATE:
			icon = METHOD_PRIVATE;
			break;
		case PROTECTED:
			icon = METHOD_PROTECTED;
			break;
		default:
			icon = METHOD_DEFAULT;
			break;
		}
		if (comp.isConstructor()) {
			icon = addConstructor(icon);
		}
		icon = addModiers(comp, icon);
		return icon;
	}

	private static Icon addModiers(StructComponent comp, Icon icon) {
		BufferedImage bf = new BufferedImage(icon.getIconWidth(),
				icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = bf.getGraphics();
		icon.paintIcon(null, g2, 0, 0);
		if (comp.isAbstract())
			ABSTRACT.paintIcon(null, g2,
					bf.getWidth() - CONSTRUCTOR.getIconWidth(), 0);
		else if (comp.isFinal())
			FINAL.paintIcon(null, g2,
					bf.getWidth() - CONSTRUCTOR.getIconWidth(), 0);

		if (comp.isStatic())
			STATIC.paintIcon(null, g2, 0, 0);

		g2.dispose();
		return new ImageIcon(bf);
	}

	private static Icon addConstructor(Icon icon) {
		BufferedImage bf = new BufferedImage(icon.getIconWidth(),
				icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = bf.getGraphics();
		icon.paintIcon(null, g2, 0, 0);
		CONSTRUCTOR.paintIcon(null, g2,
				bf.getWidth() - CONSTRUCTOR.getIconWidth(), 0);
		g2.dispose();
		return new ImageIcon(bf);
	}

}
