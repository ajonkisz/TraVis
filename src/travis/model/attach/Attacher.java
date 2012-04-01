/*
 * Attacher.java
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

import java.io.IOException;

import travis.model.script.ScriptGenerator;

public interface Attacher {

    static final String CURRENT_DIR = System.getProperty("user.dir");
    static final String DEPENDENCIES_DIR = CURRENT_DIR.endsWith("bin") ? CURRENT_DIR
            + "/../dependencies"
            : CURRENT_DIR + "/dependencies";
    static final String AGENT_JAR = DEPENDENCIES_DIR + "/btrace-agent.jar";
    static final String CLIENT_JAR = DEPENDENCIES_DIR + "/btrace-client.jar";
    static final String SCRIPT_PATH = CURRENT_DIR + '/'
            + ScriptGenerator.FILE_NAME + ".class";

    public void detach();

    public void start() throws IOException;

    public boolean isRunning();

    public String getPid();

    public String getName();

    public String getDescriptor();

}
