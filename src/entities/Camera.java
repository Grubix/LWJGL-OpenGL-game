package entities;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import renderEngine.Input;
import renderEngine.Main;
import renderEngine.Scene;
import terrains.Terrain;
import tools.Maths;

public class Camera {

	private final Input input;
	private final Scene scene;

	private final Vector4f frustumNormals[] = new Vector4f[6];
	private final Matrix4f viewMatrix = new Matrix4f();
	private final Matrix4f projectionMatrix = new Matrix4f();
	
	private float fov = 90;
	private float nearPlane = 0.1f;
	private float farPlane = 1000;
	
	private Vector3f position = new Vector3f(0f,0f,0f);
	private Vector3f rotation = new Vector3f(0f,0f,0f); //pitch | yaw | roll
	
	private Entity entity;

	private final float maxDinstanceFromEntity = 15.0f;
	private final float minDinstanceFromEntity = 2.0f;
	private float distanceFromEntity = 3.0f;
	
	private final float minPitch = -45;
	private final float maxPitch = 90;
	private float rotAroundEntity = 0.0f;

	private final float springiness = 8.0f;
	private float frameTime;
	private float damper;
	
	private float dScroll;
	private float dXPos;
	private float dYPos;
	private float oldXPos;
	private float oldYPos;
	
	private static enum CameraMode {
		FIRST_PERSON_MODE,
		THIRD_PERSON_MODE,
		FREE_MODE
	};
	private CameraMode mode = CameraMode.THIRD_PERSON_MODE;
	
	public Camera(Scene scene, Input input) {
		this.input = input;
		this.scene = scene;
		
		for(int i=0 ; i<frustumNormals.length ; i++) {
			frustumNormals[i] = new Vector4f();
		}
		
		updateViewMatrix();
		updateProjectionMatrix();
	}
	public void move(Terrain terrain) {
		checkInputs();
		
        switch (mode) {
        	case FIRST_PERSON_MODE: 
    			Vector3f pos = entity.getPosition();
    			position.x = pos.x;
    			position.y = pos.y + 1f;
    			position.z = pos.z;
        		
    			rotation.y += damper * (dXPos);
    			
    			float new_pitch = rotation.x + damper * (dYPos);
    			if(new_pitch < maxPitch && new_pitch > minPitch) {
    				rotation.x = new_pitch;
    			}
        		break;
        	case THIRD_PERSON_MODE: 
        		calcZoom();
        		calcAngles();
        		calcCameraPosition(calcHorizontalDistance(), calcVerticalDistance(), terrain);
        		break;
        	case FREE_MODE: 
        		//TODO 
        		break;
        }
        
        updateViewMatrix();
        updateFrustumNormals();
	}
	public void focusOn(Entity entity) {
		this.entity = entity;
	}
	
