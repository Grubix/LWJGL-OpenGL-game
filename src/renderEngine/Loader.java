package renderEngine;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteRenderbuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;
import static org.lwjgl.opengl.GL33.*;

import java.awt.datatransfer.FlavorTable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;

import entities.Entity;
import models.Model;
import models.ModelTexture;
import models.TexturedModel;
import objLoader.ModelData;
import objLoader.OBJLoader;
import objLoader.nm.OBJLoaderNM;
import particles.Particle;
import particles.ParticleTexture;
import terrains.Terrain;
import terrains.TerrainTexture;
import terrains.TerrainTexturePack;
import textMeshCreator.MetaData;
import textMeshCreator.TextFont;

public class Loader {

	//VAO - Vertex Array Object
	//VBO - Vertex Buffer Object
	
	//VAO has Attribute List (model data)
	//Attribute List:
	//vertex positions       //VBO_0
	//vertex colors          //VBO_1
	//texture coordinates    //VBO_2
	//...
	
	private List<Integer> VAOs = new ArrayList<Integer>();
	private List<Integer> VBOS = new ArrayList<Integer>();
	private List<Integer> FBOs = new ArrayList<Integer>();
	private List<Integer> RBOs = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
	//create vaoID and add it to VAOS
		int vaoID = createVAO();
		
		glBindVertexArray(vaoID);
		storeDataInAttrList(0, 3, vertices);
		storeDataInAttrList(1, 2, textureCoords); 
		storeDataInAttrList(2, 3, normals);       
		bindIndices(indices); 
		glBindVertexArray(0);
		
