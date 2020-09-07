package entities;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import entities.nm.EntityShaderNM;
import models.Model;
import models.ModelTexture;
import models.TexturedModel;
import renderEngine.Main;
import renderEngine.Scene;
import tools.Maths;

public class EntityRenderer {

	private EntityShader entityShader;
	private EntityShaderNM entityShaderNM;
	private boolean normalMapIsSet;

	public EntityRenderer(EntityShader entityShader, EntityShaderNM entityShaderNM, Matrix4f projectionMatrix) {
		this.entityShader = entityShader;
		this.entityShaderNM = entityShaderNM;
		
		entityShader.enable();
		entityShader.setProjectionMatrix(projectionMatrix);   
		entityShader.disable();                               
		
		entityShaderNM.enable();
		entityShaderNM.setProjectionMatrix(projectionMatrix);
		entityShaderNM.connectTextureUnits();
		entityShaderNM.disable();
	}
	
	public void renderEntities(Map<TexturedModel, List<Entity>> entities, Camera camera) {
		Player player = Scene.getPlayer();
		
		for(TexturedModel texturedModel : entities.keySet()) {
			bindTexturedModel(texturedModel);
			for(Entity entity : entities.get(texturedModel)) {
				
				if(checkFrustum(0, entity, camera) || checkFrustum(1, entity, camera)) {
					continue;
				}
				if(player != entity && new Vector3f(player.getPosition()).sub(entity.getPosition()).length() < 0.5f) {
					continue;
				}
				
				setTransformationMatrix(entity);
				glDrawElements(GL_TRIANGLES, texturedModel.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
	}

	private void bindTexturedModel(TexturedModel texturedModel) {
		Model model = texturedModel.getModel();
		ModelTexture texture = texturedModel.getTexture();
		normalMapIsSet = texture.normalMapIsSet();
		
		if(texture.hasTransparency()) {
			Scene.disableCulling();
			Scene.disableDepthMask();
			Scene.enableBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}
		
		glBindVertexArray(model.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		
		entityShader.enable();
		entityShader.setShineVariables(texture.getShineDamper(), texture.getReflectivity());
		entityShader.setFakeLightingVariable(texture.useFakeLighting());
		entityShader.setNumberOfAtlasRows(texture.getNumberOfAtlasRows());
		
		if(normalMapIsSet) {

			glActiveTexture(GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D, texture.getNormalMapID());
			
			glActiveTexture(GL_TEXTURE2);
			glBindTexture(GL_TEXTURE_2D, texture.getDepthMapID());
			
			entityShaderNM.enable();
			entityShaderNM.setShineVariables(texture.getShineDamper(), texture.getReflectivity());
			entityShaderNM.setNumberOfAtlasRows(texture.getNumberOfAtlasRows());	
		}	

	}

	private void unbindTexturedModel() {
	//disable VAO attributes
		Scene.enableCulling();
		Scene.enableDepthMask();
		Scene.disableBlending();
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glBindVertexArray(0);
	}
	
	private void setTransformationMatrix(Entity entity) {
		if(normalMapIsSet && entity.useNormalMapping()) { //TODO ewentualnie dodac mozliwosc wylaczana dla entity poszczegolncyh
			entityShaderNM.enable();
			entityShaderNM.setTransformationMatrix(entity.getModelMatrix());
			entityShaderNM.setTextureOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
		} else {
			entityShader.enable();
			entityShader.setTransformationMatrix(entity.getModelMatrix());
			entityShader.setTextureOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
			
			//TODO zrobic porzadek z tym syfem
		}
	}
	
	private boolean checkFrustum(int plane, Entity entity, Camera camera) {
		Vector4f l = new Vector4f(camera.getFrustumNormal(plane));
		float e = new Vector3f(entity.getPosition()).dot(new Vector3f(l.x,l.y,l.z));
		if(e < -l.w) {
			return true;
		}
		return false;
	}

}