	public void updateViewMatrix() {
		viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1,0,0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(0,1,0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(rotation.z), new Vector3f(0,0,1), viewMatrix);
        viewMatrix.translate(new Vector3f(position).negate());
	}
	public void updateProjectionMatrix() {
		float aspectRatio = Main.getWindowWidth() / Main.getWindowHeight();
		float frustumLength = farPlane - nearPlane;
		float yScale = ((float)(1.0f / Math.tan(Math.toRadians(fov / 2.0f))) * aspectRatio);
		float xScale = yScale / aspectRatio;
		
		projectionMatrix.m00(xScale);
		projectionMatrix.m11(yScale);
		projectionMatrix.m22(-((farPlane + nearPlane) / frustumLength));
		projectionMatrix.m23(-1.0f);
		projectionMatrix.m32(-((2 * farPlane * nearPlane) / frustumLength));
		projectionMatrix.m33(0.0f);
	}
	public void updateFrustumNormals() {
		Matrix4f transVM = new Matrix4f(viewMatrix).transpose();
		Matrix4f transPM = new Matrix4f(projectionMatrix).transpose();
		Matrix4f VPM = transVM.mul(transPM);
		
		//LEFT PLANE
		frustumNormals[0].set(
				VPM.m30() + VPM.m00(),
				VPM.m31() + VPM.m01(),
				VPM.m32() + VPM.m02(),
				VPM.m33() + VPM.m03());
		frustumNormals[0].normalize();
		
		//RIGHT PLANE
		frustumNormals[1].set(
				VPM.m30() - VPM.m00(),
				VPM.m31() - VPM.m01(),
				VPM.m32() - VPM.m02(),
				VPM.m33() - VPM.m03());
		frustumNormals[1].normalize();
		
		//BOTTOM PLANE
		frustumNormals[2].set(
				VPM.m30() + VPM.m10(),
				VPM.m31() + VPM.m11(),
				VPM.m32() + VPM.m12(),
				VPM.m33() + VPM.m13());
		frustumNormals[2].normalize();
		
		//TOP PLANE
		frustumNormals[3].set(
				VPM.m30() - VPM.m10(),
				VPM.m31() - VPM.m11(),
				VPM.m32() - VPM.m12(),
				VPM.m33() - VPM.m13());
		frustumNormals[3].normalize();
		
		//NEAR PLANE
		frustumNormals[4].set(
				VPM.m30() + VPM.m20(),
				VPM.m31() + VPM.m21(),
				VPM.m32() + VPM.m22(),
				VPM.m33() + VPM.m23());
		frustumNormals[4].normalize();
		
		//FAR PLANE
		frustumNormals[5].set(
				VPM.m30() - VPM.m20(),
				VPM.m31() - VPM.m21(),
				VPM.m32() - VPM.m22(),
				VPM.m33() - VPM.m23());
		frustumNormals[5].normalize();
	}

	public void setFov(float fov) {
		this.fov = fov;
		updateProjectionMatrix();
		scene.updateShaders();
	}
	public void setNearPlane(float nearPlane) {
		this.nearPlane = nearPlane;
		updateProjectionMatrix();
		scene.updateShaders();
	}
	public void setFarPlane(float farPlane) {
		this.farPlane = farPlane;
		updateProjectionMatrix();
		scene.updateShaders();
	}
	
	public void setPosition(float xPos, float yPos, float zPos) {
		this.rotation.set(xPos, yPos, zPos);
		//updateViewMatrix();
	}
	public void setPositionX(float xPos) {
		this.position.x = xPos;
		//updateViewMatrix();
	}
	public void setPositionY(float yPos) {
		this.position.y = yPos;
		//updateViewMatrix();
	}
	public void setPositionZ(float zPos) {
		this.position.z = zPos;
		//updateViewMatrix();
	}
	public void increasePosition(float dxPos, float dyPos, float dzPos) {
		this.position.x += dxPos;
		this.position.y += dyPos;
		this.position.z += dzPos;
		//updateViewMatrix();
	}
	
	public void setRotation(float pitch, float yaw, float roll) {
		this.rotation.set(pitch, yaw, roll);
		//updateViewMatrix();
	}
	public void setPitch(float pitch) {
		this.rotation.x = pitch;
		//updateViewMatrix();
	}
	public void setYaw(float yaw) {
		this.rotation.y = yaw;
		//updateViewMatrix();
	}
	public void setRoll(float roll) {
		this.rotation.z = roll;
		//updateViewMatrix();
	}
	public void increaseRotation(float dxRot, float dyRot, float dzRot) {
		this.rotation.x += dxRot;
		this.rotation.y += dyRot;
		this.rotation.z += dzRot;
		//updateViewMatrix();
	}
	
