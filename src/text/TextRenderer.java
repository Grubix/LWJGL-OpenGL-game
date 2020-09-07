package text;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import models.Model;
import renderEngine.Scene;
import textMeshCreator.TextFont;
import tools.Maths;

public class TextRenderer {

	private TextShader textShader;

	public TextRenderer(TextShader textShader, Matrix4f projectionMatrix) {
		this.textShader = textShader;
		
		textShader.enable();
		textShader.setProjectionMatrix(projectionMatrix);
		textShader.disable();
	}

	public void renderTexts(Map<TextFont, List<Text>> texts) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		Scene.disableCulling();
		
		for(TextFont font: texts.keySet()) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, font.getTextureAtlas());
			for(Text text : texts.get(font)) {
				render(text);
			}
		}
		
		Scene.enableCulling();
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
	}
	private void render(Text text){
		text.update();
		Model model = text.getModel();
		
		glBindVertexArray(model.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		textShader.setColor(text.getFontColor());
		setTransformationMatrix(text);
		glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}
	
	private void setTransformationMatrix(Text text) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				text.getPosition(),
				text.getRotation(),
				new Vector3f(text.getScale(), 1));
		
		textShader.setTransformationMatrix(transformationMatrix);
	}
}
