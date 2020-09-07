package guis;

import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import org.joml.Vector2f;
import org.joml.Vector3f;

import models.Model;
import textMeshCreator.TextFont;

public class GuiText {

	private Model model;
	
	private Vector2f position;
	private Vector3f color = new Vector3f(0f,0f,0f);
	private String textString;
	private TextFont fontType;
	private float fontSize = 2f;
	private float maxLineWidth = 10f;
	private int numberOfLines = 1;

	private boolean centerText = false;

	public GuiText(String textString, Vector2f position, TextFont fontType, boolean centered) {
		this.textString = textString;
		this.position = position;
		this.fontType = fontType;
		this.centerText = centered;
	}

	public void setPosition(float xPos, float yPos) {
		this.position.set(xPos, yPos);
	}
	public void setPosition(Vector2f position) {
		this.position = position;
	}
	public void setFontColor(float r, float g, float b) {
		this.color.set(r, g, b);
	}
	public void setTextString(String newTextString) {
		this.textString = newTextString;
		createModel();
	}
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
		createModel();
	}
	public void setMaxLineSize(float maxLineWidth) {
		this.maxLineWidth = maxLineWidth;
		createModel();
	}
	public void setNumberOfLines(int num) {
		this.numberOfLines = num;
	}
	public void createModel() {
		if(this.model != null) {
			glDeleteVertexArrays(this.model.getVaoID());
		}
		this.model = this.fontType.createTextModel(this);
	}

	public Model getModel() {
		return this.model;
	}
	public Vector2f getPosition() {
		return this.position;
	}
	public Vector3f getFontColor() {
		return this.color;
	}
	public String getTextString() {
		return textString;
	}
	public TextFont getFontType() {
		return this.fontType;
	}
	public float getFontSize() {
		return this.fontSize;
	}
	public float getMaxLineWidth() {
		return this.maxLineWidth;
	}
	public int getNumberOfLines() {
		return this.numberOfLines;
	}
	public boolean isCentered() {
		return centerText;
	}

}