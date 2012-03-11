package travis.view.project.graph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JPanel;

import travis.controller.UIHelper;
import travis.model.attach.Playback.Mode;
import travis.view.Util;
import travis.view.project.graph.connection.ConnectionPainter;
import travis.view.project.graph.connection.ExecutionPoint;
import travis.view.project.graph.connection.GraphBspline;
import travis.view.settings.Settings;

public class GraphTooltip extends JPanel {

	private static final long serialVersionUID = 6214722087448317356L;

	private static final int MAX_CURVE_DIST = 2;
	private static final int MAX_CURVES_PER_SEC_FOR_PLAYBACK_TOOLTIP = 3;
	private static final Font FONT = new Font("Monospaced", Font.PLAIN, 12);
	private static final Color BACKGROUND = new Color(1f, 1f, 0.75f, 0.85f);
	private static final Color BACKGROUND_ALTERNATE = new Color(0.75f, 1f, 1f,
			0.85f);
	private static final Color SPLINE_COLOUR = Color.BLUE;
	private static final int MARGIN = 2;

	private volatile Point coord;
	private volatile String text;

	private final LinkedBlockingDeque<SplineData> splines;
	private final MouseListener mouseListener;

	private ExecutionPoint playbackExecutionPoint;

	public GraphTooltip() {
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		splines = new LinkedBlockingDeque<SplineData>();
		mouseListener = new MouseListener();

		addMouseMotionListener(mouseListener);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (coord == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHints(Util.HINTS);

		g2.setFont(FONT);
		FontMetrics fm = g2.getFontMetrics();
		int height = fm.getHeight() + fm.getDescent();
		Point tooltipCoord = new Point(coord);
		tooltipCoord.y -= fm.getDescent();

		if (playbackExecutionPoint != null) {
			String text = getTextForExecutionPoint(playbackExecutionPoint);
			Point epCoord = new Point(playbackExecutionPoint.getCenter());
			drawTooltip(fm, text, epCoord, g2, height, BACKGROUND);
		}

		if (text != null) {
			drawTooltip(fm, text, tooltipCoord, g2, height, BACKGROUND);
		} else if (!splines.isEmpty()) {
			GraphPanel graph = UIHelper.getInstance().getGraph();
			AffineTransform oldT = g2.getTransform();
			g2.translate(graph.getAlignX(), graph.getAlignY());

			for (SplineData sd : splines) {
				sd.getSpline().draw(g2, SPLINE_COLOUR);
			}
			g2.setTransform(oldT);

			if (splines.size() > 1) {
				String text = "Most recent first:";
				drawTooltip(fm, text, tooltipCoord, g2, height, BACKGROUND);
				tooltipCoord.y += height;
			}

			int i = 0;
			for (SplineData sd : splines) {
				String text = getTextForSpline(sd);

				if (i++ % 2 == 0)
					drawTooltip(fm, text, tooltipCoord, g2, height, BACKGROUND);
				else
					drawTooltip(fm, text, tooltipCoord, g2, height,
							BACKGROUND_ALTERNATE);

				tooltipCoord.y += height;
			}
		}
	}

	private String getTextForExecutionPoint(ExecutionPoint ep) {
		Mode mode = getMode();
		String text = null;
		switch (mode) {
		case METHOD:
			text = ep.getComponentData().getComp().getTooltipFriendlyName()
					+ " THREAD: " + ep.getTrace().getThreadId();
			break;
		case CLASS:
			text = ep.getComponentData().getComp().getParent()
					.getTooltipFriendlyName()
					+ " THREAD: " + ep.getTrace().getThreadId();
			break;
		case PACKAGE:
			text = ep.getComponentData().getComp().getParent().getParent()
					.getTooltipFriendlyName()
					+ " THREAD: " + ep.getTrace().getThreadId();
			break;
		default:
			break;
		}

		return text;
	}

	private String getTextForSpline(SplineData sd) {
		Mode mode = getMode();
		String text = null;
		switch (mode) {
		case METHOD:
			text = "FROM " + sd.getSrc().getComp().getTooltipFriendlyName()
					+ " TO " + sd.getDest().getComp().getTooltipFriendlyName()
					+ " THREAD: " + sd.getDestTrace().getThreadId();
			break;
		case CLASS:
			text = "FROM "
					+ sd.getSrc().getComp().getParent()
							.getTooltipFriendlyName()
					+ " TO "
					+ sd.getDest().getComp().getParent()
							.getTooltipFriendlyName() + " THREAD: "
					+ sd.getDestTrace().getThreadId();
			break;
		case PACKAGE:
			text = "FROM "
					+ sd.getSrc().getComp().getParent().getParent()
							.getTooltipFriendlyName()
					+ " TO "
					+ sd.getDest().getComp().getParent().getParent()
							.getTooltipFriendlyName() + " THREAD: "
					+ sd.getDestTrace().getThreadId();
			break;
		default:
			break;
		}

		return text;
	}
	
	private Mode getMode() {
		Mode mode = Mode.PACKAGE;
		if (Settings.getInstance().isDrawingStruct(Settings.STRUCT_METHOD)) {
			mode = Mode.METHOD;
		} else if (Settings.getInstance().isDrawingStruct(
				Settings.STRUCT_CLASS)) {
			mode = Mode.CLASS;
		}
		return mode;
	}

	private void drawTooltip(FontMetrics fm, String text, Point tooltipCoord,
			Graphics2D g2, int height, Color background) {
		int width = fm.stringWidth(text);
		int rectWidth = width + MARGIN * 2;

		Point coord = new Point(tooltipCoord);
		if (coord.x + rectWidth >= getWidth())
			coord.x -= coord.x + rectWidth - getWidth();

		g2.setColor(background);
		g2.fillRoundRect(coord.x,
				coord.y - height + fm.getDescent(), rectWidth, height,
				height / 2, height / 2);

		g2.setColor(Color.BLACK);
		g2.drawString(text, coord.x + MARGIN, coord.y);
	}

	public void displayExecutionPointTooltip(ExecutionPoint ep) {
		UIHelper helper = UIHelper.getInstance();
		if (helper.getMode() == UIHelper.Mode.PLAYBACK
				&& Settings.getInstance().getCurvesPerSec() <= MAX_CURVES_PER_SEC_FOR_PLAYBACK_TOOLTIP) {
			playbackExecutionPoint = ep;
		} else {
			playbackExecutionPoint = null;
		}

		if (ep == null || ep.getComponentData() == null
				|| ep.getCenter() == null) {
			playbackExecutionPoint = null;
		}
	}

	private class MouseListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
			text = null;
			splines.clear();
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			text = null;
			splines.clear();

			GraphPanel graph = UIHelper.getInstance().getGraph();
			TreeRepresentation treeRep = graph.getTreeRepRepresentation();
			ConnectionPainter connPainter = graph.getConnectionPainter();

			coord = e.getPoint();
			Point p = new Point(e.getPoint().x - graph.getAlignX(),
					e.getPoint().y - graph.getAlignY());

			ExecutionPoint ep = connPainter.getExecutionPoint();
			if (ep != null && ep.contains(p)) {
				text = ep.getComponentData().getComp().getTooltipFriendlyName()
						+ " THREAD: " + ep.getTrace().getThreadId();
				repaint();
				return;
			}

			ComponentData cd = treeRep.getElementForCoord(p);
			if (cd != null) {
				text = cd.getComp().getTooltipFriendlyName();
			} else {
				Collection<GraphBspline> gSplines = connPainter.getSplines();
				for (GraphBspline gSpline : gSplines) {
					double distance;
					if ((distance = gSpline.distanceFrom(p)) < MAX_CURVE_DIST) {
						ComponentData[] methods = treeRep.getMethods();
						ComponentData src = methods[gSpline.getCallerTrace()
								.getMethodId()];
						ComponentData dst = methods[gSpline.getCalleeTrace()
								.getMethodId()];
						splines.addFirst(new SplineData(gSpline, src, gSpline
								.getCallerTrace(), dst, gSpline
								.getCalleeTrace(), distance));
					}
				}

			}

			repaint();
		}
	}

}
