package text;

import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

import org.joml.Vector2f;
import org.joml.Vector3f;

import entities.Camera;
import models.Model;
import textMeshCreator.TextFont;

public class Text {

	private Model model;
	private Camera camera;
	private boolean faceCamera = false;
	
	private Vector3f position;
	private Vector3f rotation = new Vector3f(0f,0f,0f);
	private Vector2f scale = new Vector2f(1f,1f);
	private Vector3f fontColor = new Vector3f(0f,0f,0f);
	private String textString;
	private TextFont fontType;
	private float fontSize = 2f;
	private float maxLineWidth = 1f;
	private int numberOfLines = 1;
	

	public Text(String textString, Vector3f position, TextFont fontType) {
		this.textString = textString;
		this.position = position;
		this.fontType = fontType;
	}

	public void update() {
		if(faceCamera) {
			this.rotation.y = - camera.getRotation().y;
		}
	}
	public void faceCamera(Camera camera) {
		this.camera = camera;
		faceCamera = true;
	}
	public void setPosition(float xPos, float yPos, float zPos) {
		this.position.set(xPos, yPos, zPos);
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public void setRotation(float xRot, float yRot, float zRot) {
		this.position.set(xRot, yRot, zRot);
	}
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	public void setScale(float xScale, float yScale) {
		scale.set(xScale, yScale);
	}
	public void setScale(float scale) {
		this.scale.set(scale, scale);
	}
	public void setScale(Vector2f scale) {
		this.scale = scale;
	}
	public void setFontColor(float r, float g, float b) {
		this.fontColor.set(r, g, b);
	}
	public void setFontColor(Vector3f fontColor) {
		this.fontColor = fontColor;
	}
	public void setTextString(String newTextString) {
		this.textString = newTextString;
		createModel();
	}
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
		createModel();
	}
	public void setMaxLineWidth(float maxSize) {
		this.maxLineWidth = maxSize;
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
	
	public void increasePosition(float dxPos, float dyPos, float dzPos) {
		this.position.x += dxPos;
		this.position.y += dyPos;
		this.position.z += dzPos;
	}
	public void increaseRottaion(float dxRot, float dyRot, float dzRot) {
		this.rotation.x += dxRot;
		this.rotation.y += dyRot;
		this.rotation.z += dzRot;
	}
	public void increaseScale(float dxScale, float dyScale) {
		this.scale.x += dxScale;
		this.scale.y += dyScale;
	}
	
	public Model getModel() {
		return this.model;
	}
	public Vector3f getPosition() {
		return this.position;
	}
	public Vector3f getRotation() {
		return this.rotation;
	}
	public Vector2f getScale() {
		return this.scale;
	}
	public Vector3f getFontColor() {
		return this.fontColor;
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
		return numberOfLines;
	}
	
}