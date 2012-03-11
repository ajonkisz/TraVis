package travis.model.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;

import javax.swing.tree.TreePath;

import travis.model.project.structure.StructMethod;

public class ScriptHandler extends Observable {

	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd'T'HHmmssSSS");
	private static final ScriptHandler INSTANCE = new ScriptHandler();
	private static final int FLUSH_COUNT = 2000;
	public static final String TREEPATHS_TERMINATING_SEQUENCE = "treePathsEnd";
	public static final String HEADER_TERMINATING_SEQUENCE = "headerEnd";

	private File outputFile;
	private FileWriter fw;
	private int counter;

	private ScriptHandler() {
		configureNewFileWriter();
	}

	public static ScriptHandler getInstance() {
		return INSTANCE;
	}

	private void configureNewFileWriter() {
		try {
			close();
			outputFile = new File(DATE_FORMATTER.format(new Date()) + ".vis");
			outputFile.deleteOnExit();
			fw = new FileWriter(outputFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void close() {
		try {
			if (fw != null)
				fw.close();
			if (outputFile != null)
				outputFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveToFile(File dest) throws IOException {
		saveToFile(dest, null);
	}

	public void saveToFile(File dest, TreePath[] treePathToWrite)
			throws IOException {
		synchronized (outputFile) {
			FileOutputStream fis = new FileOutputStream(dest);
			ObjectOutputStream oos = new ObjectOutputStream(fis);
			if (treePathToWrite != null)
				oos.writeObject(treePathToWrite);
			oos.writeUTF(TREEPATHS_TERMINATING_SEQUENCE + '\n');
			oos.close();
			fis.close();

			flush();
			copyFile(outputFile, dest);
		}
	}

	public void writeScriptLine(String methodId, boolean returnCall,
			String nanoTime, String threadId) {
		try {
			FileWriter oldFw = fw;
			TraceInfo trace = null;
			synchronized (outputFile) {
				// Check if created a new FileOutputStream if so do not write
				// anything to a newly created file - discard input.
				if (oldFw != fw)
					return;

				if (returnCall)
					oldFw.write('-');
				oldFw.write(methodId);
				oldFw.write(' ');
				oldFw.write(nanoTime);
				oldFw.write(' ');
				oldFw.write(threadId);
				oldFw.write('\n');
				if (++counter % FLUSH_COUNT == 0) {
					flush();
				}
				trace = new TraceInfo(Integer.parseInt(methodId), returnCall,
						Long.parseLong(nanoTime), Long.parseLong(threadId));
			}
			sendTraceInfo(trace);
		} catch (IOException e) {
			// This should never happen!
			e.printStackTrace();
		}
	}

	public void sendTraceInfo(TraceInfo trace) {
		if (trace != null) {
			setChanged();
			notifyObservers(trace);
		}
	}

	public void sendEmptyCall() {
		setChanged();
		notifyObservers();
	}

	private void flush() {
		counter = 0;
		try {
			synchronized (outputFile) {
				fw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeScriptHeader(ScriptGenerator scriptGen) {
		try {
			synchronized (outputFile) {
				configureNewFileWriter();
				Map<StructMethod, Integer> map = scriptGen.getMethods();
				for (Entry<StructMethod, Integer> e : map.entrySet()) {
					StructMethod m = e.getKey();
					fw.write(String.format("%d %s %d %s %s %d%n", e.getValue(),
							m.getParentName(), m.getParent().getAccessFlag(),
							m.getName(), m.getDescriptor(), m.getAccessFlag()));
				}
				fw.write(HEADER_TERMINATING_SEQUENCE + "\n");
				flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyLines(File sourceFile, File destFile, long from,
			long to) throws IOException {
		if (from >= to)
			return;
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		LineNumberReader source = null;
		FileOutputStream destination = null;
		try {
			source = new LineNumberReader(new FileReader(sourceFile));
			destination = new FileOutputStream(destFile, true);

			// Skip not copied lines
			while (source.readLine() != null
					&& source.getLineNumber() < from)
				;

			int flusher = 0;
			String line;
			while ((line = source.readLine()) != null
					&& source.getLineNumber() <= to) {
				destination.write(line.getBytes());
				destination.write('\n');
				flusher++;
				if (flusher >= 5000) {
					destination.flush();
					flusher = 0;
				}
			}
			destination.flush();
		} finally {
			if (source != null)
				source.close();
			if (destination != null)
				destination.close();
		}
	}

	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile, true).getChannel();
			destination.transferFrom(source, destination.size(), source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}
}
