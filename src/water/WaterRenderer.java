package water;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.GL_TEXTURE4;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;

import org.joml.Matrix4f;

import entities.Light;
import models.Model;
import renderEngine.Loader;
import renderEngine.Scene;
import tools.Maths;

public class WaterRenderer {

	private WaterShader waterShader;
	private Model waterQuad;
	
	public WaterRenderer(Loader loader, WaterShader waterShader, Matrix4f projectionMatrix) {
		this.waterShader = waterShader;
		this.waterQuad = createWaterQuad(loader);
		
		waterShader.enable();
		waterShader.connectTextureUnits();
		waterShader.setProjectionMatrix(projectionMatrix);
		waterShader.disable();
	}
	
	public void renderWaters(List<Water> water, Light light) {
        glBindVertexArray(waterQuad.getVaoID());
        glEnableVertexAttribArray(0); 
        Scene.enableBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        waterShader.setLight(light);

        for (Water waterTile : water) {	
            bindWater(waterTile);
            glDrawArrays(GL_TRIANGLES, 0, waterQuad.getVertexCount());
        }
        
        Scene.disableBlending();
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
		
	private void bindWater(Water waterTile) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				waterTile.getPosition(),
				waterTile.getRotation(),
				waterTile.getTileSize());
		
		waterShader.setTransformationMatrix(transformationMatrix);
		waterShader.setMoveFactor(waterTile.updateMoveFactor());
		
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, waterTile.getFBOs().getReflectionTexture());
        
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, waterTile.getFBOs().getRefractionTexture());
        
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, waterTile.getFBOs().getRefractionDepthTexture());
        
		glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, waterTile.getDudvMapTextureID());
        
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, waterTile.getNormalMapTextureID());
	}
	
	private Model createWaterQuad(Loader loader) {
        float[] vertices = {
        	-1,-1, -1,1, 1,-1,
        	1,-1, -1,1, 1,1
        };
        return loader.loadModel(vertices, 2);
	}

}
