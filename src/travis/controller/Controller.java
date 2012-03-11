package travis.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Timer;

import travis.controller.UIHelper.Mode;
import travis.model.script.ScriptHandler;
import travis.model.script.TraceInfo;
import travis.view.project.graph.connection.ConnectionPainter;
import travis.view.settings.Settings;
import travis.view.settings.Settings.Type;

public class Controller implements Observer, ActionListener {

	private static final int FRAME_TIME = 1000 / 25;

	private long lastRepaint;
	private volatile boolean outstandingRepaint;

	public Controller() {
		outstandingRepaint = false;

		UIHelper.getInstance().populateFrame();
		Settings.getInstance().addObserver(this);
		ScriptHandler.getInstance().addObserver(this);

		Timer timer = new Timer(250, this);
		timer.setRepeats(true);
		timer.setInitialDelay(1000);
		timer.start();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof Settings) {
			Type type = (Type) arg;
			if (UIHelper.getInstance().getMode() == Mode.PLAYBACK) {
				if (type == Type.PLAYBACK_SPEED) {
					UIHelper.getInstance().updatePlaybackSpeed();
				} else if (type == Type.PLAYBACK_MODE) {
					UIHelper.getInstance().updatePlaybackMode();
				}
			}
			if (type == Type.GRAPH) {
				UIGraphicsHelper.getInstance().repaintTreeGraph();
			} else if (type == Type.GRAPH_CONNECTION) {
				UIGraphicsHelper.getInstance().repaintGraph();
			}
		} else if (o instanceof ScriptHandler) {
			if (arg == null) {
				UIGraphicsHelper.getInstance().repaintGraph();
			} else {
				ConnectionPainter painter = UIHelper.getInstance().getGraph()
						.getConnectionPainter();
				painter.lineTo((TraceInfo) arg);
				checkForRepaint();
			}
		}
	}

	private void checkForRepaint() {
		long now = System.currentTimeMillis();
		if (now - lastRepaint > FRAME_TIME) {
			lastRepaint = now;
			UIGraphicsHelper.getInstance().repaintGraph();
			outstandingRepaint = false;
		} else {
			outstandingRepaint = true;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (outstandingRepaint)
			checkForRepaint();
	}

}
