/*
 * ScriptPrinter.java
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

import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import travis.view.console.Printer;

public class ScriptPrinter extends PrintStream {

    private static final Pattern LINE_PATTERN = Pattern.compile("((?!"
            + ScriptGenerator.SEQUENCE + ").*)?" + ScriptGenerator.SEQUENCE
            + "([-])?(\\d+)[ ](\\d+)[ ](\\d+)$");
    private static final Matcher MATCHER = LINE_PATTERN.matcher("");

    private static final ScriptPrinter INSTANCE = new ScriptPrinter();

    private final ScriptHandler handler;
    private final Collection<Printer> listeningPrinters;

    private ScriptPrinter() {
        super(System.out, true);
        handler = ScriptHandler.getInstance();
        listeningPrinters = new ConcurrentLinkedQueue<Printer>();
    }

    public void addListeningPrinter(Printer printer) {
        listeningPrinters.add(printer);
    }

    public void removeListeningPrinter(Printer printer) {
        listeningPrinters.remove(printer);
    }

    public static ScriptPrinter getInstance() {
        return INSTANCE;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        String s = new String(buf, off, len);
        // If didn't find script string then print to console as normal
        if (!findScript(s)) {
            printToOld(s);
        }
    }

    private boolean findScript(String s) {
        String methodId;
        boolean returnCall;
        String nanoTime;
        String threadId;
        String consumedString;

        synchronized (MATCHER) {
            MATCHER.reset(s);
            if (MATCHER.find()) {
                consumedString = MATCHER.group(1);

                returnCall = MATCHER.group(2) != null;
                methodId = MATCHER.group(3);
                nanoTime = MATCHER.group(4);
                threadId = MATCHER.group(5);
            } else {
                return false;
            }
        }
        handler.writeScriptLine(methodId, returnCall, nanoTime, threadId);

        if (consumedString != null)
            printToOld(consumedString);

        return true;
    }

    private void printToOld(String string) {
        byte[] bytes = string.getBytes();
        super.write(bytes, 0, bytes.length);
        if (bytes[bytes.length - 1] != '\n') {
            super.write('\n');
            notifyPrinters(string + '\n');
        } else {
            notifyPrinters(string);
        }
    }

    private void notifyPrinters(String s) {
        for (Printer printer : listeningPrinters) {
            printer.write(s);
        }
    }
}
