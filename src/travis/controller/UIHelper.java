/*
 * UIHelper.java
 *
 * Copyright (C) 2011-2012, Artur Jonkisz, <travis.source@gmail.com>
 *
 * This file is part of TraVis.
 * See https://github.com/ajonkisz/TraVis for more info.
 *
 * TraVis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TraVis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with TraVis.  If not, see <http://www.gnu.org/licenses/>.
 */

package travis.controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import travis.model.attach.Attacher;
import travis.model.attach.Playback;
import travis.model.script.FileParser;
import travis.model.script.ScriptHandler;
import travis.util.Messages;
import travis.view.AttacherPanel;
import travis.view.MainFrame;
import travis.view.console.ConsolePanel;
import travis.view.playback.PlaybackPanel;
import travis.view.playback.PlaybackProgress;
import travis.view.project.graph.GraphLayeredPane;
import travis.view.project.graph.GraphPanel;
import travis.view.project.graph.GraphTooltip;
import travis.view.project.tree.ProjectTree;
import travis.view.project.tree.TreePanel;
import travis.view.settings.SettingsPane;

public class UIHelper {

    private static final UIHelper INSTANCE = new UIHelper();

    public enum MessageType {
        INFORMATION, WARNING, ERROR
    }

    public enum Mode {
        ATTACH, PLAYBACK
    }

    private Mode mode;

    private final JFileChooser fc;
    private volatile File currentDirectory;

    private final Vector<Attacher> attachers;

    private final MainFrame frame;
    private final TreePanel treePanel;
    private final AttacherPanel attacherPanel;
    private final ConsolePanel consolePanel;
    private final GraphPanel graph;
    private final GraphTooltip tooltip;
    private final GraphLayeredPane graphLayeredPane;
    private final SettingsPane settingsPane;
    private final PlaybackPanel playbackPanel;

    private final ExecutorService dispatcher;

    private UIHelper() {
        dispatcher = Executors.newFixedThreadPool(2);

        fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);

        attachers = new Vector<Attacher>();

        frame = new MainFrame();
        treePanel = new TreePanel();
        attacherPanel = new AttacherPanel();
        consolePanel = new ConsolePanel();
        graph = new GraphPanel();
        tooltip = new GraphTooltip();
        graphLayeredPane = new GraphLayeredPane(graph, tooltip);
        settingsPane = new SettingsPane();
        playbackPanel = new PlaybackPanel();

