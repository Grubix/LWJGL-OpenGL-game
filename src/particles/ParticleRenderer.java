package particles;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.*;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import entities.Camera;
import models.Model;
import renderEngine.Loader;
import renderEngine.Scene;
import tools.Maths;

public class ParticleRenderer {
	
	private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	private static final int MAX_INSTANCES = 1000;
	private static final int INSTANCE_DATA_LENGTH = 21; 
	private static final FloatBuffer buffer = BufferUtils
			.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
	
	private Loader loader;
	private int vboID;
	private int pointer;
	private Model quad;
	private ParticleShader particleShader;
	
	public ParticleRenderer(Loader loader, ParticleShader particleShader, Matrix4f projectionMatrix){
		this.loader = loader;		
		this.quad = loader.loadModel(VERTICES, 2);
		this.particleShader = particleShader;
		
		this.vboID = loader.createVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		loader.addInstancedAttribute(quad.getVaoID(), vboID, 1, 4, INSTANCE_DATA_LENGTH, 0);
		loader.addInstancedAttribute(quad.getVaoID(), vboID, 2, 4, INSTANCE_DATA_LENGTH, 4);
		loader.addInstancedAttribute(quad.getVaoID(), vboID, 3, 4, INSTANCE_DATA_LENGTH, 8);
		loader.addInstancedAttribute(quad.getVaoID(), vboID, 4, 4, INSTANCE_DATA_LENGTH, 12);
		loader.addInstancedAttribute(quad.getVaoID(), vboID, 5, 4, INSTANCE_DATA_LENGTH, 16);
		loader.addInstancedAttribute(quad.getVaoID(), vboID, 6, 1, INSTANCE_DATA_LENGTH, 20);

		particleShader.enable();
		particleShader.setProjectionMatrix(projectionMatrix);
		particleShader.disable();
	}
	
	public void renderParticles(Map<ParticleTexture, List<Particle>> particles, Camera camera){
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glEnableVertexAttribArray(4);
		glEnableVertexAttribArray(5);
		glEnableVertexAttribArray(6);
		
		Scene.disableDepthMask();
		Scene.enableBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Scene.disableCulling();
	
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		
		for(ParticleTexture texture : particles.keySet()) {
			bindParticleTexture(texture);
			pointer = 0;
			List<Particle> partilcesList = particles.get(texture);
			float[] vboData = new float[partilcesList.size() * INSTANCE_DATA_LENGTH]; 
			for(Particle particle : partilcesList) {
				updateModelViewMatrix(particle, viewMatrix, vboData);
				updateTexCoordInfo(particle, vboData);
			}
			loader.updateVbo(vboID, vboData, buffer);
			glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), partilcesList.size());
		}
		
		Scene.enableCulling();
		Scene.enableDepthMask();
		Scene.disableBlending();
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(3);
		glDisableVertexAttribArray(4);
		glDisableVertexAttribArray(5);
		glDisableVertexAttribArray(6);
		glBindVertexArray(0);
	}

	private void bindParticleTexture(ParticleTexture texture) {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture.getTextureID());
		particleShader.setNumberOfAtlasRows(texture.getNumberOfAtlasRows());

	}
	private void updateModelViewMatrix(Particle particle, Matrix4f viewMatrix, float[] vboData) {
		
		Matrix4f modelMatrix = new Matrix4f().translate(particle.getPosition());

		modelMatrix.m00(viewMatrix.m00());
		modelMatrix.m01(viewMatrix.m10());
		modelMatrix.m02(viewMatrix.m20());
		
		modelMatrix.m10(viewMatrix.m01());
		modelMatrix.m11(viewMatrix.m11());
		modelMatrix.m12(viewMatrix.m21());
		
		modelMatrix.m20(viewMatrix.m02());
		modelMatrix.m21(viewMatrix.m12());
		modelMatrix.m22(viewMatrix.m22());
		
		modelMatrix.rotate((float)Math.toRadians(particle.getRotation()), new Vector3f(0,0,1));
		modelMatrix.scale(particle.getScale());
		Matrix4f modelViewMatrix = new Matrix4f(viewMatrix).mul(modelMatrix);
		storeMatrixData(modelViewMatrix, vboData);
	}
	private void storeMatrixData(Matrix4f modelViewMatrix, float[] vboData) {
		vboData[pointer++] = modelViewMatrix.m00();
		vboData[pointer++] = modelViewMatrix.m01();
		vboData[pointer++] = modelViewMatrix.m02();
		vboData[pointer++] = modelViewMatrix.m03();
		vboData[pointer++] = modelViewMatrix.m10();
		vboData[pointer++] = modelViewMatrix.m11();
		vboData[pointer++] = modelViewMatrix.m12();
		vboData[pointer++] = modelViewMatrix.m13();
		vboData[pointer++] = modelViewMatrix.m20();
		vboData[pointer++] = modelViewMatrix.m21();
		vboData[pointer++] = modelViewMatrix.m22();
		vboData[pointer++] = modelViewMatrix.m23();
		vboData[pointer++] = modelViewMatrix.m30();
		vboData[pointer++] = modelViewMatrix.m31();
		vboData[pointer++] = modelViewMatrix.m32();
		vboData[pointer++] = modelViewMatrix.m33();
	}
	private void updateTexCoordInfo(Particle particle, float[] vboData) {
		vboData[pointer++] = particle.getTextureOffset_1().x;
		vboData[pointer++] = particle.getTextureOffset_1().y;
		vboData[pointer++] = particle.getTextureOffset_2().x;
		vboData[pointer++] = particle.getTextureOffset_2().y;
		vboData[pointer++] = particle.getBlendFactor();
	}

}
