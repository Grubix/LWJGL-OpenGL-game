package entities;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import renderEngine.ShaderProgram;
import tools.Maths;

public class EntityShader extends ShaderProgram {

	private static final String VS_FILE = "src/entities/entityShader_vs.glsl";
	private static final String FS_FILE = "src/entities/entityShader_fs.glsl";
	private static final int MAX_LIGHTS = 4;
	
	private int transformationMatrix_location; 
	private int projectionMatrix_location; 
	private int viewMatrix_location; 
	private int lightPosition_location[]; 
	private int lightColor_location[]; 
	private int lightAttenuation_location[];
	private int shineDamper_location;
	private int reflectivity_location;
	private int useFakeLighting_location;
	private int skyColor_location;
	private int density_location;
	private int gradient_location;
	private int numberOfAtlasRows_location;
	private int textureOffset_location;
	private int clipPlane_location;
	
	public EntityShader() {
		super(VS_FILE, FS_FILE);
	}
	
	@Override
	protected void bindAttributes() {
	//bind textures 
		super.bindAttribute(0, "vertices");
		super.bindAttribute(1, "textureCoords");	
		super.bindAttribute(2, "normals");
	}
	@Override
	protected void getUniformLocations() {
		this.transformationMatrix_location = super.getUniformLocation("transformationMatrix");
		this.projectionMatrix_location = super.getUniformLocation("projectionMatrix");
		this.viewMatrix_location = super.getUniformLocation("viewMatrix");
		this.shineDamper_location = super.getUniformLocation("shineDamper");
		this.reflectivity_location = super.getUniformLocation("reflectivity");
		this.useFakeLighting_location = super.getUniformLocation("useFakeLighting");
		this.skyColor_location = super.getUniformLocation("skyColor");
		this.density_location = super.getUniformLocation("density");
		this.gradient_location = super.getUniformLocation("gradient");
		this.numberOfAtlasRows_location = super.getUniformLocation("numberOfAtlasRows");
		this.textureOffset_location = super.getUniformLocation("textureOffset");
		this.clipPlane_location = super.getUniformLocation("clipPlane");
		
		lightPosition_location = new int[MAX_LIGHTS];
		lightColor_location = new int[MAX_LIGHTS];
		lightAttenuation_location = new int[MAX_LIGHTS];
		for(int i=0 ; i<MAX_LIGHTS ; i++) {
			lightPosition_location[i] = super.getUniformLocation("lightPosition[" + i + "]");
			lightColor_location[i] = super.getUniformLocation("lightColor[" + i + "]");
			lightAttenuation_location[i] = super.getUniformLocation("lightAttenuation[" + i + "]");

		}
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
	public void setLights(List<Light> lights) {
		for(int i=0 ; i<MAX_LIGHTS ; i++) {
			if(i<lights.size()) {
				super.setUniform(lightPosition_location[i], lights.get(i).getPosition());
				super.setUniform(lightColor_location[i], lights.get(i).getColor());
				super.setUniform(lightAttenuation_location[i], lights.get(i).getAttenuation());
			} else {
				super.setUniform(lightPosition_location[i], new Vector3f(0,0,0));
				super.setUniform(lightColor_location[i], new Vector3f(0,0,0));
				super.setUniform(lightAttenuation_location[i], new Vector3f(1,0,0));

			}
		}
	}
	public void setShineVariables(float damper, float reflectivity) {
		super.setUniform(shineDamper_location, damper);	
		super.setUniform(reflectivity_location, reflectivity);	
	}
	public void setFakeLightingVariable(boolean useFakeLighting) {
		super.setUniform(useFakeLighting_location, useFakeLighting);
	}
	public void setSkyColor(float R, float G, float B) {
		super.setUniform(skyColor_location, new Vector3f(R,G,B));
	}
	public void setFogVariables(float density, float gradient) {
		super.setUniform(density_location, density);
		super.setUniform(gradient_location, gradient);
	}
	public void setNumberOfAtlasRows(int numberOfRows) {
		super.setUniform(numberOfAtlasRows_location, (float)numberOfRows);
	}
	public void setTextureOffset(float xOffset, float yOffset) {
		super.setUniform(textureOffset_location, new Vector2f(xOffset, yOffset));
	}
	public void setClipPlane(Vector4f plane) {
		super.setUniform(clipPlane_location, plane);
	}
}
