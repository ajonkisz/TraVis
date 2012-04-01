/*
 * FileParser.java
 *
 * Copyright (C) 2011-2012, Artur Jonkisz, <travis.source@gmail.com>
 *
 * This file is part of TraVis.
 * See https://github.com/ajonkisz/TraVis for more info.
 *
 * TraVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with TraVis.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.TreePath;

import travis.model.project.StructStub;
import travis.model.project.structure.StructComponent;
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

    private StructComponent buildPackagePathForClassName(String className) {
        String[] packages = getPackagesForClassName(className);
        StructComponent parent = root;
        for (String aPackage : packages) {
            StructStub stub = new StructStub(aPackage);
            StructPackage structPackage = new StructPackage(parent, stub);
            StructComponent foundChild;
            if ((foundChild = parent.getChild(structPackage)) != null) {
                parent = foundChild;
            } else {
                parent.addStructComponent(structPackage);
                parent = structPackage;
            }
        }
        return parent;
    }

    private String[] getPackagesForClassName(String className) {
        String[] packages = className.split("\\.");
        for (int i = 1; i < packages.length - 1; i++) {
            packages[i] = packages[i - 1] + '.' + packages[i];
        }
        return Arrays.copyOf(packages, packages.length - 1);
    }

}
