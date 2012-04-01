/*
 * ScriptGenerator.java
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.btrace.compiler.Compiler;
import travis.model.attach.Attacher;
import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;

public class ScriptGenerator {
    public enum OnMethodType {ENTER, RETURN, THROW}

    public static final String SEQUENCE = "" + (char) 201 + (char) 199
            + (char) 200 + ' ';
    public static final String FILE_NAME = "Temp";
    private static final int FLUSH_COUNT = 2000;

    private final Object writerLock = new Object();
    private volatile FileWriter writer;
    private volatile Map<StructMethod, Integer> methods;

    public ScriptGenerator() {
    }

    public void generateForComponents(StructComponent root,
                                      Set<StructComponent> comps) throws IOException {
        synchronized (writerLock) {
            methods = null; // Let's it garbage collect
            methods = root.getMethods();
            reset();
            for (StructComponent comp : comps) {
                addScriptForComponent(comp, new AtomicInteger());
            }
            writer.write("}");
            writer.flush();
            writer.close();
        }
    }

    public Map<StructMethod, Integer> getMethods() {
        synchronized (writerLock) {
            return methods == null ? new HashMap<StructMethod, Integer>()
                    : methods;
        }
    }

    private void reset() throws IOException {
        if (writer != null)
            writer.close();

        writer = new FileWriter(FILE_NAME + ".java");
        (new File(FILE_NAME + ".java")).deleteOnExit();

        writer.write("import com.sun.btrace.annotations.*;\n");
        writer.write("import static com.sun.btrace.BTraceUtils.*;\n\n");
        writer.write("@BTrace public class ");
        writer.write(FILE_NAME);
        writer.write(" {\n\n");
    }

    private void addScriptForComponent(StructComponent comp,
                                       AtomicInteger counter) throws IOException {
        if (!(comp instanceof StructMethod)) {
            for (StructComponent c : comp.getChildren()) {
                addScriptForComponent(c, counter);
            }
            return;
        }

        int methodNo = methods.get(comp);
        writeMethod(comp, methodNo, OnMethodType.ENTER);
        writeMethod(comp, methodNo, OnMethodType.RETURN);
        writeMethod(comp, methodNo, OnMethodType.THROW);
        counter.addAndGet(10);
        if (counter.intValue() > FLUSH_COUNT) {
            flush();
            counter.set(0);
        }
    }

    private void writeMethod(StructComponent comp, int methodNo,
                             OnMethodType retType) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("\t@OnMethod(\n");
        sb.append(String.format("\t\tclazz=\"%s\",\n", comp.getParentName()));
        sb.append(String.format("\t\tmethod=\"%s\",\n", comp.getName()));
        sb.append(String.format("\t\ttype=\"%s (%s)\"", comp.getReturnType(),
                comp.getParameters(true)));
        if (retType == OnMethodType.RETURN)
            sb.append(",\n\t\tlocation=@Location(Kind.RETURN)");
        if (retType == OnMethodType.THROW)
            sb.append(",\n\t\tlocation=@Location(Kind.ERROR)");

        sb.append(")\n");
        sb.append("\tpublic static void m");
        if (retType == OnMethodType.RETURN)
            sb.append("Ret");
        if (retType == OnMethodType.THROW)
            sb.append("Throw");
        sb.append(methodNo);
        sb.append("() {\n");

        // Method body
        sb.append("\t\tString s = \"");
        sb.append(SEQUENCE);
        if (retType != OnMethodType.ENTER)
            sb.append('-');
        sb.append(methodNo);
        sb.append(' ');
        sb.append("\";\n");

        appendStringToScript("str(timeNanos())", sb);
        appendStringToScript("str(' ')", sb);
        appendStringToScript("str(threadId(currentThread()))", sb);

        sb.append("\t\tprintln(s);\n");

        sb.append("\t}\n\n");
        writer.write(sb.toString());
    }

    private void appendStringToScript(String string, StringBuilder sb) {
        sb.append("\t\ts = strcat(s, ");
        sb.append(string);
        sb.append(");\n");
    }

    private void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buildScript() {
        try {
            synchronized (writerLock) {
                Compiler.main(new String[]{"-cp", Attacher.CLIENT_JAR,
                        (new File(FILE_NAME + ".java")).getAbsolutePath()});
                (new File(FILE_NAME + ".class")).deleteOnExit();
            }
            ScriptHandler.getInstance().writeScriptHeader(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
