package models;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class ModelTexture {
	
	private int textureID;
	private int normalMapTextureID = -1;
	private int depthMapTextureID = -1;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private int numberOfAtlasRows = 1;

	public ModelTexture(int textureID) {
		this.textureID = textureID;
	}
	public ModelTexture(int textureID, int normalMapTextureID) {
		this.textureID = textureID;
		this.normalMapTextureID = normalMapTextureID;
	}

	public int getTextureID() {
		return textureID;
	}
	public int getNormalMapID() {
		return normalMapTextureID;
	}
	public int getDepthMapID() {
		return depthMapTextureID;
	}
	public float getShineDamper() {
		return shineDamper;
	}
	public float getReflectivity() {
		return reflectivity;
	}
	public int getNumberOfAtlasRows() {
		return numberOfAtlasRows;
	}
	public boolean hasTransparency() {
		return hasTransparency;
	}
	public boolean useFakeLighting() {
		return useFakeLighting;
	}
	public boolean normalMapIsSet() {
		return normalMapTextureID != -1 ? true : false;
	}

	public void setAtlasNumberOfRows(int atlasNumberOfRows) {
		this.numberOfAtlasRows = atlasNumberOfRows;
	}
	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}
	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}
	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}
	public void setNormalMap(int normalMapTextureID) {
		this.normalMapTextureID = normalMapTextureID;
	}
	public void setDepthMap(int depthMapTextureID) {
		this.depthMapTextureID = depthMapTextureID;
	}

}
