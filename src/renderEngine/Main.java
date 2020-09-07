package renderEngine;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;

import org.lwjgl.opengl.GL;

import tools.SyncTimer;

public class Main {

//Window
	private static final String W_TITLE = "Projekt OpenGL";    //window title
	private static final int W_WIDTH = 1300;           //window width
	private static final int W_HEIGHT = 650;           //window height
	private static long windowHandle;                  //the window handle
	
//Console
	private static final int C_WIDTH = 1300;           //console width
	private static final int C_HEIGHT = 200;           //console height
	private static final float C_R = 36.0f / 255.0f;   //red
	private static final float C_G = 36.0f / 255.0f;   //green
	private static final float C_B = 36.0f / 255.0f;   //blue
	private static JFrame console_frame;               //console frame handle
	private static JTextArea console;                  //console text area handle
	
//Display
	private static Scene scene;                        //dac jakis opis
	private static SyncTimer timer;                    //dac jakis opis
	private static final float FPS = 60;               //frames per second
	private static long lastFrameTime;                 //dac jakis opis
	private static float frameTime;                    //dac jakis opis

	public Main() {
		init();            //initialize
		loop();            //start main loop
		glfwTerminate();   //close window
		closeConsole();    //close console
	}

	private void init() {
	//create console, set default lookAndFeel
		setLookAndFeel();
		createConsole();
		
	//initialize GLFW
		if(!glfwInit()) {
			printlnLog("GLFW ERROR: Unable to initialize GLFW");
			glfwTerminate();
			return;
		} else {
			printlnLog("GLFW initialized properly");
		}
		
	//create window
		windowHandle = glfwCreateWindow(W_WIDTH, W_HEIGHT, "Window", 0, 0);
		if(windowHandle == 0) {
			printlnLog("GLFW ERROR: Failed to create window");
			glfwTerminate();
			return;
		} else {
			printlnLog("Window created properly");
		}
	
	//set window parameters
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);                            //set window visible
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);                           //set window resizable
		glfwSetWindowPos(windowHandle, 100, 100);                            //set window position
		glfwSetWindowTitle(windowHandle, W_TITLE);
		//glfwSetInputMode(windowHandle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);   //set cursor mode
		glfwSetCursorPos(windowHandle, W_WIDTH/2, W_HEIGHT/2);               //set cursor position
		glfwMakeContextCurrent(windowHandle);                                //????TODO
		glfwShowWindow(windowHandle);                                        //show window
		GL.createCapabilities();                                             //????TODO
		
	//OpenGL test
		printlnLog("\n\nOpenGL TEST");
		printlnLog("OS name: " + System.getProperty("os.name"));
		printlnLog("OS version: " + System.getProperty("os.version"));
		printlnLog("OpenGL version: " + glGetString(GL_VERSION));
		printlnLog("OpenGL vendor: " + glGetString(GL_VENDOR));
		printlnLog("OpenGL renderer: " + glGetString(GL_RENDERER));
		printlnLog("");
	
	//set fields
		timer = new SyncTimer(SyncTimer.LWJGL_GLFW);
		scene = new Scene();
	}
	private void loop() {			
		while(!glfwWindowShouldClose(windowHandle)) {	
			lastFrameTime = getCurrentTime();
			
			scene.update();
			scene.prepare();
			scene.renderAll();
			
			glfwSwapBuffers(windowHandle);
			glfwPollEvents();
			timerSync();
			calcFrameTime();	
		}	
		scene.clean();	
	}
	private void createConsole() {
		console_frame = new JFrame("Console");
		console_frame.setSize(C_WIDTH + 16, C_HEIGHT);
		console_frame.setLocation(100 - 8, 100 + W_HEIGHT + 3);
		console_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(false);
		
		console = new JTextArea();
		console.setAutoscrolls(true);
		console.setEditable(false);
		console.setForeground(Color.WHITE);
		console.setBackground(new Color(C_R, C_G, C_B));
		console.setFont(new Font("Verdana", Font.PLAIN, 12));
		
		DefaultCaret caret = (DefaultCaret)console.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane scrollPane = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(C_R, C_G, C_B), 5));
		
		console_frame.getContentPane().add(scrollPane);
		console_frame.setVisible(true);
	}
	private void closeConsole() {
		console_frame.setVisible(false);
		console_frame.dispose();
	}
	private long getCurrentTime() {
		return System.currentTimeMillis();
	}
	private void calcFrameTime() {
		frameTime = (getCurrentTime() - lastFrameTime) / 1000f;
	}
	private void timerSync() {
		try { 
			timer.sync(FPS); 
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	public static void printLog(String str) {
		if(console != null) {
			console.append(str);	
		} else {
			System.out.print("Err: Console pointer is null");
		}
	}
	public static void printlnLog(String str) {
		printLog(str + "\n");
	}
	
	public static int getWindowWidth() {
		return W_WIDTH;
	}
	public static int getWindowHeight() {
		return W_HEIGHT;
	}
	public static long getWindow() {
		return windowHandle;
	}
	public static JTextArea getConsole() {
		return console;
	}
	public static float getFrameTime() {
		return frameTime;
	}
	public static float getFPS() {
		return FPS;
	}
	
	private static void setLookAndFeel() {
	//set OS LookAndFeel()
		try {
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			printLog("Err: Unsupported LookAndFeel");
			return;
		}
		catch (ClassNotFoundException e) {
			printLog("Err: LookAndFeel class not found");
			return;
		}
		catch (InstantiationException e) {
			printLog("Err: LookAndFeel instantiation failed");
			return;
		}
		catch (IllegalAccessException e) {
			printLog("Err: LookAndFeel illegal access");
			return;
		}	
	}
	public static void main(String[] args) {
		new Main();
	}
}