	public float getFov() {
		return this.fov;
	}
	public float getNearPlane() {
		return this.nearPlane;
	}
	public float getFarPlane() {
		return this.farPlane;
	}
	public Vector3f getPosition() {
		return position;
	}
	public Vector3f getRotation() {
		return rotation;
	}
	public Matrix4f getViewMatrix() {
		return this.viewMatrix;
	}
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}
	public Vector4f getFrustumNormal(int plane) {
		return this.frustumNormals[plane];
	}
	
	private void calcZoom() {
		if(distanceFromEntity + dScroll > minDinstanceFromEntity &&
				distanceFromEntity + dScroll < maxDinstanceFromEntity) {
			distanceFromEntity += dScroll * 0.2f;
		}
	}
	private void calcAngles() {	
		if(input.mouse(GLFW_MOUSE_BUTTON_2)) {
			rotAroundEntity -= damper * (dXPos);
			
			if(rotAroundEntity > 360) { 
				rotAroundEntity -= 360; 
			} else if(rotAroundEntity < -360) { 
				rotAroundEntity += 360;
			}
			
			float new_pitch = rotation.x + damper * (dYPos);
			if(new_pitch < maxPitch && new_pitch > minPitch) {
				rotation.x = new_pitch;
			}
		}
	}
	private float calcHorizontalDistance() {
		return distanceFromEntity * (float)Math.cos(Math.toRadians(rotation.x));
	}
	private float calcVerticalDistance() {
		return distanceFromEntity * (float)Math.sin(Math.toRadians(rotation.x));
	}
	private void calcCameraPosition(float horizontalDistance, float verticalDistance, Terrain terrain) {
		float theta = entity.getRotation().y + rotAroundEntity;
		rotation.y = 180 - theta;
		
		float x = (float) horizontalDistance * (float) Math.sin(Math.toRadians(theta));
		float z = (float) horizontalDistance * (float) Math.cos(Math.toRadians(theta));
		
		Vector3f entityPos = entity.getPosition();
		position.x = entityPos.x - x;
		position.z = entityPos.z - z;
		position.y = entityPos.y + verticalDistance + 1.2f;
		
//		float height = terrain.getHeightOfTerrain(position.x, position.z);
//		if(position.y < height + 1) {
//			position.y = height + 1f;
//		}
	}
	private void checkInputs() {
		frameTime = Main.getFrameTime();     
		damper = (float)(1 - Math.exp(Math.log(0.5) * springiness * frameTime));
		
		dScroll = input.getdScroll();
		
		dXPos = input.getCursorXPos() - oldXPos;
		dYPos = input.getCursorYPos() - oldYPos;
		oldXPos = input.getCursorXPos();
		oldYPos = input.getCursorYPos();
		
		if(input.keyboard(GLFW_KEY_F1)) {
			mode = CameraMode.FIRST_PERSON_MODE;
		}if(input.keyboard(GLFW_KEY_F2)) {
			mode = CameraMode.THIRD_PERSON_MODE;
		}if(input.keyboard(GLFW_KEY_F3)) {
			mode = CameraMode.FREE_MODE;
		}
	}

}

//TODO zrobic wkoncu fajne chodzenie dla 1ST mode:
//if(glfwGetKey(windowHandle, GLFW_KEY_W) == GL_TRUE) {
//if(glfwGetKey(windowHandle, GLFW_KEY_SPACE) == GL_TRUE) {
//	position.x += (float) (2 * distance * Math.sin(Math.toRadians(yaw)));
//	position.z -= (float) (2 * distance * Math.cos(Math.toRadians(yaw)));
//} else {
//	position.x += (float) (distance * Math.sin(Math.toRadians(yaw)));
//	position.z -= (float) (distance * Math.cos(Math.toRadians(yaw)));
//}
//}
//if(glfwGetKey(windowHandle, GLFW_KEY_S) == GL_TRUE) {
//position.x -= (float) (distance * Math.sin(Math.toRadians(yaw)));
//position.z += (float) (distance * Math.cos(Math.toRadians(yaw)));
//}
//if(glfwGetKey(windowHandle, GLFW_KEY_A) == GL_TRUE) {
//position.x -= (float) (distance * 0.5f * Math.cos(Math.toRadians(yaw)));
//position.z -= (float) (distance * 0.5f * Math.sin(Math.toRadians(yaw)));
//}
//if(glfwGetKey(windowHandle, GLFW_KEY_D) == GL_TRUE) {
//position.x += (float) (distance * 0.5f *Math.cos(Math.toRadians(yaw)));
//position.z += (float) (distance * 0.5f * Math.sin(Math.toRadians(yaw)));
//}
