package travis.model.project.structure;

import org.objectweb.asm.Opcodes;

public class StructUtil {

	public enum Visibility {
		PUBLIC, PROTECTED, DEFAULT, PRIVATE;
		
		public String toString() {
			return super.toString().toLowerCase();
		};
	}

	public static Visibility getVisibility(int access) {
		Visibility visibility = Visibility.DEFAULT;
		if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE)
			visibility = Visibility.PRIVATE;
		else if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED)
			visibility = Visibility.PROTECTED;
		else if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC)
			visibility = Visibility.PUBLIC;

		return visibility;
	}
	
	public static String getLastPartOfClassName(String className) {
		int dotIndex = className.lastIndexOf('.');
		return className.substring(dotIndex == -1 ? 0 : dotIndex + 1);
	}
	
	public static boolean isFlagSet(int access, int flag) {
		return (access & flag) == flag;
	}

}
