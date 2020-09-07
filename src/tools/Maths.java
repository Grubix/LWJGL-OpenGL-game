package tools;

import javax.swing.text.View;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import entities.Camera;
import renderEngine.Main;

public class Maths {

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f transformationMatrix = new Matrix4f();
		transformationMatrix.identity();
		transformationMatrix.translate(new Vector3f(translation, 0.0f));
		transformationMatrix.scale(new Vector3f(scale.x, scale.y, 1f));
		return transformationMatrix;
	}
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
	
		Matrix4f transformationMatrix = new Matrix4f();
		transformationMatrix.identity();
		transformationMatrix.translate(translation);
		
		transformationMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1,0,0), transformationMatrix);
		transformationMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1,0), transformationMatrix);
		transformationMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1), transformationMatrix);
		
		transformationMatrix.scale(scale);
		
		return transformationMatrix;
	}
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {
		return createTransformationMatrix(translation, rotation, new Vector3f(scale, scale, scale));
	}
	public static Matrix4f createProjectionMatrix(float FOV, float NEAR_PLANE, float FAR_PLANE) {

		float aspectRatio = (float)Main.getWindowWidth() / (float)Main.getWindowHeight();
		float y_scale = ((float)(1.0f / Math.tan(Math.toRadians(FOV / 2.0f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1.0f);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0.0f);
		
		return projectionMatrix;
	}
	public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f().identity();
        
        Vector3f cameraRotation = camera.getRotation();
        viewMatrix.rotate((float) Math.toRadians(cameraRotation.x), new Vector3f(1,0,0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(cameraRotation.y), new Vector3f(0,1,0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(cameraRotation.z), new Vector3f(0,0,1), viewMatrix);
        
        Vector3f negCameraPos = new Vector3f(camera.getPosition()).negate();
        viewMatrix.translate(negCameraPos);
        
        return viewMatrix;	
	}
	public static void updateViewMatrix(Camera camera) {
		Matrix4f viewMatrix = camera.getViewMatrix();
		
		viewMatrix = new Matrix4f().identity();
        
        Vector3f cameraRotation = camera.getRotation();
        viewMatrix.rotate((float) Math.toRadians(cameraRotation.x), new Vector3f(1,0,0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(cameraRotation.y), new Vector3f(0,1,0), viewMatrix);
        viewMatrix.rotate((float) Math.toRadians(cameraRotation.z), new Vector3f(0,0,1), viewMatrix);
        
        Vector3f negCameraPos = new Vector3f(camera.getPosition()).negate();
        viewMatrix.translate(negCameraPos);
		
	}
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
//		return (p1.y + p2.y + p3.y) / 3;
	}
}
