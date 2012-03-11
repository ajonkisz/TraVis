package travis.view.project.graph.arcrectangle.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import travis.view.TextStroke;
import travis.view.project.graph.arcrectangle.LabelledArcRectangle;

public abstract class TextOrientationState {

	private static final int FONT_TO_CENTER_DIVIDER = 3;
	protected static final int MARGIN = 6;

	protected LabelledArcRectangle rect;
	protected Font font;
	protected int fontCenterOffset;
	protected double textWidth;

	protected TextOrientationState(LabelledArcRectangle rect, Font font) {
		this.rect = rect;
		this.font = font;
		this.fontCenterOffset = font.getSize() / FONT_TO_CENTER_DIVIDER;
	}

	public static TextOrientationState getTextOrientation(
			LabelledArcRectangle rect, Font font) {
		Horizontal horiz = new Horizontal(rect, font);
		Vertical vert = new Vertical(rect, font);
		return horiz.getPathLength() > vert.getPathLength() ? horiz : vert;
	}

	public boolean doesTextFit(Graphics2D g2) {
		FontMetrics metrics = g2.getFontMetrics(font);
		textWidth = metrics.stringWidth(rect.getText());

		return textWidth < getPathLength()
				&& font.getSize() < getAvailableHeight();
	}
	
	public final void draw(Graphics2D g2) {
		// Preserve old stroke and transform so it can be restored later
		Stroke oldStroke = g2.getStroke();
		AffineTransform oldTransform = g2.getTransform();
		
		g2.setColor(Color.BLACK);
		g2.setStroke(new TextStroke(rect.getText(), font, false, false));
		g2.draw(getCenterAlignedShape());
		
		g2.setStroke(oldStroke);
		g2.setTransform(oldTransform);		
	}

	protected static double calculateAngle(double length, double radius) {
		return LabelledArcRectangle.calculateAngle(length, radius);
	}

	protected double getActualAngle() {
		return Math.abs(rect.getCenterAngle() - 90) % 360;
	}

	protected abstract boolean isFlipped();

	protected abstract double getPathLength();

	protected abstract double getAvailableHeight();
	
	protected abstract Shape getCenterAlignedShape();
	
	protected abstract Shape getShape();

}
