package guis;

import org.joml.Vector2f;
import org.joml.Vector3f;

import renderEngine.ShaderProgram;

public class GuiTextShader extends ShaderProgram{

	private static final String VS_FILE = "src/guis/guiTextShader_vs.glsl";
	private static final String FS_FILE = "src/guis/guiTextShader_fs.glsl";
	
	private int color_location;
	private int translation_location;
	
	public GuiTextShader() {
		super(VS_FILE, FS_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	@Override
	protected void getUniformLocations() {
		color_location= super.getUniformLocation("color");
		translation_location = super.getUniformLocation("translation");

	}

	public void setColor(Vector3f color) {
		super.setUniform(color_location, color);
	}
	
	public void setTranslation(Vector2f translation) {
		super.setUniform(translation_location, translation);
	}
	
	public void clean() {
		
	}
	
}
