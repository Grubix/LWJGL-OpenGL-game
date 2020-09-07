package particles;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import entities.Camera;
import renderEngine.ShaderProgram;
import tools.Maths;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/particles/particleShader_vs.glsl";
	private static final String FRAGMENT_FILE = "src/particles/particleShader_fs.glsl";

	private int numberOfAtlasRows_location;
	private int projectionMatrix_location;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getUniformLocations() {
		numberOfAtlasRows_location = super.getUniformLocation("numberOfAtlasRows");
		projectionMatrix_location = super.getUniformLocation("projectionMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "textureOffsets");
		super.bindAttribute(6, "blendFactor");
	}

	public void setProjectionMatrix(Matrix4f matrix) {
		super.setUniform(projectionMatrix_location, matrix);
	}
	public void setNumberOfAtlasRows(float numberOfRows) {
		super.setUniform(numberOfAtlasRows_location, numberOfRows);
	}

}
