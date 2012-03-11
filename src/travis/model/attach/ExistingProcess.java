package travis.model.attach;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import travis.model.attach.client.ClientRunner;

import com.sun.btrace.comm.Command;

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
