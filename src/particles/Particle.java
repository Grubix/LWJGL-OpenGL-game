package particles;
//TODO ZROBIC PARTICLE TAK ZEBY NIE TOWRZYC ICH MILION TYLKO RESETOWAC JAK ZNIKNA I NADAC NOWE PARAMETRY!!!!
import org.joml.Vector2f;
import org.joml.Vector3f;

import entities.Camera;
import renderEngine.Main;

public class Particle {

	ParticleTexture texture;
	private Vector3f position;
	private Vector3f velocity;
	private float rotation = 0f;
	private float scale = 0.6f;
	private float gravity = 9f;
	private float lifeLength;
	
	private Vector2f textureOffset_1 = new Vector2f();
	private Vector2f textureOffset_2 = new Vector2f();
	private float blendFactor;
	
	private float elapsedTime;

	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float lifeLength) {
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.lifeLength = lifeLength;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	public void increaseRotation(float rotation) {
		this.rotation = rotation;
	}
	
	public ParticleTexture getTexture() {
		return this.texture;
	}
	public Vector3f getPosition() {
		return position;
	}
	public float getRotation() {
		return rotation;
	}
	public float getScale() {
		return scale;
	}
	
	public Vector2f getTextureOffset_1() {
		return textureOffset_1;
	}
	public Vector2f getTextureOffset_2() {
		return textureOffset_2;
	}
	public float getBlendFactor() {
		return blendFactor;
	}

	public boolean update(Camera camera) {
		float frameTime = Main.getFrameTime();
		velocity.y -= gravity * frameTime;
		position.add(new Vector3f(velocity).mul(frameTime));
		rotation += 5f;
		
		float delta = 0.1f*frameTime;
		
		if(scale - delta > 0) {
			scale -= delta;
		}
		
		updateTextureCoordsInfo();
		elapsedTime += frameTime;
		
		return elapsedTime < lifeLength;
	}
	private void updateTextureCoordsInfo() {
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfAtlasRows() * texture.getNumberOfAtlasRows();
		
		float progression = lifeFactor * stageCount;
		int index_1 = (int)Math.floor(progression);
		int index_2 = index_1 < stageCount - 1 ? index_1 + 1 : index_1;
	
		this.blendFactor = progression % 1;
		
		setTextureOffset(textureOffset_1, index_1);
		setTextureOffset(textureOffset_2, index_2);
	}
	private void setTextureOffset(Vector2f offset, int index) {
		int column = index % texture.getNumberOfAtlasRows();
		int row = index / texture.getNumberOfAtlasRows();
		offset.x = (float)column / texture.getNumberOfAtlasRows();
		offset.y = (float)row / texture.getNumberOfAtlasRows();
	}
	
}
