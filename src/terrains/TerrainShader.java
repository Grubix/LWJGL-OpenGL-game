package terrains;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import entities.Camera;
import entities.Light;
import renderEngine.ShaderProgram;
import tools.Maths;

public class TerrainShader extends ShaderProgram {

	private static final String VS_FILE = "src/terrains/terrainShader_vs.glsl";
	private static final String FS_FILE = "src/terrains/terrainShader_fs.glsl";
	private static final int MAX_LIGHTS = 4;
	
	private int transformationMatrix_location; 
	private int projectionMatrix_location; 
	private int viewMatrix_location; 
	private int lightPosition_location[]; 
	private int lightColor_location[]; 
	private int lightAttenuation_location[];
	private int shineDamper_location;
	private int reflectivity_location;
	private int skyColor_location;
	private int density_location;
	private int gradient_location;
	private int clipPlane_location;
	
	private int backgroundTexture_location;
	private int rTexture_location;
	private int gTexture_location;
	private int bTexture_location;
	private int blendMap_location;
	
	public TerrainShader() {
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
		this.skyColor_location = super.getUniformLocation("skyColor");
		this.density_location = super.getUniformLocation("density");
		this.gradient_location = super.getUniformLocation("gradient");
		this.clipPlane_location = super.getUniformLocation("clipPlane");
		
		this.backgroundTexture_location = super.getUniformLocation("backgroundTexture");
		this.rTexture_location = super.getUniformLocation("rTexture");
		this.gTexture_location = super.getUniformLocation("gTexture");
		this.bTexture_location = super.getUniformLocation("bTexture");
		this.blendMap_location = super.getUniformLocation("blendMap");
	
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
	public void setSkyColor(float R, float G, float B) {
		super.setUniform(skyColor_location, new Vector3f(R,G,B));
	}
	public void setFogVariables(float density, float gradient) {
		super.setUniform(density_location, density);
		super.setUniform(gradient_location, gradient);
	}
	public void setClipPlane(Vector4f plane) {
		super.setUniform(clipPlane_location, plane);
	}
	public void connectTextureUnits() {
		super.setUniform(backgroundTexture_location, (int)0);
		super.setUniform(rTexture_location, (int)1);
		super.setUniform(gTexture_location, (int)2);
		super.setUniform(bTexture_location, (int)3);
		super.setUniform(blendMap_location, (int)4);
	}
	
}
