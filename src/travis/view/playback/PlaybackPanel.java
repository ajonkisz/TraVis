package travis.view.playback;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import travis.controller.UIGraphicsHelper;
import travis.controller.UIHelper;
import travis.model.attach.AttacherFactory;
import travis.model.attach.Playback;
import travis.model.attach.Playback.Mode;
import travis.model.script.FileParser;
import travis.view.Util;
import travis.view.settings.Settings;

public class PlaybackPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -9083678108698554427L;

	private Playback playback;
	private final JButton playButton;
	private final JButton pauseButton;
	private final JButton stopButton;
	private final PlaybackProgress progress;
	private volatile JButton activeButton;

	public PlaybackPanel() {
		super(new MigLayout("aligny center, alignx center, insets 0"));

		playButton = Util.getButtonWithIcon("playSmall32");
		playButton.addActionListener(this);
		pauseButton = Util.getButtonWithIcon("pauseSmall32");
		pauseButton.addActionListener(this);
		stopButton = Util.getButtonWithIcon("stopSmall32");
		stopButton.addActionListener(this);

		add(playButton, "cell 0 0");
		activeButton = playButton;
		add(stopButton, "cell 1 0");

		progress = new PlaybackProgress(this);
		add(progress, "cell 2 0, w 90%, grow");
	}

	public PlaybackProgress getPlaybackProgress() {
		return progress;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		activeButton.setEnabled(enabled);
		stopButton.setEnabled(enabled);
		progress.setVisible(enabled);
	}

	public Playback getPlayback() {
		return playback;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		progress.repaint();

		if (playback != null && !playback.isRunning()
				&& activeButton == pauseButton)
			togglePlayPause(pauseButton, playButton);
	}

	public void play() {
		togglePlayPause(playButton, pauseButton);
		if (playback != null) {
			if (playback.isFinished())
				UIGraphicsHelper.getInstance()
						.resetConnectionsAndRepaintGraph();

			updatePlaybackSpeed();
			updatePlaybackMode(false);
			playback.play();
		}
	}

	public void updatePlaybackSpeed() {
		if (playback != null) {
			playback.setCurvesPerSecond(Settings.getInstance()
					.getCurvesPerSec());
		}
	}

	public void updatePlaybackMode() {
		updatePlaybackMode(true);
	}

	private void updatePlaybackMode(boolean force) {
		if (playback != null) {
			Mode mode = Mode.PACKAGE;
			if (Settings.getInstance().isDrawingStruct(Settings.STRUCT_METHOD)) {
				mode = Mode.METHOD;
			} else if (Settings.getInstance().isDrawingStruct(
					Settings.STRUCT_CLASS)) {
				mode = Mode.CLASS;
			}

			if (mode != playback.getMode() || force) {
				playback.setMode(mode, UIHelper.getInstance().getProjectTree()
						.getSelectedMethodsIds());
			}
		}
	}

	public void pause() {
		togglePlayPause(pauseButton, playButton);
		if (playback != null)
			playback.pause();
	}

	private void togglePlayPause(JButton from, JButton to) {
		remove(from);
		add(to, "cell 0 0");
		to.requestFocusInWindow();
		activeButton = to;
		validate();
	}

	public void stop() {
		pause();
		if (playback != null)
			playback.stop();
		UIGraphicsHelper.getInstance().resetConnectionsAndRepaintGraph();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(playButton)) {
			play();
		} else if (e.getSource().equals(pauseButton)) {
			pause();
		} else if (e.getSource().equals(stopButton)) {
			stop();
		}
	}

	public void setFileParser(FileParser fp) {
		try {
			playback = AttacherFactory.newAttacher(fp);
			UIHelper.getInstance().startAttacher(playback);
			updatePlaybackMode(true);
			progress.setupPlaybackGraph(fp.getDepths(), fp.getMaxDepth());
			progress.resetPlaybackRange();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
