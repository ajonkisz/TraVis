package travis.model.attach;

import java.io.File;
import java.io.FileNotFoundException;

import travis.model.script.FileParser;

public class AttacherFactory {

	public static Attacher newAttacher(JavaProcess process) {
		return new ExistingProcess(process);
	}

	public static Attacher newAttacher(String javaOptions, String main,
			String args, File classPath) {
		return new NewProcess(javaOptions, main, args, classPath);
	}

	public static Playback newAttacher(FileParser fp)
			throws FileNotFoundException {
		return new Playback(fp);
	}

}
