package travis.model.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructPackage;
import travis.util.Messages;

public class Builder {

	public StructPackage build(File rootDirectory)
			throws FileNotFoundException, IOException {
		StructPackage root = new StructPackage(Messages.get("default.package"),
				null);
		getFiles(rootDirectory, root);
		return root;
	}

	public StructPackage build() {
		StructPackage root = new StructPackage(Messages.get("default.package"),
				null);
		return root;
	}

	private void getFiles(File rootDirectory, StructPackage parent)
			throws FileNotFoundException, IOException {
		File[] files = rootDirectory.listFiles();

		for (File file : files) {
			if (file.isHidden()) {
				continue;
			}
			
			if (file.isDirectory()) {
				StructPackage sPackage;
				if (parent.isDefaultPackage()) {
					sPackage = new StructPackage(file.getName(), parent);
				} else {
					sPackage = new StructPackage(parent.getName() + "."
							+ file.getName(), parent);
				}
				parent.addStructComponent(sPackage);
				getFiles(file, sPackage);
			} else if (file.getName().endsWith(".class")) {
				ClassReader cr = new ClassReader(new FileInputStream(file));
				ClassNode cn = new ClassNode();
				cr.accept(cn, ClassReader.SKIP_DEBUG);
				parent.addStructComponent(new StructClass(cn, parent));
			}
		}
	}
}
