package travis.model.project;

import org.objectweb.asm.Opcodes;

public class StructStub {

	public enum Visibility {
		DEFAULT(0), PUBLIC(Opcodes.ACC_PUBLIC), PRIVATE(Opcodes.ACC_PRIVATE), PROTECTED(
				Opcodes.ACC_PROTECTED);

		private final int opcode;

		private Visibility(int opcode) {
			this.opcode = opcode;
		}

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public enum Modifier {
		NONE(0), FINAL(Opcodes.ACC_FINAL), ABSTRACT(Opcodes.ACC_ABSTRACT), STATIC(
				Opcodes.ACC_STATIC);

		private final int opcode;

		private Modifier(int opcode) {
			this.opcode = opcode;
		}

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public enum Inheritance {
		NONE(0), INTERFACE(Opcodes.ACC_INTERFACE), ABSTRACT(
				Opcodes.ACC_ABSTRACT), FINAL(Opcodes.ACC_FINAL), ENUM(
				Opcodes.ACC_ENUM);

		private final int opcode;

		private Inheritance(int opcode) {
			this.opcode = opcode;
		}

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	private String name;
	private String descriptor;
	private int accessFlag;

	public StructStub() {
		this("");
	}

	public StructStub(String name) {
		this.name = name;
		this.descriptor = "";
	}

	public void setVisibility(Visibility visibility) {
		accessFlag |= visibility.opcode;
	}

	public void setModifier(Modifier modifier) {
		accessFlag |= modifier.opcode;
	}

	public void setInheritance(Inheritance inheritance) {
		accessFlag |= inheritance.opcode;
	}

	public int getAccessFlag() {
		return accessFlag;
	}
	
	public void setAccessFlag(int accessFlag) {
		this.accessFlag = accessFlag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescriptor() {
		return descriptor.replace('.', '/');
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

}