		return new Model(vaoID, indices.length);
	}
	public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices) {
		int vaoID = createVAO();  
		
		glBindVertexArray(vaoID);
		storeDataInAttrList(0, 3, vertices);
		storeDataInAttrList(1, 2, textureCoords);
		storeDataInAttrList(2, 3, normals);
		storeDataInAttrList(3, 3, tangents);
		bindIndices(indices);
		glBindVertexArray(0); 
		
		return new Model(vaoID, indices.length);
	}
	public Model loadModel(float[] vertices, int vertices_dimensions) {
		int vaoID = createVAO();
		
		glBindVertexArray(vaoID);
		storeDataInAttrList(0, vertices_dimensions, vertices);
		glBindVertexArray(0);
		
		return new Model(vaoID, vertices.length / vertices_dimensions);
	}
	public Model loadModel(float[] vertices, int vertices_dimensions, float textureCoords[]) {
		int vaoID = createVAO();         
		
		glBindVertexArray(vaoID);                
		storeDataInAttrList(0, vertices_dimensions, vertices);
		storeDataInAttrList(1, 2, textureCoords); 
		glBindVertexArray(0);
		
		return new Model(vaoID, vertices.length / vertices_dimensions);
	}
	public Model loadModel(String objFilePath) {
		ModelData data = OBJLoaderNM.loadOBJ(objFilePath);
		return loadModel(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getTangents(), data.getIndices());
	}
	public Model loadEgg(float a, float b, int slices, int stacks) {
		
		float dX = Math.abs(2 * a / (float)slices);
		double dPhi = 2 * Math.PI / (double)stacks;
		
		float x = dX - a;
		float y;
		float z;
		float r;

		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
	//---VERTICES---------------------------------------------------------------------------------------------//		
		vertices.add(new Vector3f(-a,0,0));
		for(int i=0 ; i<slices-1 ; i++) {
			r = b / a * (float)Math.sqrt(Math.abs(a*a - x*x));
			for(int j=0 ; j<stacks ; j++) {
				y = r * (float)Math.sin(dPhi * j);
				z = r * (float)Math.cos(dPhi * j);
				vertices.add(new Vector3f(x,y,z));
			}	
			x += dX;
		}
		vertices.add(new Vector3f(a,0,0));
	//---INDICES----------------------------------------------------------------------------------------------//
		for(int i=0 ; i<stacks-1 ; i++) {
			indices.add(0); indices.add(i+1); indices.add(i+2);
		}
		indices.add(0); indices.add(stacks); indices.add(1);
			
		for(int i=0 ; i<slices-2 ; i++) {
			for(int j=0 ; j<stacks-1 ; j++) {
				indices.add(stacks*i+j+1); indices.add(stacks*(i+1)+j+1); indices.add(stacks*(i+1)+j+2);
				indices.add(stacks*(i+1)+j+2); indices.add(stacks*i+j+2); indices.add(stacks*i+j+1);
			}
			indices.add(stacks*(i+1)); indices.add(stacks*(i+2)); indices.add(stacks*(i+1)+1);
			indices.add(stacks*(i+1)+1); indices.add(stacks*i+1); indices.add(stacks*(i+1));
		}
		
		int start = stacks * (slices - 2) + 1;
		int end = stacks * (slices - 1) + 1;
		
		for(int i=0 ; i<stacks-1 ; i++) {
			indices.add(end); indices.add(start+i+1); indices.add(start+i);
		}
		indices.add(end); indices.add(start); indices.add(end-1);
	//--------------------------------------------------------------------------------------------------------//
		
		float[] verticesArray = new float[vertices.size()*3];
		float[] texArray = new float[vertices.size()*2];
		float[] normalsArray = new float[vertices.size()*3];
		int[] indicesArray = new int[indices.size()];
			
		for(int i=0 ; i<vertices.size() ; i++) { 
			verticesArray[i*3] = vertices.get(i).x;
			verticesArray[i*3+1] = vertices.get(i).y;
			verticesArray[i*3+2] = vertices.get(i).z;
			
			normalsArray[i*3] = vertices.get(i).x;
			normalsArray[i*3+1] = vertices.get(i).y;
			normalsArray[i*3+2] = vertices.get(i).z;
		}
		
		for(int i=0 ; i<indices.size(); i++) { 
			indicesArray[i] = indices.get(i);
		}
		
	//---CHECK------------------------------------------------------------------------------------------------//
//		for(int i=0 ; i<vertices.size() ; i++) {
//			Vector3f v = vertices.get(i);
//			System.out.println("["+i+"][" + v.x + "," +v.y + "," +v.y + "]");
//		}
//		
//		for(int i=0; i<indicesArray.length/6 ; i++) {
//			System.out.println(
//			indicesArray[6*i]+","+
//			indicesArray[6*i+1]+","+
//			indicesArray[6*i+2]+","+
//			indicesArray[6*i+3]+","+
//			indicesArray[6*i+4]+","+
//			indicesArray[6*i+5]);
//		}
		
//		for(int i=0; i<normalsArray.length/3 ; i++) {
//			System.out.println(normalsArray[3*i] + "," + normalsArray[3*i+1] + "," + normalsArray[3*i+2]);
//		}
		
	//--------------------------------------------------------------------------------------------------------//
		vertices.clear();
		indices.clear();
		return loadModel(verticesArray, texArray, normalsArray, indicesArray);
	}
	public Model loadPlate(float a, float b, int xDiv, int zDiv) {
		
		float dX = a / xDiv;
		float dZ = b / zDiv;
		float startX = -a / 2;
		float startZ = -b / 2;
		float x,z,y = 0.0f;
		
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		for(int i=0 ; i<xDiv+1 ; i++) {
			x = startX + dX * i;
			for(int j=0 ; j<zDiv+1 ; j++) {
				z = startZ + dZ * j;
				vertices.add(new Vector3f(x,y,z));
			}	
		}
		
		for(int i=0 ; i<xDiv ; i++) {
			for(int j=0 ; j<zDiv ; j++) {
				indices.add((zDiv+1)*(i+1)+j+1); indices.add((zDiv + 1)*i+j); indices.add((zDiv+1)*i+j+1);
				indices.add((zDiv+1)*i+j); indices.add((zDiv+1)*(i+1)+j+1); indices.add((zDiv+1)*(i+1)+j);
			}	
		}
		
		float[] verticesArray = new float[vertices.size()*3];
		float[] texArray = new float[vertices.size()*2];
		float[] normalsArray = new float[vertices.size()*3];
		int[] indicesArray = new int[indices.size()];
			
		for(int i=0 ; i<vertices.size() ; i++) { 
			verticesArray[i*3] = vertices.get(i).x;
			verticesArray[i*3+1] = vertices.get(i).y;
			verticesArray[i*3+2] = vertices.get(i).z;
			
			normalsArray[i*3] = 0;
			normalsArray[i*3+1] = 1;
			normalsArray[i*3+2] = 0;
		}
		
		for(int i=0 ; i<indices.size(); i++) { 
			indicesArray[i] = indices.get(i);
		}
		
		for(int i=0 ; i<texArray.length/2; i++) { 
			texArray[i*2] = 0;
			texArray[i*2+1] = 1;
		}
		
		vertices.clear();
		indices.clear();
		return loadModel(verticesArray, texArray, normalsArray, indicesArray);	
	}
	public Model loadCuboid(float a, float b, float h) {
		
		float half_a = a / 2;
		float half_b = b / 2;
		float half_h = h / 2;
		
		float[] vertices = new float[] {
				half_a, half_h, half_b,     //0
				half_a, -half_h, half_b,    //1
				-half_a, -half_h, half_b,   //2
				-half_a, half_h, half_b,    //3
				
				half_a, half_h, -half_b,    //4
				half_a, -half_h, -half_b,   //5
				half_a, -half_h, half_b,    //6
				half_a, half_h, half_b,     //7
				
				-half_a, half_h, -half_b,   //8
				-half_a, -half_h, -half_b,  //9
				half_a, -half_h, -half_b,  //10
				half_a, half_h, -half_b,   //11
				
				-half_a, half_h, half_b,   //12
				-half_a, -half_h, half_b,  //13
				-half_a, -half_h, -half_b, //14
				-half_a, half_h, -half_b,  //15
				
				half_a, half_h, -half_b,   //16
				half_a, half_h, half_b,    //17
				-half_a, half_h, half_b,   //18
				-half_a, half_h, -half_b,  //19
				
				-half_a, -half_h, -half_b, //20
				-half_a, -half_h, half_b,  //21
				half_a, -half_h, half_b,   //22
				half_a, -half_h, -half_b,  //23
		};
		float[] textureCoords = new float[] {
				1,1,1,0,0,0,0,1,
				1,1,1,0,0,0,0,1,
				1,1,1,0,0,0,0,1,
				1,1,1,0,0,0,0,1,
				1,1,1,0,0,0,0,1,
				1,1,1,0,0,0,0,1,
		};
		float[] normals = new float[] {
				0,0,1,0,0,1,0,0,1,0,0,1,
				1,0,0,1,0,0,1,0,0,1,0,0,
				0,0,-1,0,0,-1,0,0,-1,0,0,-1,
				-1,0,0,-1,0,0,-1,0,0,-1,0,0,
				0,1,0,0,1,0,0,1,0,0,1,0,
				0,-1,0,0,-1,0,0,-1,0,0,-1,0,
		};
		int[] indices = new int[] {
				0,3,2,2,1,0,
				4,7,6,6,5,4,
				8,11,10,10,9,8,
				12,15,14,14,13,12,
				16,19,18,18,17,16,
				20,23,22,22,21,20
		};
		
		return loadModel(vertices, textureCoords, normals, indices);
	}
	
	public int createVAO() {
		int vertexArrayID = glGenVertexArrays();
		VAOs.add(vertexArrayID);
		
		return vertexArrayID;
	}
	public int createVBO() {
		int vertexBufferID = glGenBuffers();
		VBOS.add(vertexBufferID);
		
		return vertexBufferID;
	}
	public int createVBO(int floatCount) {
		int vertexBufferID = createVBO();
		
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
		glBufferData(GL_ARRAY_BUFFER, floatCount * 4, GL_STREAM_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		return vertexBufferID;
	}
	public int createFBO() {
        int frameBufferID = glGenFramebuffers();
        FBOs.add(frameBufferID);
        
        return frameBufferID;
	}
	public int createRBO() {
        int renderBufferID = glGenRenderbuffers();
        RBOs.add(renderBufferID);
        
        return renderBufferID;
	}
	public int createTexture() { //TODO: dodac to we wszystkich metodach
		int textureID = glGenTextures();
		textures.add(textureID);
		
		return textureID;
	}
	
	public int loadTexture(String textureFile) {

		TextureData textureData = new TextureData(textureFile);
		
		int textureID = glGenTextures();  //create texture ID
		textures.add(textureID);
		
		int width = textureData.getWidth();
		int height = textureData.getHeight();
		ByteBuffer buffer = textureData.getByteBuffer();

		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glGenerateMipmap(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		//glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 16.0f);
			
		return textureID;
	}
	public int loadCubeMap(String[] textureFiles) {
		
		//faces order (skybox):
		//
		//right face
		//left face
		//top face
		//bottom face
		//back face
		//front face
		
		int textureID = glGenTextures();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
		
		for(int i=0 ; i<textureFiles.length ; i++) {
			TextureData texData = new TextureData(textureFiles[i]);
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA, texData.getWidth(), 
					texData.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texData.getByteBuffer());
		}
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP,  GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP,  GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		textures.add(textureID);
		return textureID;
	}
	public int createTextureAttachment(int frameBufferID, int width, int height) {
        int textureID = glGenTextures();
        textures.add(textureID);
        
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 
        		0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureID, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
	
        return textureID;
	}
	public int createDepthTextureAttachment(int frameBufferID, int width, int height) {
        int textureID = glGenTextures();
        textures.add(textureID);
        
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, width, height,
                0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, textureID, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        return textureID;
	}
	public int createDepthBufferAttachment(int frameBufferID, int width, int height) {
        int renderBufferID = createRBO();
        glBindRenderbuffer(GL_RENDERBUFFER, renderBufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width,height);
        
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBufferID);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        
        return renderBufferID;
	}
	
	public TextFont loadFont(String fontMetaFile, String textureAtlasFile) {
		return new TextFont(this, new MetaData(fontMetaFile), loadTexture(textureAtlasFile));
	}

	public ModelTexture loadTexture(String texture_file, float reflectivity, float shineDamper) {
		ModelTexture texture = new ModelTexture(loadTexture(texture_file));
		texture.setReflectivity(reflectivity);
		texture.setShineDamper(shineDamper);
		
		return texture;
	}
	public ModelTexture loadTexture(String texture_file, float reflectivity, float shineDamper, 
			boolean hasTransparency, boolean useFakeLighting) {
		
		ModelTexture texture = loadTexture(texture_file, reflectivity, shineDamper);
		texture.setHasTransparency(hasTransparency);
		texture.setUseFakeLighting(useFakeLighting);
		return texture;
	}
	public ModelTexture loadTexture(String texture_file, float reflectivity, float shineDamper,
			boolean hasTransparency, boolean useFakeLighting, int numberOfAtlasRows) {
		
		ModelTexture texture = loadTexture(texture_file, reflectivity, shineDamper, hasTransparency, useFakeLighting);
		texture.setAtlasNumberOfRows(numberOfAtlasRows);
		return texture;
	}
	
	public TerrainTexturePack loadTerrainTexturePack(String backgroundTexture, 
			String rTexture, String gTexture, String bTexture) {
		
		TerrainTexture bgTex = new TerrainTexture(loadTexture(backgroundTexture));
		TerrainTexture rTex = new TerrainTexture(loadTexture(rTexture));
		TerrainTexture gTex = new TerrainTexture(loadTexture(gTexture));
		TerrainTexture bTex = new TerrainTexture(loadTexture(bTexture));
		
		return new TerrainTexturePack(bgTex, rTex, gTex, bTex);
		//TODO przeniesc tworzenie Terenu tutaj do loadera? 
	}
	
	public Entity[] loadMaze(String maze_file, Terrain terrain) {

		//TODO dodac ewentualne zabezpieczenie przed renderowaniem z normalMapping bez zaladowania tangents!!!!!!!
		//TexturedModel wall_model = new TexturedModel(loadCuboid(1, 1, 2), loadTexture("bricks.png", 1.0f, 100));
		TexturedModel wall_model = new TexturedModel(loadModel("wall.obj"), loadTexture("bricks.png", 0.0f, 100));
		wall_model.getTexture().setNormalMap(loadTexture("normal_bricks.png"));
		TexturedModel tree_model = new TexturedModel(loadModel("tree.obj"), loadTexture("tree.png", 1.0f, 1000));
		TexturedModel grass_model = new TexturedModel(loadModel("grass.obj"), loadTexture("grass.png", 0.0f, 1000, true, true));
		
		char wall = '#';
		char tree = '^';
		char grass = '*';
		
        String line;
		FileReader fReader = null;
		BufferedReader bReader;
		List<List<Character>> cols = new ArrayList<List<Character>>();
		List<Character> row;
		List<Entity> entitiesList = new ArrayList<Entity>();
		
        try {
        	fReader = new FileReader(new File("./mazes/" + maze_file));
        } catch (FileNotFoundException e) {
        	Main.printlnLog("File '" + maze_file + "' not found in 'obj_files' folder");
        }
        
        bReader = new BufferedReader(fReader);
        try {
            while ((line = bReader.readLine()) != null) {
                row = new ArrayList<Character>();
                for(int i=0 ; i<line.length() ; i++) {
                	row.add(line.charAt(i));
                }
                cols.add(row);
            }
            bReader.close();
        } catch (IOException e) {
        	Main.printlnLog("Error reading the file");
		}
        
		Vector3f position;
        for(int i=0 ; i<cols.size() ; i++) {
            for(int j=0 ; j<cols.get(i).size() ; j++) {
            	position = new Vector3f((float)j, terrain.getHeightOfTerrain(j, i) - 0.5f, (float)i);
            	
            	if(cols.get(i).get(j) == wall) {
            		entitiesList.add(new Entity(wall_model, position, new Vector3f(0,0,0), new Vector3f(0.5f, 0.5f, 0.5f)));
            	} else if(cols.get(i).get(j) == tree) {
            		entitiesList.add(new Entity(tree_model, position, new Vector3f(0,0,0), 1f));
            	} else if(cols.get(i).get(j) == grass) {
            		entitiesList.add(new Entity(grass_model, position, new Vector3f(0,0,0), 0.2f));
            	}
            	
            }
        }
        
        Entity[] entitesArray = new Entity[entitiesList.size()];
        for(int i=0 ; i<entitiesList.size() ; i++) {
        	entitesArray[i] = entitiesList.get(i);
        }
        
        entitiesList.clear();
		return entitesArray;
	}
	public Entity[] random(float min_x, float max_x, float min_z, float max_z, int amount, TexturedModel model, float scale, float scaleVariance, Terrain terrain) {
		
		Entity random[] = new Entity[amount];
		
		float rand_x, rand_z, rand_scale;
		float dScale = scale * scaleVariance;
		float min_scale = scale - dScale;
		float max_scale = scale + dScale;
		int rows = model.getTexture().getNumberOfAtlasRows();
		int rand_index;
		
		for(int i=0 ; i<random.length ; i++) {
			rand_x = min_x + (float)Math.random() * (max_x - min_x);
			rand_z = min_z + (float)Math.random() * (max_z - min_z);
			rand_scale = min_scale + (float)Math.random() * (max_scale - min_scale);
			rand_index = (int)(Math.random() * rows*rows);
			random[i] = new Entity(model, new Vector3f(rand_x,terrain.getHeightOfTerrain(rand_x, rand_z) - 0.1f,rand_z), new Vector3f(0,0,0), rand_scale);
			random[i].setTextureAtlasIndex(rand_index);
		}
		
		return random;
	}
	
	public Particle randomParticle(ParticleTexture texture, Vector3f position) {
		float min = -5f;
		float max = 5f;
		
		float rand1 = (float) (min + Math.random() * (max - min));
		float rand2 = (float) (min + Math.random() * (max - min));
		float rand3 = (float) (min + Math.random() * (max - min));
		
		Particle particle = new Particle(texture, 
				new Vector3f(position.x + rand1 / 5, rand2 / 5 + position.y + 2, rand3/5 + position.z), 
				new Vector3f(rand1,rand2 + 10,rand3), 1f);
		
		particle.setScale((rand1 + rand2 + rand3)/10);
		return particle;
	}
	
	public void clean() {
		for(int VAO:VAOs) {
			glDeleteVertexArrays(VAO);
		}
		for(int VBO:VBOS) {
			glDeleteBuffers(VBO);
		}
		for(int FBO:FBOs) {
			glDeleteFramebuffers(FBO);
		}
		for(int RBO:RBOs) {
			glDeleteRenderbuffers(RBO);
		}
		for(int texture:textures) {
			glDeleteTextures(texture);
		}
	}

	public void updateVbo(int vboID, float[] vboData, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(vboData);
		buffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, buffer.capacity() * 4, GL_STREAM_DRAW);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	public void addInstancedAttribute(int vaoID, int vboID, int attr, int dataSize, int instancedDataLength, int offset) {
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBindVertexArray(vaoID);
		glVertexAttribPointer(attr, dataSize, GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		glVertexAttribDivisor(attr, 1);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}
	
	private void storeDataInAttrList(int attrNumber, int coordSize, float[] data) {
	//create vboID and add it to VBOs
	//GL_ARRAY_BUFFER << use that VBO for vertex attribute data
		int vboID = createVBO();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		
	//store attribute (VBO) in binded VAO
		glBufferData(GL_ARRAY_BUFFER, createFlippedFloatBuffer(data), GL_STATIC_DRAW);
		glVertexAttribPointer(attrNumber, coordSize, GL_FLOAT, false, 0, 0); //???
	}
	private void bindIndices(int[] indices) {
	//Create vboID and add it to VBOs
	//GL_ELEMENT_ARRAY_BUFFER << use that VBO for vertex attribute data
		int vboID = createVBO();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);

		glBufferData(GL_ELEMENT_ARRAY_BUFFER, createFlippedIntBuffer(indices), GL_STATIC_DRAW);
	}
	
	private static FloatBuffer createFlippedFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		return buffer;
	}
	private static IntBuffer createFlippedIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		
		return buffer;
	}	
}
