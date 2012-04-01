/*
 * Builder.java
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
