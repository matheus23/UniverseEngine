package org.universeengine.opengl.shader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniShader implements UniPrintable {
	
	public static final int VERTEX_SHADER = 0;
	public static final int FRAGMENT_SHADER = 1;

	private int pointerAdress;
	private String source = "";
	private int type;
	private String shaderDesc;

	/**
	 * Creates a new Shader, which will automatically be 
	 * "registered" in OpenGL.
	 * 
	 * @param filename the Filepath of the shader's source.
	 * @param shadertype either VERTEX_SHADER or FRAGMENT_SHADER
	 */
	public UniShader(String filename, int shadertype) {
		type = shadertype;
		if (type == VERTEX_SHADER) {
			shaderDesc = "Vertex-Shader";
		} else if (type == FRAGMENT_SHADER) {
			shaderDesc = "Fragment-Shader";
		} else {
			UniPrint.printerrf(this, "Unknown Shader type: %d\n", type);
			return;
		}
		if (type == VERTEX_SHADER) {
			pointerAdress = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
		} else if (type == FRAGMENT_SHADER) {
			pointerAdress = ARBShaderObjects.glCreateShaderObjectARB(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		}
		if (pointerAdress == 0) {
			UniPrint.printerrf(this, "Could not allocate Memory for %s\n", shaderDesc);
			return;
		}
		String line;
		StringBuilder strSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while((line = reader.readLine()) != null) {
				strSource.append(line);
				strSource.append("\n");
			}
			source = strSource.toString();
		} catch(Exception e) {
			UniPrint.printerrf(this, "Error while reading %s from %s\n", shaderDesc, filename);
			e.printStackTrace();
		}
		ARBShaderObjects.glShaderSourceARB(pointerAdress, source);
		ARBShaderObjects.glCompileShaderARB(pointerAdress);
		UniPrint.printoutf(this, "Printing Info Log for %s (id: %d):\n", shaderDesc, pointerAdress);
		if (printLogInfo(pointerAdress, shaderDesc)) {
			pointerAdress = 0;
			UniPrint.printerrf(this, "Error after Compiling %s, from \"%s\"!\n", shaderDesc, filename);
			return;
		}
	}
	
	/**
	 * Prints the Info Log from OpenGL for the given
	 * Shader (pointer).
	 * 
	 * @param shader pointer to the CREATED OpenGL shader.
	 * @param name (Optional: only for printing).
	 * @return whether there was an Error or not.
	 */
	public static boolean printLogInfo(int shader, String name) {
		IntBuffer infoLength = BufferUtils.createIntBuffer(1);
		ARBShaderObjects.glGetObjectParameterARB(shader, 
				ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB, infoLength);
		int length = infoLength.get();
		if (length > 1) {
			ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
			infoLength.flip();
			ARBShaderObjects.glGetInfoLogARB(shader, infoLength, infoLog);
			byte[] infoBytes = new byte[length];
			infoLog.get(infoBytes);
			String output = new String(infoBytes);
			UniPrint.printf("PrintInfoLog:\n >> Info Log for %s:\n%s", name, output);
			UniPrint.printf("End of InfoLog\n");
		} else {
			UniPrint.printf("InfoLog does not exist.\n");
			return false;
		}
		return true;
	}
	
	public String getClassName() {
		return getClass().getSimpleName();
	}
	
	/**
	 * @return the ShaderID from OpenGL
	 */
	public int getShaderPointer() {
		return pointerAdress;
	}
	
	/**
	 * @return whether the Shader can be used, 
	 * or there was an Error while loading.
	 */
	public boolean works() {
		return pointerAdress != 0;
	}
	
	/**
	 * @return either VERTEX_SHADER or FRAGMENT_SHADER
	 */
	public int getShaderType() {
		return type;
	}
	
	/**
	 * @return either "Fragment-Shader" or "Vertex-Shader"
	 */
	public String getShaderDescription() {
		return shaderDesc;
	}

	/**
	 * @return the complete loaded Source code,
	 * from the File, given in the Constructor
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Prints getSource()
	 */
	public void printSource() {
		System.out.printf("%s\n", source);
	}

}
