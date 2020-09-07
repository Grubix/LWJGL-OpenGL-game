package guis;

import java.util.List;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import models.Model;
import renderEngine.Loader;
import renderEngine.Scene;
import tools.Maths;

public class GuiTextureRenderer {

	private final Model quad;
	private GuiTextureShader guiShader;
	
	public GuiTextureRenderer(Loader loader, GuiTextureShader guiShader) {
		float[] vertices = {-1,1,-1,-1,1,1,1,-1};
		this.quad = loader.loadModel(vertices, 2);
		this.guiShader = new GuiTextureShader();
	}

	public void renderGuis(List<GuiTexture> guis) {
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		
		Scene.disableDepthTest();
		Scene.enableBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		for(GuiTexture gui:guis) {
			Matrix4f transformationMatrix= Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
			guiShader.setTransformationMatrix(transformationMatrix);
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, gui.getTextureID());
			glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		
		Scene.enableDepthTest();
		Scene.disableBlending();
		
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
	
}
