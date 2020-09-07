package water;

import org.joml.Vector3f;

import renderEngine.Loader;
import renderEngine.Main;

public class Water {
    
	private WaterFrameBuffers fbos;
    private Vector3f position;
    private Vector3f rotation = new Vector3f(0f,0f,0f);
	private float tileSize = 10f;
	private float waveSpeed = 0.05f;
	private float moveFactor = 0f;
	private int dudvMapTextureID;
	private int normalMapTextureID;
     
    public Water(String dudvMapTextureFile, String normalMapTextureFile, Vector3f position, Loader loader){
        this.fbos = new WaterFrameBuffers(loader);
        this.dudvMapTextureID = loader.loadTexture(dudvMapTextureFile);
        this.normalMapTextureID = loader.loadTexture(normalMapTextureFile);
    	this.position = position;
    }
 
    public float updateMoveFactor() {
        this.moveFactor += waveSpeed * Main.getFrameTime();
        this.moveFactor %= 1;
        
        return this.moveFactor;
    }
    
    public WaterFrameBuffers getFBOs() {
    	return fbos;
    }
    public Vector3f getPosition() {
    	return this.position;
    }
    public float getXPos() {
        return position.x;
    }
    public float getYPos() {
        return position.y;
    }
    public float getZPos() {
        return position.z;
    }
    public Vector3f getRotation() {
    	return this.rotation;
    }
    public float getTileSize() {
    	return this.tileSize;
    }
    public float getWaveSpeed() {
    	return this.waveSpeed;
    }
    public float getMoveFactor() {
    	return this.moveFactor;
    }
    public int getDudvMapTextureID() {
    	return this.dudvMapTextureID;
    }
    public int getNormalMapTextureID() {
    	return this.normalMapTextureID;
    }
    
}