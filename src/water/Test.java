package water;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import java.nio.ByteBuffer;

import renderEngine.Main;

import static org.lwjgl.opengl.GL30.*;

public class Test {
	private int frameBuffer;
	private int texture;
	
	public Test() {
        frameBuffer = glGenFramebuffers();
        //generate name for frame buffer
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, Main.getWindowWidth(), Main.getWindowHeight(), 0, 
      		  GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, texture, 0); 
        		
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);  
	}
	
	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);  
	}
	
	public int getTexture() {
		return this.texture;
	}

}
