package renderEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class TextureData {
    
	private static final String TEXTURES_SRC = "textures/";
	
    private int width;
    private int height;
    private ByteBuffer byteBuffer;
     
    public TextureData(String texture_file){

		BufferedImage bImage = null;
		
		try {
			bImage = ImageIO.read(new File(TEXTURES_SRC + texture_file));
			this.width = bImage.getWidth();    //get image width
			this.height = bImage.getHeight();  //get image height
				
			//int[] pixels_raw = new int[width * height];
			int[] pixels_raw = bImage.getRGB(0,  0,  width,  height,  null,  0,  width);
			this.byteBuffer = createFlippedByteBuffer(pixels_raw);
			
			Main.printlnLog("texture file '" + TEXTURES_SRC + texture_file + "' loaded");
		} catch (IOException e) {
			//convert exception to string and print
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			//Main.printLog(sw.toString());
			System.out.print(sw.toString());
			return;
		}
    }

    public int getWidth(){
        return width;
    }   
    public int getHeight(){
        return height;
    }
    public ByteBuffer getByteBuffer(){
        return byteBuffer;
    }
 
	private static ByteBuffer createFlippedByteBuffer(int[] data) {
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length * 4);
		
		for(int i=0 ; i<data.length ; i++) {
			int pixel = data[i];
			buffer.put((byte)((pixel >> 16) & 0xFF)); //RED
			buffer.put((byte)((pixel >> 8) & 0xFF));  //GREEN
			buffer.put((byte)(pixel & 0xFF));         //BLUE
			buffer.put((byte)((pixel >> 24) & 0xFF)); //ALPHA
		}

		buffer.flip();
		return buffer;
	}

}
