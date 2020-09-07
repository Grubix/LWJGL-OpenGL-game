package guis;

import org.joml.Matrix4f;

import renderEngine.ShaderProgram;

public class GuiTextureShader extends ShaderProgram{
    
    private static final String VS_FILE = "src/guis/guiTextureShader_vs.glsl";
    private static final String FS_FILE = "src/guis/guiTextureShader_fs.glsl";
     
    private int location_transformationMatrix;
 
    public GuiTextureShader() {
        super(VS_FILE, FS_FILE);
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
	@Override
	protected void getUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}
    
    public void setTransformationMatrix(Matrix4f matrix){
        super.setUniform(location_transformationMatrix, matrix);
    }
}
