package terrains;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
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
import org.joml.Vector3f;

import models.Model;
import tools.Maths;

public class TerrainRenderer {

	private TerrainShader terrainShader;
	
	public TerrainRenderer(TerrainShader terrainShader, Matrix4f projectionMatrix) {
		this.terrainShader = terrainShader;

		terrainShader.enable();                                //enable this shader
		terrainShader.setProjectionMatrix(projectionMatrix);   //set projection matrix
		terrainShader.connectTextureUnits();                   //TODO: DODAC OPIS
		terrainShader.disable();                               //disable this shader
	}
	
	public void renderTerrains(List<Terrain> terrains) {
		for(Terrain terrain:terrains) {
			bindTerrain(terrain);
			setTransformationMatrix(terrain);
			glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
			glDisable(GL_BLEND);
			
			unbindTerrain();
			
		}
	}
	private void bindTerrain(Terrain terrain) {
		Model model = terrain.getModel();
		
	//enable VAO attributes
		glBindVertexArray(model.getVaoID());    //bind VAO
		glEnableVertexAttribArray(0);           //Enable 1st attribute (VBO) - vertices data
		glEnableVertexAttribArray(1);           //Enable 2nd attribute (VBO) - texture coordinates
		glEnableVertexAttribArray(2);           //Enable 3rd attribute (VBO) - normals   
		
	//bind terrain textures
		bindTextures(terrain);
		
	//set shine variables for current texture (shineDamper and reflectivity)
		terrainShader.setShineVariables(1, 0);
	}
	private void unbindTerrain() {
	//disable VAO attributes
		glDisableVertexAttribArray(0);   //disable 1st VAO attribute (VBO)
		glDisableVertexAttribArray(1);   //disable 2nd VAO attribute (VBO)
		glDisableVertexAttribArray(2);   //disable 3rd VBO attribute (VBO)
		glBindVertexArray(0); 	         //unbind VAO
	}
	private void bindTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		
		glActiveTexture(GL_TEXTURE3);
		glBindTexture(GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		
		glActiveTexture(GL_TEXTURE4);
		glBindTexture(GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
		
	}
	private void setTransformationMatrix(Terrain terrain) {
	//create transformation matrix for current entity
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()),
				new Vector3f(0,0,0),
				new Vector3f(1,1,1));
		
	//set uniform variable "transformationMatrix" to transformationMatrix in vertex shader
		terrainShader.setTransformationMatrix(transformationMatrix);
	}

}
