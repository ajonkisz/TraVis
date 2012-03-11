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
