package skybox;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import org.joml.Matrix4f;

import entities.Camera;
import models.Model;
import renderEngine.Loader;
import renderEngine.Main;

public class SkyboxRenderer {
	
	private static final float SIZE = 500f;
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	private static final String[] TEXTURE_FILES_DAY = {
			"skybox/day/right.png",
			"skybox/day/left.png",
			"skybox/day/top.png",
			"skybox/day/bottom.png",
			"skybox/day/back.png",
			"skybox/day/front.png"
	};
	private static final String[] TEXTURE_FILES_NIGHT = {
			"skybox/day/vright.png",
			"skybox/day/vleft.png",
			"skybox/day/vtop.png",
			"skybox/day/bottom.png",
			"skybox/day/vback.png",
			"skybox/day/vfront.png"
	};
	
	private Model cube;
	private int textureID_day;
	private int textureID_night;
	private SkyboxShader skyboxShader;
	private float time = 0.0f;
	
	public SkyboxRenderer(Loader loader, SkyboxShader skyboxShader, Matrix4f projectionMatrix) {
		
		this.skyboxShader = skyboxShader;
		this.cube = loader.loadModel(VERTICES, 3);
		this.textureID_day = loader.loadCubeMap(TEXTURE_FILES_DAY);
		this.textureID_night = loader.loadCubeMap(TEXTURE_FILES_NIGHT);
		
		skyboxShader.enable();
		skyboxShader.connectTextureUnits();
		skyboxShader.setProjectionMatrix(projectionMatrix);
		skyboxShader.disable();
	}
	
	public void renderSkybox() {
		glBindVertexArray(cube.getVaoID());
		glEnableVertexAttribArray(0);
		
		bindTextures();
		glDrawArrays(GL_TRIANGLES, 0, cube.getVertexCount());
		
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
	
	private void bindTextures() {
		time += Main.getFrameTime() * 10;
		time %= 2400;
		int texture1;
		int texture2;
		float blendFactor;		
		if(time >= 0 && time < 5000){
			texture1 = textureID_night;
			texture2 = textureID_night;
			blendFactor = (time - 0)/(5000 - 0);
		}else if(time >= 5000 && time < 8000){
			texture1 = textureID_night;
			texture2 = textureID_day;
			blendFactor = (time - 5000)/(8000 - 5000);
		}else if(time >= 8000 && time < 21000){
			texture1 = textureID_day;
			texture2 = textureID_day;
			blendFactor = (time - 8000)/(21000 - 8000);
		}else{
			texture1 = textureID_day;
			texture2 = textureID_night;
			blendFactor = (time - 21000)/(24000 - 21000);
		}
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, texture1);
		
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_CUBE_MAP, texture2);
		
		skyboxShader.setBlendFactor(blendFactor);
	}

}
