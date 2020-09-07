package shadows;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.Model;
import models.ModelTexture;
import models.TexturedModel;
import renderEngine.Loader;
import renderEngine.Scene;

public class ShadowMapRenderer {

	private static final int SHADOW_MAP_SIZE = 2048;
	
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f lightViewMatrix = new Matrix4f();
	private Matrix4f projectionViewMatrix = new Matrix4f();
	private Matrix4f offset = createOffset();
	
	private ShadowFrameBuffer shadowFBO;
	private ShadowShader shadowShader;
	private ShadowBox shadowBox;

	public ShadowMapRenderer(Loader loader, Camera camera, ShadowShader shadowShader) {
		this.shadowShader = shadowShader;
		this.shadowBox = new ShadowBox(lightViewMatrix, camera);
		this.shadowFBO = new ShadowFrameBuffer(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE, loader);
	}
	public void render(Map<TexturedModel, List<Entity>> entities, Light sun) {
		
		shadowBox.update();
		updateOrthoProjectionMatrix();
		updateLightViewMatrix(new Vector3f(sun.getPosition()).negate());
		updateProjectionViewMatrix();
		
		shadowFBO.bindFrameBuffer();
		shadowShader.enable();
		Scene.enableDepthTest();
		glClear(GL_DEPTH_BUFFER_BIT);
		
		for (TexturedModel texturedModel : entities.keySet()) {
			bindTexturedModel(texturedModel);
			for (Entity entity : entities.get(texturedModel)) {
				setMvpMatrix(entity);
				glDrawElements(GL_TRIANGLES, texturedModel.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			}
		}
		
		shadowShader.disable();
		shadowFBO.unbindFrameBuffer();
		
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}

	public int getShadowMap() {
		return shadowFBO.getShadowMap();
	}
	public Matrix4f getProjectionMatrix() {
		return new Matrix4f(offset).mul(projectionMatrix);
	}
	public Matrix4f getLightViewMatrix() {
		return lightViewMatrix;
	}
	
	private void bindTexturedModel(TexturedModel texturedModel) {
		Model model = texturedModel.getModel();
		ModelTexture texture = texturedModel.getTexture();
		
		glBindVertexArray(model.getVaoID());
		glEnableVertexAttribArray(0);
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
	}
	private void setMvpMatrix(Entity entity) {
		Matrix4f test = new Matrix4f(projectionViewMatrix).mul(entity.getModelMatrix());
		shadowShader.setMvpMatrix(test);
	}

	private void updateOrthoProjectionMatrix() {
		projectionMatrix.identity();
		projectionMatrix.m00(2f / shadowBox.getWidth());
		projectionMatrix.m11(2f / shadowBox.getHeight());
		projectionMatrix.m22(-2f / shadowBox.getLength());
		projectionMatrix.m33(1);
	}
	private void updateLightViewMatrix(Vector3f lightDirection) {
		lightDirection.normalize();
		lightViewMatrix.identity();
		
		float pitch = (float) Math.acos(new Vector2f(lightDirection.x, lightDirection.z).length());
		lightViewMatrix.rotate(pitch, new Vector3f(1,0,0));
		
		float yaw = (float) Math.toDegrees(((float) Math.atan(lightDirection.x / lightDirection.z)));
		yaw = lightDirection.z > 0 ? yaw - 180 : yaw;
		lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0,1,0));

		lightViewMatrix.translate(shadowBox.getCenter().negate()); // TODO: mogą byc błędy
		//System.out.println(lightViewMatrix.toString());
	}
	private void updateProjectionViewMatrix() {
		projectionViewMatrix = new Matrix4f(projectionMatrix).mul(lightViewMatrix);
	}
	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}
	
}
