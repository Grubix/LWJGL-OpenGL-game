package renderEngine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_CLIP_DISTANCE0;

import java.awt.datatransfer.FlavorTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.EntityRenderer;
import entities.EntityShader;
import entities.Light;
import entities.Player;
import entities.Sun;
import entities.nm.EntityShaderNM;
import guis.GuiText;
import guis.GuiTextRenderer;
import guis.GuiTextShader;
import guis.GuiTexture;
import guis.GuiTextureRenderer;
import guis.GuiTextureShader;
import models.TexturedModel;
import particles.Particle;
import particles.ParticleRenderer;
import particles.ParticleShader;
import particles.ParticleTexture;
import shadows.ShadowMapRenderer;
import shadows.ShadowShader;
import skybox.SkyboxRenderer;
import skybox.SkyboxShader;
import terrains.Terrain;
import terrains.TerrainRenderer;
import terrains.TerrainShader;
import terrains.TerrainTexture;
import terrains.TerrainTexturePack;
import text.Text;
import text.TextRenderer;
import text.TextShader;
import textMeshCreator.TextFont;
import tools.MousePicker;
import water.Water;
import water.WaterRenderer;
import water.WaterShader;

public class Scene {

	private static float R = 0.55f;
	private static float G = 0.62f;
	private static float B = 0.69f;
	private static final int MAX_LIGHTS = 4;
	
	private final Loader loader;
	private final Input input;
	private final MousePicker picker;
	private final Camera camera;
	private final Matrix4f projectionMatrix;
	private final Matrix4f viewMatrix;

	//TODO STWORZYC KLASE RENDERERS ZEBY ODCHUDZIC TA KLASE
	private final EntityShader entityShader = new EntityShader();
	private final EntityShaderNM entityShaderNM = new EntityShaderNM();
	private final TerrainShader terrainShader = new TerrainShader();
	private final SkyboxShader skyboxShader = new SkyboxShader();
	private final GuiTextureShader guiShader = new GuiTextureShader();
	private final GuiTextShader guiTextShader = new GuiTextShader();
	private final TextShader textShader = new TextShader();
	private final WaterShader waterShader = new WaterShader();
	private final ParticleShader particleShader = new ParticleShader();
	private final ShadowShader shadowShader = new ShadowShader();
	
	private final EntityRenderer entityRenderer;
	private final TerrainRenderer terrainRenderer;
	private final SkyboxRenderer skyboxRenderer;
	private final GuiTextureRenderer guiRenderer;
	private final GuiTextRenderer guiTextRenderer;
	private final TextRenderer textRenderer;
	private final WaterRenderer waterRenderer;
	private final ParticleRenderer particleRenderer;
	private final ShadowMapRenderer shadowMapRenderer;
	
	private Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TextFont, List<GuiText>> guiTexts = new HashMap<TextFont, List<GuiText>>();
	private Map<TextFont, List<Text>> texts = new HashMap<TextFont, List<Text>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<Light> lights = new ArrayList<Light>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	private List<Water> water = new ArrayList<Water>();
	
	private static Player player;
	private Sun sun;
	private Terrain terrain;
	
	private Entity barrel; //CHWILOWO
	private boolean isBarrelSelected; //CHWILOWO
	private final ParticleTexture texture; //CHWILOWO
	
	public Scene() {
		this.loader = new Loader();
		this.input = new Input(Main.getWindow());
		this.camera = new Camera(this, input);
		this.viewMatrix = camera.getViewMatrix();
		this.projectionMatrix = camera.getProjectionMatrix();
		this.picker = new MousePicker(projectionMatrix, viewMatrix, input);
		
		texture = new ParticleTexture(loader.loadTexture("exp.png"), 5);
		
		entityRenderer = new EntityRenderer(entityShader, entityShaderNM, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, skyboxShader, projectionMatrix);
		guiRenderer = new GuiTextureRenderer(loader, guiShader);
		guiTextRenderer = new GuiTextRenderer(guiTextShader);
		textRenderer = new TextRenderer(textShader, projectionMatrix);
		waterRenderer = new WaterRenderer(loader, waterShader, projectionMatrix);
		particleRenderer = new ParticleRenderer(loader, particleShader, projectionMatrix);
		shadowMapRenderer = new ShadowMapRenderer(loader, camera, shadowShader);
		
		prepareTerrain();
		prepareEntites();
	}
	private void prepareTerrain() {
		TerrainTexturePack terrainTexturePack = loader.loadTerrainTexturePack("grassSurf.png", "mud.png", "gravel.png", "bricks.png");
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("B_MAP.png"));
		terrain = new Terrain(0, 0, terrainTexturePack, blendMap, "heightMap.png", loader);
		processTerrain(terrain);
		
