package travis.view.project.graph.connection;

import java.awt.Point;

import travis.model.script.TraceInfo;
import travis.view.Bspline;

public class GraphBspline extends Bspline {

	private final TraceInfo callerTrace;
	private final TraceInfo calleeTrace;

	public GraphBspline(TraceInfo callerTrace, TraceInfo calleeTrace,
			Point[] points) {
		super(points);
		this.callerTrace = callerTrace;
		this.calleeTrace = calleeTrace;
	}

	public TraceInfo getCallerTrace() {
		return callerTrace;
	}

	public TraceInfo getCalleeTrace() {
		return calleeTrace;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bspline) {
			Bspline s2 = (Bspline) obj;
			return getStartPointX() == s2.getStartPointX()
					&& getStartPointY() == s2.getStartPointY()
					&& getEndPointX() == s2.getEndPointX()
					&& getEndPointY() == s2.getEndPointY();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		int mult = 37;
		hash = hash * mult + getStartPointX();
		hash = hash * mult + getStartPointY();
		hash = hash * mult + getEndPointX();
		hash = hash * mult + getEndPointY();
		return hash;
	}

}
