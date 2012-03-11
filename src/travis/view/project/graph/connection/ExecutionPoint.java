package travis.view.project.graph.connection;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import travis.model.script.TraceInfo;
import travis.view.project.graph.ComponentData;
import travis.view.settings.Settings;

public class ExecutionPoint {

	private Point center;
	private TraceInfo trace;
	private ComponentData cd;
	private final Ellipse2D oval;

	public ExecutionPoint() {
		this.oval = new Ellipse2D.Double();
	}

	public ExecutionPoint(Point center) {
		this.center = center;
		this.oval = new Ellipse2D.Double();
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public void draw(Graphics2D g2) {
		if (center == null)
			return;

		int size = Settings.getInstance().getExecutionPointSize();
		g2.setColor(Settings.getInstance().getColors().getExecutionPointColor());
		oval.setFrame(center.x - size, center.y - size, size * 2, size * 2);
		g2.fill(oval);
	}
	
	public boolean contains(Point p) {
		return oval.contains(p);
	}
	
	public ComponentData getComponentData() {
		return cd;
	}

	public void setComponentData(ComponentData cd) {
		this.cd = cd;
	}
	
	public TraceInfo getTrace() {
		return trace;
	}

	public void setTrace(TraceInfo trace) {
		this.trace = trace;
	}

}