		Water water = new Water("water_dudvmap.png", "water_normalmap.png", new Vector3f(12, -1.2f, 39), loader);
		processWater(water);	
	}
	private void prepareEntites() {
		//CREATE MODELS
		//------------------------------------------------------------------------------------------------------------------------//
			TexturedModel player_model = new TexturedModel(loader.loadModel("cow.obj"), loader.loadTexture("cow2.png", 0.0f, 100));
			TexturedModel lamp_model = new TexturedModel(loader.loadModel("lamp.obj"), loader.loadTexture("lampAtlas.png", 1.0f, 10000, false, true, 2));
			TexturedModel tree1_model = new TexturedModel(loader.loadModel("trees/tree1.obj"), loader.loadTexture("tree1AtlasScaled.png", 0.0f, 100, false, false, 2));
			TexturedModel tree2_model = new TexturedModel(loader.loadModel("trees/tree2.obj"), loader.loadTexture("tree2.png", 0.0f, 100));
			TexturedModel tree3_model = new TexturedModel(loader.loadModel("trees/tree3.obj"), loader.loadTexture("tree3.png", 0.0f, 100));
			TexturedModel rock1_model = new TexturedModel(loader.loadModel("rocks/rock1.obj"), loader.loadTexture("rock1.png", 0.0f, 100));
			TexturedModel barrel_model = new TexturedModel(loader.loadModel("barrel.obj"), loader.loadTexture("barrel.png", 1.0f, 100));
			
		//PLAYER
		//------------------------------------------------------------------------------------------------------------------------//
			player = new Player(player_model, new Vector3f(12f,0f,39f), new Vector3f(0,180,0), 0.3f, input);
			camera.focusOn(player);	
			processEntity(player);
			
		//SUN
		//------------------------------------------------------------------------------------------------------------------------//	
			sun = new Sun(new Vector3f(0,10,0), new Vector3f(0.9f,0.7f,0.6f));
			processLight(sun);
			
		//WHITE LAMP	
		//------------------------------------------------------------------------------------------------------------------------//
			Entity lamp_white = new Entity(
					lamp_model, 
					new Vector3f(8,terrain.getHeightOfTerrain(8, 40),40), 
					new Vector3f(0,0,0), 
					0.1f);
			Light light_white = new Light(
					//new Vector3f(lamp_white.getPosition().x, lamp_white.getPosition().y + 1.7f, lamp_white.getPosition().z),
					player.getPosition(),
					new Vector3f(1,1,1),
					new Vector3f(1,0.01f,0.1f));
			lamp_white.setTextureAtlasIndex(0);
			
			processLight(light_white);
			processEntity(lamp_white);
		
		//RED LAMP
		//------------------------------------------------------------------------------------------------------------------------//
			Entity lamp_red = new Entity(
					lamp_model,
					new Vector3f(35,terrain.getHeightOfTerrain(35, 35),35),
					new Vector3f(0,0,0),
					0.1f);
			Light light_red = new Light(
					new Vector3f(lamp_red.getPosition().x, lamp_red.getPosition().y + 1.7f, lamp_red.getPosition().z),
					new Vector3f(1,0,0),
					new Vector3f(1,0.01f,0.1f));
			lamp_red.setTextureAtlasIndex(1);
			
			processLight(light_red);
			processEntity(lamp_red);
		
		//GREEN LAMP
		//------------------------------------------------------------------------------------------------------------------------//
			Entity lamp_green = new Entity(
					lamp_model,
					new Vector3f(25,terrain.getHeightOfTerrain(25, 35),35),
					new Vector3f(0,0,0),
					0.1f);
			Light light_green = new Light(
					new Vector3f(lamp_green.getPosition().x, lamp_green.getPosition().y + 1.7f, lamp_green.getPosition().z),
					new Vector3f(0,1,0), 
					new Vector3f(1,0.01f,0.1f));
			lamp_green.setTextureAtlasIndex(2);
			
			processLight(light_green);
			processEntity(lamp_green);
		
		//MAZE WALLS
		//------------------------------------------------------------------------------------------------------------------------//
			Entity maze[] = loader.loadMaze("maze.txt", terrain);
			
			for(Entity e: maze) {
				processEntity(e);
			}
		
		//RANDOM ENTITIES
		//------------------------------------------------------------------------------------------------------------------------//
			Entity trees1[] = loader.random(0, 50, 0, 50, 50, tree1_model, 0.5f, 0.3f, terrain);
			Entity trees2[] = loader.random(0, 50, 0, 50, 50, tree2_model, 0.5f, 0.3f, terrain);
			Entity trees3[] = loader.random(0, 50, 0, 50, 50, tree3_model, 0.5f, 0.3f, terrain);
			Entity rocks1[] = loader.random(0, 50, 0, 50, 50, rock1_model, 0.5f, 0.5f, terrain);
		
			for(Entity e: trees1) {
				processEntity(e);
			}
			
			for(Entity e: trees2) {
				processEntity(e);
			}
			
			for(Entity e: trees3) {
				processEntity(e);
			}
			
			for(Entity e: rocks1) {
				processEntity(e);
			}
			
			
			barrel = new Entity(barrel_model, new Vector3f(8,terrain.getHeightOfTerrain(8, 37) + 1f,37), new Vector3f(0,0,0), 1);
			barrel.setScale(0.1f);
			barrel_model.getTexture().setNormalMap(loader.loadTexture("barrel_normal.png"));
			barrel.enableNormalMapping();
			processEntity(barrel);
	}
	
	public void update() {
		camera.move(terrain);
		player.move(terrain);
		picker.update();
		sun.update();
		
		//TODO: WSZYSTKO PONIZEJ JEST TYMCZASOWE !!!!!!!
		barrel.increaseRotation(0, 0.1f, 0);
		if(input.mouse(0)) {
			Vector3f pickerRay = new Vector3f(picker.getRay());
			Vector3f origin = new Vector3f(camera.getPosition());
			Vector3f entityCenter = new Vector3f(barrel.getPosition());
			Vector3f sub = origin.sub(entityCenter);
			float r = 0.7f;
			
			float b = pickerRay.dot(sub);
			float c = sub.dot(sub) - r*r;

			if(b*b - c >= 0) {
				isBarrelSelected = true;
				barrel.disableNormalMapping();
			}
		}	
		
		if(input.keyboard(GLFW_KEY_P)) {
			isBarrelSelected = false;
			barrel.enableNormalMapping();
		}
		
		if(isBarrelSelected) {
			if(input.keyboard(GLFW_KEY_UP)) {
				barrel.increasePosition(0.03f, 0, 0);
			}
			if(input.keyboard(GLFW_KEY_DOWN)) {
				barrel.increasePosition(-0.03f, 0, 0);		
			}
			if(input.keyboard(GLFW_KEY_LEFT)) {
				barrel.increasePosition(0, 0, -0.03f);		
			}
			if(input.keyboard(GLFW_KEY_RIGHT)) {
				barrel.increasePosition(0, 0, 0.03f);		
			}
		}
		
		if(input.keyboard(GLFW_KEY_Y)) {
			float randX = -3 + (float)Math.random() * (3 + 3);
			float randY = 0 + (float)Math.random() * (10 - 0);
			float randZ = -3 + (float)Math.random() * (3 + 3);
			processParticle(new Particle(texture, new Vector3f(player.getPosition().x, player.getPosition().y, player.getPosition().z), new Vector3f(randX, randY, randZ), 1));
		}
		
	}
	public void prepare() {
		glClearColor(R, G, B, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	public void updateShaders() {
		entityShader.enable();
		entityShader.setProjectionMatrix(projectionMatrix);    
		
		entityShaderNM.enable();
		entityShaderNM.setProjectionMatrix(projectionMatrix);   
		
		particleShader.enable();
		particleShader.setProjectionMatrix(projectionMatrix); 
		
		skyboxShader.enable();
		skyboxShader.setProjectionMatrix(projectionMatrix);
		
		terrainShader.enable();
		terrainShader.setProjectionMatrix(projectionMatrix);

		textShader.enable();
		textShader.setProjectionMatrix(projectionMatrix);
		
		waterShader.enable();
		waterShader.setProjectionMatrix(projectionMatrix);
		
		glUseProgram(0);
	}
	
	public void renderEntites() {
		entityShader.enable();                                   //enable static shader
		entityShader.setLights(lights);                          //set uniform variables
		entityShader.setSkyColor(R,G,B);                         //set uniform variables
		entityShader.setViewMatrix(viewMatrix);                      //set uniform variables
		entityShader.disable();                                  //disable static shader
		
		entityShaderNM.enable();                                 //enable static shader
		entityShaderNM.setViewMatrix(viewMatrix);                //set uniform variables
		entityShaderNM.setLights(lights, viewMatrix);            //set uniform variables
		entityShaderNM.setSkyColor(R,G,B);                       //set uniform variables
		entityShaderNM.disable();    
		
		entityRenderer.renderEntities(entities, camera);               //render all processed entities
	}
	public void renderTerrains() {
		terrainShader.enable();                                  //enable this shader
		terrainShader.setLights(lights);                         //set uniform variables
		terrainShader.setSkyColor(R,G,B);                        //set uniform variables
		terrainShader.setViewMatrix(viewMatrix);                     //set uniform variables
		terrainRenderer.renderTerrains(terrains);                //render all processed terrains
		terrainShader.disable();                                 //disable terrain shader
	}
	public void renderSkybox() {
		skyboxShader.enable();            
		skyboxShader.setViewMatrix(viewMatrix);
		skyboxShader.setFogColor(R, G, B);
		skyboxRenderer.renderSkybox();
		skyboxShader.disable();	
	}
	public void renderGuis() {
		guiShader.enable();                                      //enable gui shader
		guiRenderer.renderGuis(guis);                            //render all processed guis
		guiShader.disable();                                     //disable gui shader
	}
	public void renderGuiTexts() {
		guiTextShader.enable();                                  //enable gui text shader
		guiTextRenderer.renderGuiTexts(guiTexts);                //render all processed gui texts
		guiTextShader.disable();                                 //disable gui text shader	
	}
	public void renderTexts() {
		textShader.enable();
		textShader.setViewMatrix(viewMatrix);                     
		textRenderer.renderTexts(texts);
		textShader.disable();
	}
	public void renderWater() {
        updateWater();
        waterShader.enable();
        waterShader.setViewMatrix(viewMatrix);
        waterShader.setCameraPosition(camera.getPosition());
        waterRenderer.renderWaters(water, sun);
        waterShader.disable();
	}
	public void updateWater() {
		glEnable(GL_CLIP_DISTANCE0);

		for (Water waterTile : water) {	
			Vector4f clipPlane;
    		float distance = 2 * (camera.getPosition().y - waterTile.getYPos());
    	
    	//render to reflection buffer
    		waterTile.getFBOs().bindReflectionFrameBuffer();
    		
    		camera.getPosition().y -= distance;
    		camera.getRotation().x = -camera.getRotation().x;
    		camera.updateViewMatrix();
    		
    		clipPlane = new Vector4f(0, 1, 0, -waterTile.getYPos() + 0.01f);
    	
    		terrainShader.enable();
    		terrainShader.setClipPlane(clipPlane);
 
    		entityShader.enable();
    		entityShader.setClipPlane(clipPlane);
    		
    		entityShaderNM.enable();
    		entityShaderNM.setClipPlane(clipPlane);
    		
    		prepare();
    		renderSkybox();
    		renderTerrains();
    		renderEntites();
    		
    	//render to refraction buffer
    		waterTile.getFBOs().bindRefractionFrameBuffer();
    		
    		camera.getPosition().y += distance;
    		camera.getRotation().x = -camera.getRotation().x;
    		camera.updateViewMatrix();

    		clipPlane = new Vector4f(0, -1, 0, waterTile.getYPos() + 0.01f);
        	
    		terrainShader.enable();
    		terrainShader.setClipPlane(clipPlane);
 
    		entityShader.enable();
    		entityShader.setClipPlane(clipPlane);
    		
    		entityShaderNM.enable();
    		entityShaderNM.setClipPlane(clipPlane);

    		prepare();
    		renderTerrains();
    		renderEntites();
    		
    	//bind default frame buffer (render to screen)
    		waterTile.getFBOs().unbindFrameBuffer();	
        }
        
		glDisable(GL_CLIP_DISTANCE0);
	}
	public void renderParticles() {
		particleShader.enable();
		particleRenderer.renderParticles(particles, camera);
		particleShader.disable();
		updateParticles();
	}
	public void updateParticles() {
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while(mapIterator.hasNext()) {
			List<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator = list.iterator();
			
			while(iterator.hasNext()) {
				Particle particle = iterator.next();
				if(!particle.update(camera)) {
					iterator.remove();
					if(list.isEmpty()) {
						mapIterator.remove();
					}
				}
			}
			
		}
	}
	public void renderShadowMap() {
		shadowMapRenderer.render(entities, new Light(new Vector3f(10000,15000,-10000), new Vector3f(1,1,1)));
	}
	public void renderAll() {	
		renderShadowMap();
		renderSkybox();
		renderTerrains();
		renderEntites();
		renderWater();
		renderTexts();
		renderParticles();
		renderGuis();
		renderGuiTexts();
	}
	
	public void processEntity(Entity entity) {
		TexturedModel texturedModel = entity.getTexturedModel();
		
		List<Entity> batch = entities.get(texturedModel);
		if(batch == null) {
			batch = new ArrayList<Entity>();	
			entities.put(texturedModel, batch);
		}
		batch.add(entity);
	}
	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	public void processLight(Light light) {
		if(lights.size() < MAX_LIGHTS) {
			lights.add(light);
		} else {
			Main.printlnLog("Error: Max number of lights: " + MAX_LIGHTS);
		}
	}
	public void processWater(Water waterTile) {
		water.add(waterTile);
	}
	public void processGui(GuiTexture gui) {
		guis.add(gui);
	}
	public void processGuiText(GuiText guiText) {
		TextFont fontType = guiText.getFontType();
		guiText.createModel();
		
		List<GuiText> batch = guiTexts.get(fontType);
		if(batch == null) {
			batch = new ArrayList<GuiText>();
			guiTexts.put(fontType, batch);
		} 
		batch.add(guiText);
	}
	public void processText(Text text) {
		TextFont fontType = text.getFontType();
		text.createModel();
		
		List<Text> batch = texts.get(fontType);
		if(batch == null) {
			batch = new ArrayList<Text>();
			texts.put(fontType, batch);
		} 
		batch.add(text);
	}
	public void processParticle(Particle particle) {
		ParticleTexture texture = particle.getTexture();
		
		List<Particle> batch = particles.get(texture);
		if(batch == null) {
			batch = new ArrayList<Particle>();	
			particles.put(texture, batch);
		}
		batch.add(particle);
	}
		
	public void removeGuiText(GuiText guiText) {
		
		if(guiText == null) {
			return;
		}
		
		List<GuiText> batch = guiTexts.get(guiText.getFontType());
		
		if(batch == null) {
			return;
		}
		
		batch.remove(guiText);
		if(batch.isEmpty()) {
			guiTexts.remove(guiText.getFontType());
		}
		
	}
	public void removeText(Text text) {
		if(text == null) {
			return;
		}
		
		List<Text> batch = texts.get(text.getFontType());
		
		if(batch == null) {
			return;
		}
		
		batch.remove(text);
		if(batch.isEmpty()) {
			guiTexts.remove(text.getFontType());
		}
	}
		
	public static void enableCulling() {
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}
	public static void disableCulling() {
		glDisable(GL_CULL_FACE);
	}
	
	public static void enableBlending(int sFactor, int dFactor) {
		glEnable(GL_BLEND);
		glBlendFunc(sFactor, dFactor);
	}
	public static void disableBlending() {
		glDisable(GL_BLEND);	
	}
	
	public static void enableDepthMask() {
		glDepthMask(true);
	}
	public static void disableDepthMask() {
		glDepthMask(false);
	}
	
	public static void enableDepthTest() {
		glEnable(GL_DEPTH_TEST);
	}
	public static void disableDepthTest() {
		glDisable(GL_DEPTH_TEST);
	}
	
	public static Player getPlayer() {
		return player;
	}
	
	private void clearData() {
		entities.clear();
		terrains.clear();
		lights.clear();
		guis.clear();
		guiTexts.clear();
		texts.clear();
		water.clear();
		particles.clear();
	}
	public void clean() {
		clearData();
		loader.clean();
		
		entityShader.clean();
		entityShaderNM.clean();
		terrainShader.clean();
		guiShader.clean();
		guiTextShader.clean();
		textShader.clean();
		skyboxShader.clean();
		waterShader.clean();
	}

}
