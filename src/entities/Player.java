package entities;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector3f;

import models.TexturedModel;
import renderEngine.Input;
import renderEngine.Main;
import terrains.Terrain;

public class Player extends Entity{

	private final Input input;
	
	private static final float WALK_SPEED = 2;     //units per second
	private static final float RUN_SPEED = 5;      //units per second
	private static final float TURN_SPEED = 160;   //degrees per second
	private static final float GRAVITY = 9.81f;
	private static final float JUMP_SPEED = 5f;
	
	public static final float checkRadius = 0.7f;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float verticalSpeed = 0;
	private float oldTerrainHeight = 0;
	
	private boolean playerIsInAir = false;
	
	public Player(TexturedModel texturedModel, Vector3f position, Vector3f rotation, Vector3f scale, Input input) {
		super(texturedModel, position, rotation, scale);
		this.input = input;
	}
	public Player(TexturedModel texturedModel, Vector3f position, Vector3f rotation, float scale, Input input) {
		this(texturedModel, position, rotation, new Vector3f(scale, scale, scale), input);
	}

	public void move(Terrain terrain) {
		checkInputs();
		float frameTime = (float)Math.round(Main.getFrameTime() * 100) / 100;
		//float frameTime = 1 / Main.getFPS();
		float angle = currentTurnSpeed * frameTime;
		float distance = currentSpeed * frameTime;
		float dX = (float) (distance * Math.sin(Math.toRadians(super.getRotation().y)));
		float dZ = (float) (distance * Math.cos(Math.toRadians(super.getRotation().y)));
		
		if(!playerIsInAir) {
			super.increaseRotation(0, angle, 0);
		}
		super.increasePosition(dX, 0, dZ);
		verticalSpeed -= GRAVITY * frameTime;
		super.increasePosition(0, verticalSpeed * frameTime / 2, 0);
		
		float terrainHeight = lerp(oldTerrainHeight, terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z), 0.1f);

		if(super.getPosition().y< terrainHeight) {
			verticalSpeed = 0;
			playerIsInAir = false;
			super.getPosition().y = terrainHeight;
		}
		
		oldTerrainHeight = terrainHeight;
	}
	private void jump() {
		if(!playerIsInAir) {
			verticalSpeed = JUMP_SPEED;
			playerIsInAir = true;
		}
	}
	private void checkInputs() {
		if(!playerIsInAir) {
			
			currentSpeed = 0;
			currentTurnSpeed = 0;
			
			if(input.keyboard(GLFW_KEY_W)) {
				if(input.keyboard(GLFW_KEY_LEFT_SHIFT)) {
					currentSpeed = RUN_SPEED;
				} else {
					currentSpeed = WALK_SPEED;
				}
			} 
			if(input.keyboard(GLFW_KEY_S)) {
				currentSpeed = -WALK_SPEED;
			} 
			if(input.keyboard(GLFW_KEY_A)) {
				currentTurnSpeed = TURN_SPEED;
			} 
			if(input.keyboard(GLFW_KEY_D)) {
				currentTurnSpeed = -TURN_SPEED;
			}
			if(input.keyboard(GLFW_KEY_SPACE)) {
				jump();
			}
		}
	}
	
	private float lerp(float a, float b, float f) {
	    return a + f * (b - a);
	}
	
}
