package guis;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import models.Model;
import renderEngine.Scene;
import textMeshCreator.TextFont;

public class GuiTextRenderer {

	private GuiTextShader guiTextShader;

	public GuiTextRenderer(GuiTextShader guiTextShader) {
		this.guiTextShader = guiTextShader;
	}

	public void renderGuiTexts(Map<TextFont, List<GuiText>> guiTexts) {
		
		Scene.disableDepthTest();
		Scene.enableBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		for(TextFont font: guiTexts.keySet()) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, font.getTextureAtlas());
			for(GuiText guiText : guiTexts.get(font)) {
				render(guiText);
			}
		}
		
		Scene.enableDepthTest();
		Scene.disableBlending();
		
	}
	private void render(GuiText guiText){
		Model model = guiText.getModel();
		
		glBindVertexArray(model.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		guiTextShader.setColor(guiText.getFontColor());
		guiTextShader.setTranslation(guiText.getPosition());
		glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
	}

}
