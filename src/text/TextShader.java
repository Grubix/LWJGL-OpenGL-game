package text;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import entities.Camera;
import renderEngine.ShaderProgram;
import tools.Maths;

public class TextShader extends ShaderProgram{

	private static final String VS_FILE = "src/text/textShader_vs.glsl";
	private static final String FS_FILE = "src/text/textShader_fs.glsl";
	
	private int transformationMatrix_location; 
	private int projectionMatrix_location; 
	private int viewMatrix_location; 
	private int color_location;
	
	public TextShader() {
		super(VS_FILE, FS_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	@Override
	protected void getUniformLocations() {
		transformationMatrix_location = super.getUniformLocation("transformationMatrix");
		projectionMatrix_location = super.getUniformLocation("projectionMatrix");
		viewMatrix_location = super.getUniformLocation("viewMatrix");
		color_location= super.getUniformLocation("color");
	}

	public void setColor(Vector3f color) {
		super.setUniform(color_location, color);
	}
	public void setTransformationMatrix(Matrix4f matrix) {
		super.setUniform(transformationMatrix_location, matrix);
	}
	public void setProjectionMatrix(Matrix4f matrix) {
		super.setUniform(projectionMatrix_location, matrix);
	}
	public void setViewMatrix(Matrix4f matrix) {
		super.setUniform(viewMatrix_location, matrix);
	}
	
}
