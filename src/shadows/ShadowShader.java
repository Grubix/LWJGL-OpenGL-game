package shadows;

import org.joml.Matrix4f;

import renderEngine.ShaderProgram;

public class ShadowShader extends ShaderProgram {
	
	private static final String VS_FILE = "src/shadows/shadowShader_vs.glsl";
	private static final String FS_FILE = "src/shadows/shadowShader_fs.glsl";
	
	private int mvpMatrix_location;

	public ShadowShader() {
		super(VS_FILE, FS_FILE);
	}
	
	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "in_position");
	}
	
	@Override
	public void getUniformLocations() {
		mvpMatrix_location = super.getUniformLocation("mvpMatrix");
		
	}
	
	public void setMvpMatrix(Matrix4f mvpMatrix){
		super.setUniform(mvpMatrix_location, mvpMatrix);
	}

}
