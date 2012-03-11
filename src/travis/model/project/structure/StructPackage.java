package travis.model.project.structure;

import travis.model.project.StructStub;
import travis.util.Messages;

public class StructPackage extends StructComponent {

	private static final long serialVersionUID = -1589126374176458284L;

	public StructPackage(String name, StructComponent parent) {
		super(name, parent);
	}
	
	public StructPackage(StructComponent parent, StructStub stub) {
		super(parent, stub);
		super.isPartOfClassPath = State.YES;
	}

	@Override
	public String getName() {
		if (isDefaultPackage())
			return "";
		else
			return super.getName();
	}
	
	@Override
	public boolean isDefaultPackage() {
		return super.getName().equals(Messages.get("default.package"));
	}

	@Override
	public boolean addStructComponent(StructComponent sComponent) {
		if (sComponent instanceof StructMethod) {
			throw new IllegalArgumentException(
					Messages.get("spackage.component.exception"));
		}
		return super.addStructComponent(sComponent);
	}

}
