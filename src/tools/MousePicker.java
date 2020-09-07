package tools;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import renderEngine.Input;
import renderEngine.Main;

public class MousePicker {

	private final Input input;
	private final float windowWidth;
	private final float windowHeight;
	private final float cursorPosPrecision = 100f;
	
	private final Vector3f ray;
	private final Matrix4f projectionMatrix;
	private final Matrix4f viewMatrix;
	
	public MousePicker(Matrix4f projectionMatrix, Matrix4f viewMatrix, Input input) {
		this.input = input;
		this.windowWidth = Main.getWindowWidth();
		this.windowHeight = Main.getWindowHeight();
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = viewMatrix;

		this.ray = new Vector3f();
	}
	public Vector3f getRay() {
		return this.ray;
	}
	
	public void update() {
		float cursorXPos = input.getCursorXPos();
		float cursorYPos = input.getCursorYPos();
		Vector2f normalizedCursorCoords= calcNormalizedCursorCoords(cursorXPos, cursorYPos);
		Vector4f clipCoords = new Vector4f(
				normalizedCursorCoords.x,
				normalizedCursorCoords.y,
				-1f,
				1f);
		Vector4f toEyeCoords = calcToEyeCoords(clipCoords);
		Vector3f worldCoords = calcToWorldCoords(toEyeCoords);
		
		worldCoords.x = round(worldCoords.x);
		worldCoords.y = round(worldCoords.y);
		worldCoords.z = round(worldCoords.z);
		
		this.ray.set(worldCoords.x, worldCoords.y, worldCoords.z);
	}
	private Vector2f calcNormalizedCursorCoords(float cursorXPos, float cursorYPos) {
		float x = round(2 * (float)cursorXPos / windowWidth - 1);
		float y = round(1 - 2 * (float)cursorYPos / windowHeight);
		
		return new Vector2f(x, y);
	}
	private Vector4f calcToEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjectionMatrix = new Matrix4f(projectionMatrix).invert();
		Vector4f toEyeCoords = invertedProjectionMatrix.transform(clipCoords);
		
		return new Vector4f(toEyeCoords.x, toEyeCoords.y, -1f, 0f);
	}
	private Vector3f calcToWorldCoords(Vector4f toEyeCoords) {
		Matrix4f invertedViewMatrix = new Matrix4f(viewMatrix).invert();
		Vector4f rayWorld = invertedViewMatrix.transform(toEyeCoords);
		Vector3f mouseRay = new Vector3f(rayWorld.x ,rayWorld.y, rayWorld.z);
		
		return mouseRay.normalize();
	}
	private float round(float value) {
		return Math.round(value * cursorPosPrecision) / cursorPosPrecision;
	}
	
}
