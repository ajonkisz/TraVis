package travis.model.project.structure;

import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import travis.model.project.StructStub;
import travis.util.Messages;

public class StructClass extends StructComponent {

	private static final long serialVersionUID = 6857848461494206987L;

	public StructClass(ClassNode classNode, StructComponent parent) {
		super(classNode.name.replaceAll("/", "."), classNode.access, parent,
				State.YES);
		addMethods(classNode);
		isPartOfClassPath = State.YES;
	}

	public StructClass(StructComponent parent, StructStub stub) {
		super(parent, stub);
		isPartOfClassPath = State.YES;
	}

	private void addMethods(ClassNode classNode) {
		@SuppressWarnings("unchecked")
		List<MethodNode> methods = classNode.methods;
		for (MethodNode methodNode : methods) {
			if (!methodNode.name.equals("<clinit>"))
				addStructComponent(new StructMethod(methodNode, this));
		}
	}

	@Override
	public boolean isOrdinaryClass() {
		return !isAbstract() && !isInterface() && !isEnum();
	}

	@Override
	public boolean isPartOfClassPath() {
		return true;
	}

	@Override
	public boolean containsAnyClasses() {
		return true;
	}

	@Override
	protected String getClassPath() {
		return removeLastDotWord(getName());
	}

	@Override
	public boolean addStructComponent(StructComponent sComponent) {
		if (sComponent instanceof StructPackage) {
			throw new IllegalArgumentException(
					Messages.get("sclass.component.exception"));
		}
		return super.addStructComponent(sComponent);
	}

	@Override
	public String toString() {
		return StructUtil.getLastPartOfClassName(super.getName());
	}

}
