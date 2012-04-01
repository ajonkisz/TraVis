/*
 * JavaProcess.java
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

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class JavaProcess implements Comparable<JavaProcess> {

    private static final Pattern PID_PATTERN = Pattern.compile("(?!@)\\d+");

    private final int pid;
    private final String name;

    public JavaProcess(String pid, String name) {
        this.pid = Integer.parseInt(pid);
        this.name = name;
    }

    public int getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s %s", pid, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JavaProcess) {
            return compareTo((JavaProcess) obj) == 0;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result += 37 * pid;
        return result;
    }

    @Override
    public int compareTo(JavaProcess o) {
        if (pid == o.pid)
            return 0;
        return pid < o.pid ? 1 : -1;
    }

    public static Collection<JavaProcess> getVMList() {
        List<VirtualMachineDescriptor> descriptors = VirtualMachine.list();
        Set<JavaProcess> processes = new TreeSet<JavaProcess>();

        String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        String currentPid = (new Scanner(runtimeName)).findInLine(PID_PATTERN);

        for (VirtualMachineDescriptor descriptor : descriptors) {
            if (descriptor.id().equals(currentPid))
                continue;
            processes.add(new JavaProcess(descriptor.id(), descriptor
                    .displayName()));
        }

        return processes;
    }

}
