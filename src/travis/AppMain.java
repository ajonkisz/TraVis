package travis;

import travis.controller.Controller;
import travis.model.script.ScriptPrinter;

public class AppMain {

	public static void main(String[] args) {
		System.setOut(ScriptPrinter.getInstance());
		new Controller();
	}

}
