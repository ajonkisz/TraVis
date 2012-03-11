package travis.model.attach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import travis.model.project.structure.StructComponent;
import travis.model.project.structure.StructMethod;
import travis.model.script.FileParser;
import travis.model.script.ScriptHandler;
import travis.model.script.TraceInfo;
import travis.util.Messages;

public class Playback implements Attacher, Runnable {
	
	private static final String DESCRIPTOR = "Playback";

	public enum Mode {
		PACKAGE, CLASS, METHOD
	}

	private static final long serialVersionUID = 4989826460595091553L;
	private static final Pattern RETURN_CALL_PATTERN = Pattern.compile("-.*");

	private volatile Mode mode;
	private volatile Map<Integer, StructMethod> selectedIds;

	private final File script;
	private final int tracesStart;
	private final int tracesLength;

	private volatile double playbackStart;
	private volatile double playbackEnd;
	private volatile TraceInfo previousTrace;
	private volatile LineNumberReader reader;
	private volatile boolean needScannerRestart;

	private final ExecutorService player;
	private volatile boolean running;
	private volatile boolean finished;
	private volatile long wait;

	public Playback(FileParser fp) throws FileNotFoundException {
		this.script = fp.getFile();
		reader = new LineNumberReader(new FileReader(script));

		mode = Mode.METHOD;
		selectedIds = Collections.emptyMap();

		tracesStart = fp.getTracesStart();
		tracesLength = fp.getTracesLength();

		playbackStart = 0d;
		playbackEnd = 1d;
		needScannerRestart = true;

		player = Executors.newSingleThreadExecutor();
		running = false;
		finished = false;
	}
	
	public File getScript() {
		return script;
	}
	
	public int getTracesStart() {
		return tracesStart;
	}
	
	public int getTracesLength() {
		return tracesLength;
	}

	public boolean isFinished() {
		return finished;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode, Map<Integer, StructMethod> map) {
		this.mode = mode;
		this.selectedIds = map;
	}

	public void setPlaybackStart(double playbackStart) {
		if (playbackStart < 0 || playbackStart > 1)
			throw new IllegalArgumentException(
					Messages.get("playback.position.exception"));
		this.playbackStart = playbackStart;
		needScannerRestart = true;
		if (!isRunning())
			restartScanner();
	}

	public void setPlaybackEnd(double playbackEnd) {
		if (playbackEnd < 0 || playbackEnd > 1)
			throw new IllegalArgumentException(
					Messages.get("playback.position.exception"));
		this.playbackEnd = playbackEnd;
	}

	public void setCurrentPos(double currentPos) {
		if (currentPos < 0 || currentPos > 1)
			throw new IllegalArgumentException(
					Messages.get("playback.position.exception"));
		previousTrace = null;
		restartScannerToPosition((int) (tracesStart + tracesLength * currentPos));
	}

	public void setCurvesPerSecond(int curvesNo) {
		wait = 1000 / curvesNo;
	}

	public double getCurrentPosPercent() {
		if (reader.getLineNumber() <= tracesStart)
			return 0d;
		return (double) (reader.getLineNumber() - tracesStart + 1)
				/ (tracesLength - 1);
	}

	@Override
	public void run() {
		previousTrace = null;
		while (running) {
			try {
				finished = false;
				previousTrace = readNextCall(previousTrace);
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private TraceInfo readNextCall(TraceInfo previousTrace)
			throws InterruptedException, IOException {
		String line;
		for (double posPc = getCurrentPosPercent(); (line = reader.readLine()) != null
				&& posPc >= playbackStart && posPc <= playbackEnd; posPc = getCurrentPosPercent()) {
			Scanner scanner = new Scanner(line);
			boolean returnCall = scanner.hasNext(RETURN_CALL_PATTERN);
			int methodId = Math.abs(scanner.nextInt());
			long callTime = scanner.nextLong();
			long threadId = scanner.nextLong();

			TraceInfo trace = new TraceInfo(methodId, returnCall, callTime,
					threadId);
			ScriptHandler.getInstance().sendTraceInfo(trace);

			if (mode == Mode.METHOD && returnFromMethod(trace, previousTrace)) {
				return trace;
			} else if (mode == Mode.CLASS
					&& returnFromClass(trace, previousTrace)) {
				return trace;
			} else if (mode == Mode.PACKAGE
					&& returnFromPackage(trace, previousTrace)) {
				return trace;
			}
		}

		// Can get to here only if did not return.
		needScannerRestart = true;
		running = false;
		finished = true;
		Thread.sleep(50);
		ScriptHandler.getInstance().sendEmptyCall();

		return previousTrace;
	}

	private boolean returnFromMethod(TraceInfo trace, TraceInfo previousTrace) {
		if (trace.isReturnCall() && previousTrace == null)
			return false;
		return selectedIds.containsKey(trace.getMethodId());
	}

	private boolean returnFromClass(TraceInfo trace, TraceInfo previousTrace) {
		if (trace.isReturnCall() && previousTrace == null)
			return false;

		StructMethod currentMethod = selectedIds.get(trace.getMethodId());
		if (previousTrace == null)
			return currentMethod != null;

		StructMethod previousMethod = selectedIds.get(previousTrace
				.getMethodId());
		if (currentMethod == null || previousMethod == null)
			return false;

		return !currentMethod.getParent().equals(previousMethod.getParent());
	}

	private boolean returnFromPackage(TraceInfo trace, TraceInfo previousTrace) {
		if (trace.isReturnCall() && previousTrace == null)
			return false;

		StructMethod currentMethod = selectedIds.get(trace.getMethodId());
		if (previousTrace == null)
			return currentMethod != null;

		StructMethod previousMethod = selectedIds.get(previousTrace
				.getMethodId());
		if (currentMethod == null || previousMethod == null)
			return false;

		StructComponent pckg = currentMethod.getParent().getParent();
		StructComponent previousPckg = previousMethod.getParent().getParent();

		if ((pckg == null && previousPckg != null)
				|| (previousPckg == null && pckg != null))
			return true;

		return !pckg.equals(previousPckg);
	}

	@Override
	public void detach() {
		stop();
	}

	@Override
	public void start() {
		player.submit(this);
	}

	private void restartScanner() {
		if (needScannerRestart) {
			int end = (int) (tracesStart + tracesLength * playbackStart);
			configureCurrentPos(end);
		}
	}

	private void restartScannerToPosition(int position) {
		boolean playing = isRunning();
		configureCurrentPos(position);
		if (playing)
			play();
	}

	private void configureCurrentPos(int position) {
		running = false;

		if (reader.getLineNumber() >= position) {
			try {
				reader = new LineNumberReader(new FileReader(script));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		while (reader.getLineNumber() < position) {
			try {
				reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		needScannerRestart = false;
		ScriptHandler.getInstance().sendEmptyCall();
	}

	public void play() {
		restartScanner();
		running = true;
		start();
	}

	public void pause() {
		running = false;
	}

	public void stop() {
		pause();
		needScannerRestart = true;
		restartScanner();
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public String getPid() {
		return "?";
	}

	@Override
	public String getName() {
		return script.getName();
	}

	@Override
	public String getDescriptor() {
		return DESCRIPTOR;
	}

}
