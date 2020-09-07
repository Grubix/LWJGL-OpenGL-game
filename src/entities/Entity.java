package entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import models.TexturedModel;
import tools.Maths;

public class Entity {

	private String name = "[Entity name]";
	private TexturedModel texturedModel;
	private boolean normalMapping = false;
	
	private Vector3f position;
	private Vector3f rotation = new Vector3f(0f,0f,0f);
	private Vector3f scale = new Vector3f(1f,1f,1f);
	
	private Matrix4f modelMatrix;

	private int textureAtlasIndex = 0;

	public Entity(TexturedModel texturedModel, Vector3f position, Vector3f rotation, Vector3f scale) {
		this.texturedModel = texturedModel;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		
		updateModelMatrix();
	}
	public Entity(TexturedModel texturedModel, Vector3f position, Vector3f rotation, float scale) {
		this(texturedModel, position, rotation, new Vector3f(scale, scale, scale));
	}
		
	public void enableNormalMapping() {
		normalMapping = true;
	}
	public void disableNormalMapping() {
		normalMapping = false;
	}
	
	public void updateModelMatrix() {
		this.modelMatrix = Maths.createTransformationMatrix(position, rotation, scale);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setTexturedModel(TexturedModel texturedModel) {
		this.texturedModel = texturedModel;
	}
	public void setPosition(float xPos, float yPos, float zPos) {
		this.position.set(xPos, yPos, zPos);
		updateModelMatrix();
	}
	public void setPosition(Vector3f position) {
		this.position = position;
		updateModelMatrix();
	}
	public void setRotation(float xRot, float yRot, float zRot) {
		this.position.set(xRot, yRot, zRot);
		updateModelMatrix();
	}
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
		updateModelMatrix();
	}
	public void setScale(float xScale, float yScale, float zScale) {
		this.scale.set(xScale, yScale, zScale);
		updateModelMatrix();
	}
	public void setScale(float scale) {
		this.scale = new Vector3f(scale, scale, scale);
		updateModelMatrix();
	}
	public void setScale(Vector3f scale) {
		this.scale = scale;
		updateModelMatrix();
	}
	public void setTextureAtlasIndex(int index) {
		this.textureAtlasIndex = index;
	}
	
	public void increasePosition(float dxPos, float dyPos, float dzPos) {
		this.position.x += dxPos;
		this.position.y += dyPos;
		this.position.z += dzPos;
		updateModelMatrix();
	}
	public void increaseRotation(float dxRot, float dyRot, float dzRot) {
		this.rotation.x += dxRot;
		this.rotation.y += dyRot;
		this.rotation.z += dzRot;
		updateModelMatrix();
	}
	public void increaseScale(float dxScale, float dyScale, float dzScale) {
		this.scale.x += dxScale;
		this.scale.y += dyScale;
		this.scale.z += dzScale;
		updateModelMatrix();
	}
	public void increaseScale(float dScale) {
		this.scale.x += dScale;
		this.scale.y += dScale;
		this.scale.z += dScale;
		updateModelMatrix();
	}
	
	public String getName() {
		return this.name;
	}
	public TexturedModel getTexturedModel() {
		return texturedModel;
	}
	public boolean useNormalMapping() {
		return normalMapping;
	}
	public Vector3f getPosition() {
		return position;
	}
	public Vector3f getRotation() {
		return rotation;
	}
	public Vector3f getScale() {
		return scale;
	}
	public Matrix4f getModelMatrix() {
		return this.modelMatrix;
	}
	public int getTextureAtlasIndex() {
		return this.textureAtlasIndex;
	}
	
	public String getPositionAsString() {
		return "x: " + Math.round(this.position.x) + " y: " + Math.round(this.position.y) + " z: " + Math.round(this.position.z);
	}
	public String getRotationAsString() {
		return "x: " + Math.round(this.rotation.x) + " y: " + Math.round(this.rotation.y) + " z: " + Math.round(this.rotation.z);
	}
	
	public float getTextureXOffset() {
		int column = textureAtlasIndex % texturedModel.getTexture().getNumberOfAtlasRows();
		return (float)column / (float)texturedModel.getTexture().getNumberOfAtlasRows();
	}
	public float getTextureYOffset() {
		int row = textureAtlasIndex / texturedModel.getTexture().getNumberOfAtlasRows();
		return (float)row / (float)texturedModel.getTexture().getNumberOfAtlasRows();
	}
}
