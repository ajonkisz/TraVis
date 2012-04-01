/*
 * ClientMain.java
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

package travis.model.attach.client;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.sun.btrace.CommandListener;
import com.sun.btrace.client.Client;
import com.sun.btrace.comm.Command;
import com.sun.btrace.comm.DataCommand;
import com.sun.btrace.comm.ErrorCommand;
import sun.misc.Signal;
import sun.misc.SignalHandler;
import travis.model.attach.Attacher;

/**
 * This is the main class for a simple command line BTrace client. It is
 * possible to create a GUI client using the Client class.
 *
 * @author A. Sundararajan
 */
public final class ClientMain {
    public static volatile boolean exiting;
    public static final boolean DEBUG;
    public static final boolean TRACK_RETRANSFORM;
    public static final boolean UNSAFE;
    public static final boolean DUMP_CLASSES;
    public static final String DUMP_DIR;
    public static final String PROBE_DESC_PATH;
    public static final int BTRACE_DEFAULT_PORT = 2020;

    private static final Console con;
    private static final PrintWriter out;

    static {
        DEBUG = Boolean.getBoolean("com.sun.btrace.debug");
        if (isDebug())
            debugPrint("btrace debug mode is set");
        TRACK_RETRANSFORM = Boolean
                .getBoolean("com.sun.btrace.trackRetransforms");
        if (isDebug() && TRACK_RETRANSFORM)
            debugPrint("trackRetransforms flag is set");
        UNSAFE = Boolean.getBoolean("com.sun.btrace.unsafe");
        if (isDebug() && UNSAFE)
            debugPrint("btrace unsafe mode is set");
        DUMP_CLASSES = Boolean.getBoolean("com.sun.btrace.dumpClasses");
        if (isDebug() && DUMP_CLASSES)
            debugPrint("dumpClasses flag is set");
        DUMP_DIR = System.getProperty("com.sun.btrace.dumpDir", ".");
        if (DUMP_CLASSES) {
            if (isDebug())
                debugPrint("dumpDir is " + DUMP_DIR);
        }
        PROBE_DESC_PATH = System.getProperty("com.sun.btrace.probeDescPath",
                ".");
        con = System.console();
        out = new PrintWriter(System.out);
    }

    private final Client client;

    public ClientMain(int port) {
        client = new Client(port, PROBE_DESC_PATH, DEBUG, TRACK_RETRANSFORM,
                UNSAFE, DUMP_CLASSES, DUMP_DIR);
    }

    public void start(String fileName, String classPath, String includePath,
                      String pid, String[] btraceArgs) throws IOException {
        if (!new File(fileName).exists()) {
            errorExit("File not found: " + fileName, 1);
        }
        byte[] code = client.compile(fileName, classPath, includePath);
        if (code == null) {
            errorExit("BTrace compilation failed", 1);
        }
        client.attach(pid, Attacher.AGENT_JAR, null, null);
        registerExitHook(client);
        if (con != null) {
            registerSignalHandler(client);
        }
        if (isDebug())
            debugPrint("submitting the BTrace program");
        client.submit(fileName, code, btraceArgs, createCommandListener(client));
    }

    public Client getClient() {
        return client;
    }

    private static CommandListener createCommandListener(Client client) {
        return new CommandListener() {
            public void onCommand(Command cmd) throws IOException {
                int type = cmd.getType();
                if (cmd instanceof DataCommand) {
                    ((DataCommand) cmd).print(out);
                    out.flush();
                } else if (type == Command.EXIT) {
                    exiting = true;
                } else if (type == Command.ERROR) {
                    ErrorCommand ecmd = (ErrorCommand) cmd;
                    Throwable cause = ecmd.getCause();
                    if (cause != null) {
                        cause.printStackTrace();
                    }
                }
            }
        };
    }

    private static void registerExitHook(final Client client) {
        if (isDebug())
            debugPrint("registering shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                if (!exiting) {
                    try {
                        if (isDebug())
                            debugPrint("sending exit command");
                        client.sendExit(0);
                    } catch (IOException ioexp) {
                        if (isDebug())
                            debugPrint(ioexp.toString());
                    }
                }
            }
        }));
    }

    private static void registerSignalHandler(final Client client) {
        if (isDebug())
            debugPrint("registering signal handler for SIGINT");
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                try {
                    con.printf("Please enter your option:\n");
                    con.printf("\t1. exit\n\t2. send an event\n\t3. send a named event\n");
                    con.flush();
                    String option = con.readLine();
                    option = option.trim();
                    if (option == null) {
                        return;
                    }
                    if (option.equals("1")) {
                        System.exit(0);
                    } else if (option.equals("2")) {
                        if (isDebug())
                            debugPrint("sending event command");
                        client.sendEvent();
                    } else if (option.equals("3")) {
                        con.printf("Please enter the event name: ");
                        String name = con.readLine();
                        if (name != null) {
                            if (isDebug())
                                debugPrint("sending event command");
                            client.sendEvent(name);
                        }
                    } else {
                        con.printf("invalid option!\n");
                    }
                } catch (IOException ioexp) {
                    if (isDebug())
                        debugPrint(ioexp.toString());
                }
            }
        });
    }

    private static boolean isDebug() {
        return DEBUG;
    }

    private static void debugPrint(String msg) {
        System.out.println("DEBUG: " + msg);
    }

    private static void errorExit(String msg, int code) {
        exiting = true;
        System.err.println(msg);
        System.exit(code);
    }
}
