/*
 * ClientRunner.java
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

import java.io.IOException;

import com.sun.btrace.client.Client;

public class ClientRunner implements Runnable {
    private final ClientMain cm;
    private final Client client;
    private final String path;
    private final int pid;

    static {
        System.setProperty("com.sun.btrace.probeDescPath", ".");
        System.setProperty("com.sun.btrace.dumpClasses", "false");
        System.setProperty("com.sun.btrace.unsafe", "false");
        System.setProperty("com.sun.btrace.debug", "false");
    }

    public ClientRunner(String path, int pid) {
        this.path = path;
        this.pid = pid;
        cm = new ClientMain(ClientMain.BTRACE_DEFAULT_PORT);
        client = cm.getClient();
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void run() {
        try {
            cm.start(path, ".", null, "" + pid, new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
