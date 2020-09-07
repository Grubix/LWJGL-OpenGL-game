package skybox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import entities.Camera;
import renderEngine.Main;
import renderEngine.ShaderProgram;
import tools.Maths;

public class SkyboxShader extends ShaderProgram{
	 
    private static final String VS_FILE = "src/skybox/skyboxShader_vs.glsl";
    private static final String FS_FILE = "src/skybox/skyboxShader_fs.glsl";
    private static final float SKYBOX_ROTATE_SPEED = 0.02f;
    
    private int projectionMatrix_location;
    private int viewMatrix_location;
    private int fogColor_location;
    
    private int blendFactor_location;
    private int cubeMap1_location;
    private int cubeMap2_location;
    
    public SkyboxShader() {
        super(VS_FILE, FS_FILE);
    }
     
    @Override
    protected void getUniformLocations() {
        projectionMatrix_location = super.getUniformLocation("projectionMatrix");
        viewMatrix_location = super.getUniformLocation("viewMatrix");
        fogColor_location = super.getUniformLocation("fogColor");
        blendFactor_location = super.getUniformLocation("blendFactor");
        cubeMap1_location = super.getUniformLocation("cubeMap1");
        cubeMap2_location = super.getUniformLocation("cubeMap2");

    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
    
    public void setProjectionMatrix(Matrix4f matrix){
        super.setUniform(projectionMatrix_location, matrix);
    }
    public void setViewMatrix(Matrix4f matrix){
        Matrix4f skyboxViewMatrix = new Matrix4f(matrix);
        //remove translation
        skyboxViewMatrix.m30(0);
        skyboxViewMatrix.m31(0);
        skyboxViewMatrix.m32(0);
        
        super.setUniform(viewMatrix_location, skyboxViewMatrix);
    }
    public void setFogColor(float r, float g, float b) {
    	super.setUniform(fogColor_location, new Vector3f(r,g,b));
    }
    public void setBlendFactor(float blendFactor) {
    	super.setUniform(blendFactor_location, blendFactor);
    	
    }
    public void connectTextureUnits() {
    	super.setUniform(cubeMap1_location, 0);
    	super.setUniform(cubeMap2_location, 1);
    }
}
