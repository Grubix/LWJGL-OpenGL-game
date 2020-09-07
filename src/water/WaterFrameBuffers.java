package water;

import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import renderEngine.Loader;
import renderEngine.Main;

public class WaterFrameBuffers {

    private static final int REFLECTION_WIDTH = 320;
    private static final int REFLECTION_HEIGHT = 180;
     
    private static final int REFRACTION_WIDTH = 1280;
    private static final int REFRACTION_HEIGHT = 720;
 
    private int reflectionFrameBuffer;
    private int reflectionDepthBuffer;
    private int reflectionTexture;
     
    private int refractionFrameBuffer;
    private int refractionDepthTexture;
    private int refractionTexture;
	
	public WaterFrameBuffers(Loader loader) {
		reflectionFrameBuffer = loader.createFBO();
		reflectionDepthBuffer = loader.createDepthBufferAttachment(reflectionFrameBuffer,
				REFLECTION_WIDTH, REFLECTION_HEIGHT);
		reflectionTexture = loader.createTextureAttachment(reflectionFrameBuffer, 
				REFLECTION_WIDTH, REFLECTION_HEIGHT);
		
		refractionFrameBuffer = loader.createFBO();
		refractionDepthTexture = loader.createDepthTextureAttachment(refractionFrameBuffer,
				REFRACTION_WIDTH, REFRACTION_HEIGHT);
		refractionTexture = loader.createTextureAttachment(refractionFrameBuffer, 
				REFRACTION_WIDTH, REFRACTION_HEIGHT);
	}
	
	public void bindReflectionFrameBuffer() {
        bindFrameBuffer(reflectionFrameBuffer, REFLECTION_WIDTH, REFLECTION_HEIGHT);
	}
	public void bindRefractionFrameBuffer() {
        bindFrameBuffer(refractionFrameBuffer, REFRACTION_WIDTH, REFRACTION_HEIGHT);
	}
    public void unbindFrameBuffer() {//call to switch to default frame buffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
    }
    private void bindFrameBuffer(int frameBuffer, int width, int height){
        //glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glViewport(0, 0, width, height);
	}

	public int getReflectionFrameBuffer() {
		return reflectionFrameBuffer;
	}
	public int getReflectionDepthBuffer() {
		return reflectionDepthBuffer;
	}
	public int getReflectionTexture() {
		return reflectionTexture;
	}
	public int getRefractionFrameBuffer() {
		return refractionFrameBuffer;
	}
	public int getRefractionDepthTexture() {
		return refractionDepthTexture;
	}
	public int getRefractionTexture() {
		return refractionTexture;
	}
	

}
