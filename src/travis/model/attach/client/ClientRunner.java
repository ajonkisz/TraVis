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
