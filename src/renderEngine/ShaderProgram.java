package renderEngine;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public abstract class ShaderProgram {
	
	//model data in VAO >> [vertex shader] >> per vertex variables
	//per vertex variables >> [fragment shader] >> pixel color
	
	private int programID;  //shader program ID
	private int vsID;       //vertex shader
	private int fsID;       //fragment shader
	
	public ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
		
		this.programID = glCreateProgram();
		
		Main.printlnLog("");
		Main.printlnLog("Shader program ID: " + programID);
		Main.printlnLog("Vertex shader file: " + vertexShaderFile);
		Main.printlnLog("Fragment shader file: " + fragmentShaderFile);
		
		this.vsID = loadShaderFile(vertexShaderFile, GL_VERTEX_SHADER);
		this.fsID = loadShaderFile(fragmentShaderFile, GL_FRAGMENT_SHADER);
		
	//attach vertex shader and fragment shader to program
		glAttachShader(programID, vsID);
		glAttachShader(programID, fsID);
	
	//bind attributes
		bindAttributes();
		
	//link the program
		glLinkProgram(programID);
		if(glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
			Main.printlnLog("Program linking error:");
			Main.printLog(glGetProgramInfoLog(programID));
			return;
		} else {
			Main.printlnLog("Program linked");
		}
		
	//validate the program	
		glValidateProgram(programID);
		if(glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
			Main.printlnLog("Program validation error:");
			Main.printLog(glGetProgramInfoLog(programID));
			return;
		} else {
			Main.printlnLog("Program validated");
		}
		
	//get all uniform locations
		getUniformLocations();
		
		Main.printlnLog("");
	}
	private static int loadShaderFile(String shaderFile, int shader_type) {
	//convert text from file to String
		StringBuilder sbuilder = new StringBuilder();
		BufferedReader reader;
		String shaderSource;
		
		try {
			reader = new BufferedReader(new FileReader(new File(shaderFile)));
			String line;
			
			while((line = reader.readLine()) != null) {
				sbuilder.append(line + "\n");
			}
			
			reader.close();
		} catch(IOException e) {
		//convert exception to string and print
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Main.printLog(sw.toString());
		}
		
		shaderSource = sbuilder.toString();
	
	//create shader and set source
		int shaderID = glCreateShader(shader_type);
		glShaderSource(shaderID, shaderSource);
		
	//check shader compile status, return created shaderID
		glCompileShader(shaderID);
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			Main.printlnLog("'" + shaderFile + "' compilation error:");
			Main.printLog(glGetShaderInfoLog(shaderID));
			return 0;
		} else {
			Main.printlnLog("'" + shaderFile + "' compiled");
			return shaderID;
		}
	}

	protected void bindAttribute(int attribute, String VariableName) {
		glBindAttribLocation(programID, attribute, VariableName);
	}
	public void enable() {
		glUseProgram(programID);
	}
	public void disable() {
		glUseProgram(0);
	}
	public void clean() {
		disable();
		glDetachShader(programID, vsID);
		glDetachShader(programID, fsID);
		
		glDeleteShader(vsID);
		glDeleteShader(fsID);
		glDeleteShader(programID);
	}
	
	protected int getUniformLocation(String name) {
		int location = glGetUniformLocation(programID, name);	
		
		//BLAD ZWRACA ROWNIEZ WTEDY JEZELI ZMIENNA JEST NIEUZYWANA W SHADERZE!
		if(location == - 1) {
			Main.printlnLog("Err: glGetUniformLocation(\"" + name + "\") = -1");
		}

		return location;
	}
	protected void setUniform(int uniformLocation, int value) {
		glUniform1i(uniformLocation, value);
	}
	protected void setUniform(int uniformLocation, boolean value) {
	float val = 0.0f;
	if(value) {
		val = 1.0f;
	}
		glUniform1f(uniformLocation,val);
	}
	protected void setUniform(int uniformLocation, float value) {
		glUniform1f(uniformLocation, value);
	}
	protected void setUniform(int uniformLocation, Vector2f value) {
		glUniform2f(uniformLocation, value.x, value.y);
	}
	protected void setUniform(int uniformLocation, Vector3f value) {
		glUniform3f(uniformLocation, value.x, value.y, value.z);
	}
	protected void setUniform(int uniformLocation, Vector4f value) {
		glUniform4f(uniformLocation, value.x, value.y, value.z, value.w);
	}
	protected void setUniform(int uniformLocation, Matrix4f matrix) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrix.get(buffer);
		glUniformMatrix4fv(uniformLocation, false, buffer);
	}

	protected abstract void bindAttributes();
	protected abstract void getUniformLocations();
}
