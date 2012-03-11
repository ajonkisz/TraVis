package travis.model.attach;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewProcess implements Attacher {
	
	private static final String DESCRIPTOR = "New";

	private final String javaOptions;
	private final String main;
	private final String args;
	private final File classPath;
	private final ExecutorService executor;
	private volatile boolean running;
	private Process process;

	public NewProcess(String javaOptions, String main, String args,
			File classPath) {
		this.javaOptions = javaOptions;
		this.main = main;
		this.args = args;
		this.classPath = classPath;
		this.running = false;
		executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void detach() {
		if (process != null)
			process.destroy();
		executor.shutdown();
		running = false;
	}

	@Override
	public void start() throws IOException {
		ProcessBuilder pb = new ProcessBuilder(getCommands());
		pb.redirectErrorStream(true);

		pb.directory(classPath);

		process = pb.start();

		executor.submit(new PrinterRunnable());
		running = true;
	}

	private List<String> getCommands() {
		String agentString = "-javaagent:" + AGENT_JAR
				+ "=dumpClasses=false,debug=false,stdout=true,unsafe=false,"
				+ "probeDescPath=.,noServer=true," + "script=" + SCRIPT_PATH;

		List<String> commands = new ArrayList<String>();
		commands.add("java");
		if (!javaOptions.trim().isEmpty()) {
			for (String string : javaOptions.trim().split(" ")) {
				commands.add(string);
			}
		}
		commands.add("-Xshare:off");
		commands.add(agentString);

		commands.add(main);
		if (!args.trim().isEmpty()) {
			for (String string : args.trim().split(" ")) {
				commands.add(string);
			}
		}

		return commands;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	private class PrinterRunnable implements Runnable {
		@Override
		public void run() {
			Scanner sc = new Scanner(process.getInputStream());
			try {
				for (String s = sc.nextLine(); s != null; s = sc.nextLine()) {
					byte[] bytes = s.getBytes();
					System.out.write(bytes, 0, bytes.length);
				}
			} catch (NoSuchElementException e) {
				// Occurs when process destroyed
			}
		}
	}

	@Override
	public String getPid() {
		return "?";
	}

	@Override
	public String getName() {
		String name = main;
		int i = name.lastIndexOf('.');
		if (i != -1) {
			name = name.substring(i + 1);
		}
		i = name.lastIndexOf('\\');
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
