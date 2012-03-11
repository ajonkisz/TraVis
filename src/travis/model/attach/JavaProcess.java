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
			return compareTo((JavaProcess) obj) == 0 ? true : false;
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
