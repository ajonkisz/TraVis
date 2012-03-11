package travis.view.project.graph.arcrectangle;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import travis.view.project.graph.arcrectangle.state.TextOrientationState;

public class LabelledArcRectangle extends ArcRectangle {

	private static final int MIN_FONT_SIZE = 1;
	private static final int MAX_FONT_SIZE = 30;
	
	private int fontSize;
	private boolean dynamicFont;
	private String text;

	public LabelledArcRectangle(double centerX, double centerY, double radius,
			double startAngle, double degreeWidth, double height, String text) {
		super(centerX, centerY, radius, startAngle, degreeWidth, height);
		this.text = text;
	}

	public LabelledArcRectangle(Ellipse2D ellipse, double startAngle,
			double degreeWidth, double height, String text) {
		this(ellipse.getCenterX(), ellipse.getCenterY(),
				ellipse.getWidth() / 2, startAngle, degreeWidth, height, text);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);

		if (!dynamicFont && fontSize > MIN_FONT_SIZE) {
			Font font = new Font("Monospaced", Font.PLAIN, fontSize);
			TextOrientationState state = TextOrientationState.getTextOrientation(this, font);
			if (state.doesTextFit(g2))
				state.draw(g2);
		} else {
			int fontSize = MAX_FONT_SIZE;
			Font font;
			TextOrientationState state;
			do {
				font = new Font("Monospaced", Font.PLAIN, fontSize--);
				state = TextOrientationState.getTextOrientation(this, font);
			} while (!state.doesTextFit(g2) && fontSize >= MIN_FONT_SIZE);
			
			if (state.doesTextFit(g2))
				state.draw(g2);
		}
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
		setDynamicFont(fontSize < MIN_FONT_SIZE);
	}

	public boolean isDynamicFont() {
		return dynamicFont;
	}

	public void setDynamicFont(boolean dynamicFont) {
		this.dynamicFont = dynamicFont;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
