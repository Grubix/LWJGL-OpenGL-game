package renderEngine;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

public class Input {
	
	private final long windowHandle;
	
	private boolean keyboard[] = new boolean[350];
	private boolean mouse[] = new boolean[8];
	
	private float dScroll = 0.0f;
	private float cursorXPos = 0.0f;
	private float cursorYPos = 0.0f;
	
	public Input(long windowHandle) {
		this.windowHandle = windowHandle;
		
		GLFW.glfwSetKeyCallback(this.windowHandle, new GLFWKeyCallback() {
			@Override
			public void invoke(long win, int key, int scancode, int action, int mods) {
				if(key != -1) {
					if(action == GLFW_PRESS) {
						keyboard[key] = true;
					} else if(action == GLFW_RELEASE) {
						keyboard[key] = false;
					}		
				} else {
					Main.printlnLog("GLFW Error: unknown key");
				}
			}
		});
		GLFW.glfwSetScrollCallback(this.windowHandle, new GLFWScrollCallback() {
		    @Override public void invoke (long win, double dx, double dy) {
		        dScroll = (float) dy;
		    }
		});
		GLFW.glfwSetCursorPosCallback(this.windowHandle, new GLFWCursorPosCallback() {
			@Override
			public void invoke(long win, double XPos, double YPos) {
				cursorXPos = (float) XPos;
				cursorYPos = (float) YPos;
			}
			
		});
		GLFW.glfwSetMouseButtonCallback(this.windowHandle, new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long win, int button, int action, int mods) {
				if(action == GLFW_PRESS) {
					mouse[button] = true;
				} else if(action == GLFW_RELEASE) {
					mouse[button] = false;
				}
			}
		});
	}
	
	public float getdScroll() {
		return dScroll;
	}
	public float getCursorXPos() {
		return cursorXPos;
	}
	public float getCursorYPos() {
		return cursorYPos;
	}
	
	public boolean keyboard(int key) {
		return keyboard[key];
	}
	public boolean mouse(int button) {
		return mouse[button];
	}

}
