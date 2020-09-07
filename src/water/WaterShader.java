package water;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import entities.Camera;
import entities.Light;
import renderEngine.ShaderProgram;
import tools.Maths;

public class WaterShader extends ShaderProgram{
	 
    private static final String VS_FILE = "src/water/waterShader_vs.glsl";
    private static final String FS_FILE = "src/water/waterShader_fs.glsl";

    private int projectionMatrix_location;
    private int transformationMatrix_location;
    private int viewMatrix_location;
    private int reflectionTexture_location;
    private int refractionTexture_location;
    private int dudvMap_location;
    private int normalMap_location;
    private int depthMap_location;
    private int moveFactor_location;
    private int cameraPosition_location;
    private int lightPosition_location;
    private int lightColor_location;

    public WaterShader() {
        super(VS_FILE, FS_FILE);
    }
     
    @Override
    protected void getUniformLocations() {
        projectionMatrix_location = super.getUniformLocation("projectionMatrix");
        transformationMatrix_location = super.getUniformLocation("transformationMatrix");
        viewMatrix_location = super.getUniformLocation("viewMatrix");
        reflectionTexture_location = super.getUniformLocation("reflectionTexture");
        refractionTexture_location = super.getUniformLocation("refractionTexture");
        dudvMap_location = super.getUniformLocation("dudvMap");
        normalMap_location = super.getUniformLocation("normalMap");
        depthMap_location = super.getUniformLocation("depthMap");
        moveFactor_location = super.getUniformLocation("moveFactor");
        cameraPosition_location = super.getUniformLocation("cameraPosition");
        lightPosition_location = super.getUniformLocation("lightPosition");
        lightColor_location = super.getUniformLocation("lightColor");
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
    
    public void setProjectionMatrix(Matrix4f matrix){
        super.setUniform(projectionMatrix_location, matrix);
    }
    public void setTransformationMatrix(Matrix4f matrix){
        super.setUniform(transformationMatrix_location, matrix);
    }
    public void setCameraPosition(Vector3f cameraPosition) {
    	super.setUniform(cameraPosition_location, cameraPosition);
    }
    public void setViewMatrix(Matrix4f matrix){
        super.setUniform(viewMatrix_location, matrix);
    }
    public void connectTextureUnits() {
    	super.setUniform(reflectionTexture_location, 0);
    	super.setUniform(refractionTexture_location, 1);
    	super.setUniform(depthMap_location, 2);
    	super.setUniform(dudvMap_location, 3);
    	super.setUniform(normalMap_location, 4);
    }
    public void setMoveFactor(float factor) {
    	super.setUniform(moveFactor_location, factor);
    }
    public void setLight(Light light) {
    	super.setUniform(lightPosition_location, light.getPosition());
    	super.setUniform(lightColor_location, light.getColor());

    }
    
}
