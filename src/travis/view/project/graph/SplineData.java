package travis.view.project.graph;

import travis.model.script.TraceInfo;
import travis.view.project.graph.connection.GraphBspline;

public class SplineData {

	private final GraphBspline spline;
	private final ComponentData src;
	private final TraceInfo srcTrace;
	private final ComponentData dest;
	private final TraceInfo destTrace;
	private final double distance;

	public SplineData(GraphBspline spline, ComponentData src,
			TraceInfo srcTrace, ComponentData dest, TraceInfo destTrace,
			double distance) {
		this.spline = spline;
		this.src = src;
		this.srcTrace = srcTrace;
		this.dest = dest;
		this.destTrace = destTrace;
		this.distance = distance;
	}

	public GraphBspline getSpline() {
		return spline;
	}

	public ComponentData getSrc() {
		return src;
	}
	
	public TraceInfo getSrcTrace() {
		return srcTrace;
	}

	public ComponentData getDest() {
		return dest;
	}
	
	public TraceInfo getDestTrace() {
		return destTrace;
	}

	public double getDistance() {
		return distance;
	}

}
