package particles;

public class ParticleTexture {

	private int textureID;
	private int numberOfAtlasRows;
	
	public ParticleTexture(int textureID, int numberOfAtlasRows) {
		this.textureID = textureID;
		this.numberOfAtlasRows = numberOfAtlasRows;
	}

	public int getTextureID() {
		return textureID;
	}
	public int getNumberOfAtlasRows() {
		return numberOfAtlasRows;
	}
	
}