        mode = Mode.ATTACH;
    }

    public static UIHelper getInstance() {
        return INSTANCE;
    }

    public void populateFrame() {
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        currentDirectory = fc.getCurrentDirectory();
        frame.populateFrame();
        changeMode(Mode.ATTACH);
    }

    private void changeMode(Mode mode) {
        this.mode = mode;
        if (mode == Mode.ATTACH) {
            frame.setAttachMode();
        } else if (mode == Mode.PLAYBACK) {
            frame.setPlaybackMode();
        }
    }

    public Mode getMode() {
        return mode;
    }

    public Vector<Attacher> getAttachers() {
        return new Vector<Attacher>(attachers);
    }

    public void killAllAttachers() {
        synchronized (attachers) {
            for (Attacher attacher : attachers) {
                attacher.detach();
            }
            attachers.clear();
        }
        repaintAttachersTable();
    }

    public void startAttacher(Attacher attacher) {
        getProjectTree().setupScriptGenerator();

        try {
            attacher.start();
        } catch (IOException e) {
            displayException(e);
        }
    }

    public void addAndStartAttacher(final Attacher attacher) {
        final JDialog dialog = getAndShowProgressBarWindow(Messages
                .get("attaching"));
        dispatcher.submit(new Runnable() {
            @Override
            public void run() {
                attachers.add(attacher);
                getProjectTree().generateScript();

                try {
                    attacher.start();
                } catch (IOException e) {
                    displayException(e);
                }
                repaintAttachersTable();

                dialog.setVisible(false);
            }
        });
    }

    public void removeAndKillAttacher(Attacher attacher) {
        attacher.detach();
        attachers.remove(attacher);
        repaintAttachersTable();
    }

    public ConsolePanel getConsolePanel() {
        return consolePanel;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public void updatePlaybackMode() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                playbackPanel.updatePlaybackMode();
            }
        });
    }

    public void updatePlaybackSpeed() {
        playbackPanel.updatePlaybackSpeed();
    }

    public TreePanel getTreePanel() {
        return treePanel;
    }

    public ProjectTree getProjectTree() {
        return treePanel.getTree();
    }

    public AttacherPanel getAttacherPanel() {
        return attacherPanel;
    }

    public GraphPanel getGraph() {
        return graph;
    }

    public GraphTooltip getTooltip() {
        return tooltip;
    }

    public GraphLayeredPane getGraphLayeredPane() {
        return graphLayeredPane;
    }

    public SettingsPane getSettingsPane() {
        return settingsPane;
    }

    public PlaybackPanel getPlaybackPanel() {
        return playbackPanel;
    }

    public MainFrame getMainFrame() {
        return frame;
    }

    public void openClasspath() {
        if (!askToKillAllIfConnected())
            return;

        setupFilechooserForDirs();
        if (checkClassPath()) {
            final JDialog dialog = getAndShowProgressBarWindow(Messages
                    .get("opening.classpath"));
            dispatcher.submit(new Runnable() {
                @Override
                public void run() {
                    treePanel.buildTree(fc.getSelectedFile());
                    UIGraphicsHelper.getInstance()
                            .resetConnectionsAndRepaintTree();
                    dialog.setVisible(false);
                }
            });
        }
    }

    public void attachClasspath() {
        if (!askToKillAllIfConnected())
            return;

        setupFilechooserForDirs();
        if (checkClassPath()) {
            final JDialog dialog = getAndShowProgressBarWindow(Messages
                    .get("opening.classpath"));
            dispatcher.submit(new Runnable() {
                @Override
                public void run() {
                    treePanel.attachTree(fc.getSelectedFile());
                    UIGraphicsHelper.getInstance()
                            .resetConnectionsAndRepaintTree();
                    dialog.setVisible(false);
                }
            });
        }
    }

    private boolean checkClassPath() {
        if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
            return false;

        changeMode(Mode.ATTACH);
        currentDirectory = fc.getSelectedFile();
        File f = fc.getSelectedFile();
        if (!isClasspathValid(f)) {
            displayMessage(Messages.get("file.not.valid"), MessageType.ERROR);
            return false;
        }
        return true;
    }

    private boolean isClasspathValid(File f) {
        return f.isDirectory() && f.canRead();
    }

    private void setupFilechooserForDirs() {
        fc.setCurrentDirectory(getCurrentDirectory());
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return Messages.get("directories.only");
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
        });
    }

    public void openFile() {
        if (!askToKillAllIfConnected())
            return;

        setupFilechooserForFiles();
        if (fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
            return;

        final JDialog dialog = getAndShowProgressBarWindow(Messages
                .get("opening.file"));
        changeMode(Mode.PLAYBACK);
        currentDirectory = fc.getCurrentDirectory();

        dispatcher.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final FileParser fp = new FileParser(fc.getSelectedFile());
                    UIGraphicsHelper.getInstance().resetConnections();
                    getProjectTree().setRoot(fp);
                    playbackPanel.setFileParser(fp);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setVisible(false);
                        }
                    });
                } catch (IOException e) {
                    displayException(e);
                } catch (ClassNotFoundException e) {
                    displayException(e);
                }
            }
        });
    }

    public JDialog getAndShowProgressBarWindow(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(label, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.PAGE_END);

        final JOptionPane optionPane = new JOptionPane(panel,
                JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
                new Object[]{}, null);
        final JDialog dialog = optionPane.createDialog(frame,
                Messages.get("work.in.progress"));
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        dispatcher.submit(new Runnable() {
            @Override
            public void run() {
                dialog.pack();
                dialog.setResizable(false);
                dialog.setVisible(true);
            }
        });

        return dialog;
    }

    public void saveFileAs() {
        if (!askToKillAllIfConnected())
            return;

        setupFilechooserForFiles();
        if (fc.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION)
            return;

        currentDirectory = fc.getCurrentDirectory();
        File f = fc.getSelectedFile();
        if (!f.getName().matches(".*\\.vis")) {
            f = new File(f.getAbsolutePath() + ".vis");
        }

        final JDialog dialog = getAndShowProgressBarWindow(Messages
                .get("saving.file"));
        final File fFinal = f;
        dispatcher.submit(new Runnable() {
            @Override
            public void run() {
                getProjectTree().setupScriptGenerator();
                try {
                    ScriptHandler.getInstance().saveToFile(fFinal,
                            getProjectTree().getCheckedPaths());
                } catch (IOException e) {
                    displayException(e);
                }
                dialog.setVisible(false);
            }
        });
    }

    public void saveSubtrace() {
        if (mode != Mode.PLAYBACK)
            return;

        playbackPanel.pause();

        // Ensure subtrace is selected
        PlaybackProgress progress = playbackPanel.getPlaybackProgress();
        double start = progress.getPlaybackStart();
        double end = progress.getPlaybackEnd();
        if (start == 0d && end == 1d) {
            displayMessage(Messages.get("no.subtrace"), MessageType.INFORMATION);
            return;
        }

        // Ensure playback attacher
        Playback playback = playbackPanel.getPlayback();
        if (playback == null) {
            displayMessage(Messages.get("no.playback"), MessageType.ERROR);
            return;
        }

        setupFilechooserForFiles();
        if (fc.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION)
            return;

        currentDirectory = fc.getCurrentDirectory();
        File selectedFile = fc.getSelectedFile();
        final File f = selectedFile.getName().matches(".*\\.vis") ? selectedFile
                : new File(selectedFile.getAbsolutePath() + ".vis");

        // Ensure saving to a different file
        final File scriptFile = playback.getScript();
        if (f.equals(scriptFile)) {
            displayMessage(Messages.get("subtrace.to.trace"), MessageType.ERROR);
            saveSubtrace();
            return;
        }

        final long from = (long) (playback.getTracesStart() + (double) playback
                .getTracesLength() * start);
        final long to = (long) (playback.getTracesStart() + (double) playback
                .getTracesLength() * end);

        final JDialog dialog = getAndShowProgressBarWindow(Messages
                .get("saving.subtrace"));

        dispatcher.submit(new Runnable() {
            @Override
            public void run() {
                getProjectTree().generateScript();
                try {
                    ScriptHandler.getInstance().saveToFile(f,
                            getProjectTree().getCheckedPaths());
                    ScriptHandler.copyLines(scriptFile, f, from, to);
                } catch (IOException e) {
                    displayException(e);
                }
                dialog.setVisible(false);
            }
        });

    }

    private void setupFilechooserForFiles() {
        fc.setCurrentDirectory(getCurrentDirectory());
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return ".vis";
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().matches(".*\\.vis");
            }
        });
    }

    /**
     * Asks user, in form of a dialog window, if he wants to disconnect from
     * attached processes if connected to any.
     *
     * @return true if disconnected from attached processes, false otherwise.
     */
    public boolean askToKillAllIfConnected() {
        if (mode == Mode.PLAYBACK) {
            Playback p = playbackPanel.getPlayback();
            if (p != null) {
                p.pause();
            }
            return true;
        }
        if (attachers.isEmpty()) {
            return true;
        }
        if (displayConfirmation(Messages.get("disconnect.confirm"))) {
            killAllAttachers();
            return true;
        }
        return false;
    }

    public void repaintAttachersTable() {
        attacherPanel.getAttachersTable().repaintTable();
    }

    public void displayMessage(String msg, MessageType type) {
        String title;
        int messageType;
        switch (type) {
            case ERROR:
                title = Messages.get("error");
                messageType = JOptionPane.ERROR_MESSAGE;
                break;
            case INFORMATION:
                title = Messages.get("information");
                messageType = JOptionPane.INFORMATION_MESSAGE;
                break;
            case WARNING:
                title = Messages.get("warning");
                messageType = JOptionPane.WARNING_MESSAGE;
                break;
            default:
                return;
        }
        JOptionPane.showMessageDialog(frame, msg, title, messageType);
    }

    public boolean displayConfirmation(String msg) {
        JPanel panel = new JPanel();
        panel.add(new JLabel(msg));
        int status = JOptionPane.showConfirmDialog(frame, panel,
                Messages.get("confirm"), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null);
        return status == JOptionPane.OK_OPTION;
    }

    public void displayException(Exception e) {
        killAllAttachers();
        e.printStackTrace();

        StringBuilder sb = new StringBuilder(e.toString());
        for (StackTraceElement trace : e.getStackTrace()) {
            sb.append("\n   at ");
            sb.append(trace);
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 250));

        JOptionPane.showMessageDialog(frame, scrollPane,
                Messages.get("exception"), JOptionPane.ERROR_MESSAGE);
    }

}
