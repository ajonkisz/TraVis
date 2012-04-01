/*
 * ExistingProcess.java
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

package travis.model.attach;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.btrace.comm.Command;
import travis.model.attach.client.ClientRunner;

public class ExistingProcess implements Attacher {

    private static final String DESCRIPTOR = "Existing";

    private final ExecutorService executor;
    private final ClientRunner runner;
    private final JavaProcess process;
    private volatile boolean running;

    public ExistingProcess(JavaProcess process) {
        this.process = process;
        runner = new ClientRunner(SCRIPT_PATH, process.getPid());
        running = false;
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void detach() {
        if (!executor.isShutdown()) {
            try {
                runner.getClient().sendExit(Command.EXIT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            executor.shutdown();
            running = false;
        }
    }

    @Override
    public void start() {
        executor.submit(runner);
        running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public String getPid() {
        return "" + process.getPid();
    }

    @Override
    public String getName() {
        String name = process.getName();
        int i = name.lastIndexOf('\\');
        if (i != -1) {
            name = name.substring(i + 1);
        }
        i = name.lastIndexOf('/');
        if (i != -1) {
            name = name.substring(i + 1);
        }
        return name;
    }

    @Override
    public String getDescriptor() {
        return DESCRIPTOR;
    }

}
