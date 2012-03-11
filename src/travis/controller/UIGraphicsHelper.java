package travis.controller;

import javax.swing.SwingUtilities;

import travis.controller.UIHelper.Mode;

public class UIGraphicsHelper {

	private static final UIGraphicsHelper INSTANCE = new UIGraphicsHelper();

	private UIGraphicsHelper() {
	}

	public static UIGraphicsHelper getInstance() {
		return INSTANCE;
	}

	public void resetConnectionsAndRepaintGraph() {
		resetConnections();
		repaintGraph();
	}

	public void resetConnectionsAndRepaintTree() {
		resetConnections();
		repaintTreeGraph();
	}

	public void resetConnections() {
		UIHelper.getInstance().getGraph().getConnectionPainter().reset();
	}

	public void repaintTreeGraph() {
		Runnable repainter = new Runnable() {
			@Override
			public void run() {
				UIHelper.getInstance().getGraph().updateImage();
			}
		};
		SwingUtilities.invokeLater(repainter);
	}

	public void repaintGraph() {
		Runnable repainter = new Runnable() {
			@Override
			public void run() {
				UIHelper.getInstance().getGraph().getConnectionPainter()
						.setNeedRepaint(true);
				UIHelper.getInstance().getGraph().repaint();
				if (UIHelper.getInstance().getMode() == Mode.PLAYBACK) {
					UIHelper.getInstance().getPlaybackPanel().repaint();
				}
			}
		};
		SwingUtilities.invokeLater(repainter);
	}

}
