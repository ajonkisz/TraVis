package travis.view.project.graph;

import java.awt.Point;
import java.util.Arrays;

public class ControlPoint extends Point {

	private static final long serialVersionUID = 2985196999770983254L;

	private ControlPoint parentPoint;

	public ControlPoint(int x, int y) {
		super(x, y);
	}

	public ControlPoint getParentPoint() {
		return parentPoint;
	}

	public void setParentPoint(ControlPoint parentPoint) {
		this.parentPoint = parentPoint;
	}
	
	public int getDepth() {
		if (parentPoint == null)
			return 1;
		return 1 + parentPoint.getDepth();
	}

	public Point[] getPathTo(ControlPoint cp) {
		int depth = cp.getDepth();
		Point[] path1 = new Point[depth * 2];
		Point[] path2 = new Point[depth];
		populatePath(path1, this, 0);
		populatePath(path2, cp, 0);

		boolean foundCommonAncestor = false;
		int i = 0;
		for (Point p : path1) {
			if (path1[i] == null)
				break;
			if (path2[i] == null) {
				i++;
				break;
			}
			if (p.equals(path2[i])) {
				int length = addInReverse(path1, i + 1, i, path2);
				path1 = Arrays.copyOf(path1, length);
				foundCommonAncestor = true;
				break;
			}
			i++;
		}

		if (!foundCommonAncestor) {
			int length = addInReverse(path1, i, path2.length, path2);
			path1 = Arrays.copyOf(path1, length);
		}

		return path1;
	}

	private int addInReverse(Point[] dest, int offset, int beforeIndex, Point[] source) {
		beforeIndex--;
		for (; beforeIndex >= 0; beforeIndex--, offset++) {
			dest[offset] = source[beforeIndex];
		}
		return offset;
	}

	private void populatePath(Point[] path, ControlPoint cp, int i) {
		if (cp == null)
			return;
		path[i] = cp;
		populatePath(path, cp.getParentPoint(), ++i);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}