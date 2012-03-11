package travis.model.script;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.tree.TreePath;

import travis.model.project.StructStub;
import travis.model.project.structure.StructClass;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.project.structure.StructPackage;
import travis.view.project.tree.ProjectTreeNode;

public class FileParser {

	private final StructComponent root;
	private final File file;
	private final int tracesStart;
	private final int tracesLength;
	private final TreePath[] treePaths;
	private final List<Integer> depths;
	private int maxDepth;

	public FileParser(File script) throws IOException, ClassNotFoundException {
		file = script;

		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		treePaths = (TreePath[]) ois.readObject();

		root = ((ProjectTreeNode) treePaths[0].getLastPathComponent())
				.getRootStructComp();
		ois.close();
		fis.close();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(treePaths);

		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		lnr.skip(bos.size());
		// Consume till line ends and TREEPATHS_TERMINATING_SEQUENCE
		lnr.readLine();
		lnr.readLine();

		String s;
		Matcher m = Pattern.compile(ScriptHandler.HEADER_TERMINATING_SEQUENCE)
				.matcher("");
		while ((s = lnr.readLine()) != null) {
			m.reset(s);
			if (m.matches())
				break;
		}

		// This got replaced by serialization
		// root = (new Builder()).build();
		// parseHeader(new Scanner(file));

		tracesStart = lnr.getLineNumber();

		// Skip until the end reached
		depths = new ArrayList<Integer>(5000);
		int depth = 0;
		while ((s = lnr.readLine()) != null) {
			if (s.charAt(0) == '-') {
				if (depth > 0)
					depth--;
			} else {
				depth++;
			}
			depths.add(depth);
			maxDepth = Math.max(maxDepth, depth);
		}

		tracesLength = lnr.getLineNumber() - tracesStart + 1;
	}

	public List<Integer> getDepths() {
		return depths;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public int getTracesLength() {
		return tracesLength;
	}

	public int getTracesStart() {
		return tracesStart;
	}

	public StructComponent getRoot() {
		return root;
	}

	public TreePath[] getTreePaths() {
		return treePaths;
	}

	public File getFile() {
		return file;
	}

	// This got replaced by serialization, but is left for reference.
	@SuppressWarnings("unused")
	private int parseHeader(Scanner sc) throws IOException {
		sc.findWithinHorizon(ScriptHandler.TREEPATHS_TERMINATING_SEQUENCE, 0);
		sc.nextLine();

		int i = 1;
		try {
			while (!sc.hasNext(ScriptHandler.HEADER_TERMINATING_SEQUENCE)) {
				i++;
				sc.nextInt();
				String className = sc.next();
				int classAccessFlag = sc.nextInt();
				String methodName = sc.next();
				String methodDescriptor = sc.next();
				int methodAccessFlag = sc.nextInt();

				StructStub classStub = new StructStub(className);
				classStub.setAccessFlag(classAccessFlag);
				StructClass methodParent = getMethodParentForClassStub(classStub);

				StructStub methodStub = new StructStub(methodName);
				methodStub.setAccessFlag(methodAccessFlag);
				methodStub.setDescriptor(methodDescriptor);
				methodParent.addStructComponent(new StructMethod(methodParent,
						methodStub));
			}
			if (i != 1) {
				// Consume end of the line
				sc.nextLine();
				// Consume HEADER_TERMINATING_SEQUENCE
				sc.nextLine();
			}
		} catch (Exception e) {
			System.err.println("Problem with parsing traces in "
					+ file.getName() + " file");
		}
		return i;
	}

	private StructClass getMethodParentForClassStub(StructStub stub) {
		StructComponent packageParent = buildPackagePathForClassName(stub
				.getName());
		StructClass structClass = new StructClass(packageParent, stub);

		StructComponent foundChild = packageParent.getChild(structClass);
		StructClass methodParent;
		if (foundChild != null) {
			methodParent = (StructClass) foundChild;
		} else {
			packageParent.addStructComponent(structClass);
			methodParent = structClass;
		}

		return methodParent;
	}

	private StructComponent buildPackagePathForClassName(String className) {
		String[] packages = getPackagesForClassName(className);
		StructComponent parent = root;
		for (int i = 0; i < packages.length; i++) {
			StructStub stub = new StructStub(packages[i]);
			StructPackage structPackage = new StructPackage(parent, stub);
			StructComponent foundChild;
			if ((foundChild = parent.getChild(structPackage)) != null) {
				parent = foundChild;
				continue;
			} else {
				parent.addStructComponent(structPackage);
				parent = structPackage;
			}
		}
		return parent;
	}

	private String[] getPackagesForClassName(String className) {
		String[] packages = className.split("\\.");
		for (int i = 0; i < packages.length - 1; i++) {
			if (i == 0) {
				continue;
			} else {
				packages[i] = packages[i - 1] + '.' + packages[i];
			}
		}
		return Arrays.copyOf(packages, packages.length - 1);
	}

}
