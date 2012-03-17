package org.universeengine.opengl.shader;

import org.lwjgl.opengl.ARBShaderObjects;
import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

import static org.lwjgl.opengl.GL11.*;

public class UniShaderProgram implements UniPrintable {
	
	public UniShader vertexShader;
	public UniShader fragmentShader;
	private int pointerAdress;
	private boolean works;
	
	/**
	 * Creates a new ShaderProgram. Either vertexShader or fragmentShader
	 * can be null, and then the default OpenGL Shaders will be used,
	 * but they can't both be null!
	 * 
	 * @param vertexShader the created UniShader with 
	 * getShaderType() == VERTEX_SHADER.
	 * @param fragmentShader the created UniShader with
	 * getShaderType() == FRAGMENT_SHADER. 
	 */
	public UniShaderProgram(UniShader vertexShader, UniShader fragmentShader) {
		works = false;
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
		boolean leaveVertexShader = vertexShader == null;
		boolean leaveFragmentShader = fragmentShader == null;
		if (leaveVertexShader && leaveFragmentShader) {
			UniPrint.printerrf(this, "Cannot create ShaderProgram without Shaders.\n" +
					"Both Shaders are Null!\n");
		}
		pointerAdress = ARBShaderObjects.glCreateProgramObjectARB();
		if (pointerAdress == 0) {
			UniPrint.printerrf(this, "Could not allocate ShaderObject!\n");
			return;
		}
		if (!(leaveVertexShader ? true : vertexShader.works()) 
				| !(leaveFragmentShader ? true : fragmentShader.works())) {
			UniPrint.printerrf(this, "One of the given Shaders does not work!\n" +
					"Printing the InfoLog:\n");
			UniShader.printLogInfo(pointerAdress, getClassName());
			return;
		}
		if (!leaveVertexShader) ARBShaderObjects.glAttachObjectARB(pointerAdress, vertexShader.getShaderPointer());
		if (!leaveFragmentShader) ARBShaderObjects.glAttachObjectARB(pointerAdress, fragmentShader.getShaderPointer());
		ARBShaderObjects.glLinkProgramARB(pointerAdress);
		ARBShaderObjects.glValidateProgramARB(pointerAdress);
		System.out.printf("ShaderProgram:\n >> Printing Info Log:\n");
		if (UniShader.printLogInfo(pointerAdress, getClassName())) {
			UniPrint.printerrf(this, "Error after linking and validating Program\n" +
					"VertexShader Source:\n%s\n" +
					"FragmentShader Source:\n%s\n", 
					vertexShader.getSource(), fragmentShader.getSource());
			return;
		}
		works = true;
	}
	
	/**
	 * @return the ID, returned from OpenGL during creation.
	 */
	public int getAdress() {
		return pointerAdress;
	}
	
	public String getClassName() {
		return getClass().getSimpleName();
	}
	
	/**
	 * @return whether both UniShaders work.
	 */
	public boolean works() {
		return works;
	}

	/**
	 * This is Depricated!
	 * Use
	 * 	use();
	 * 	glBegin(mode);
	 * instead!
	 */
	@Deprecated
	public void begin(int mode) {
		use();
		glBegin(mode);
	}
	
	/**
	 * This is Depricated!
	 * Use
	 * 	glEnd();
	 * 	unuse();
	 * instead!
	 */
	@Deprecated
	public void end() {
		glEnd();
		unuse();
	}
	
	/**
	 * Binds the Program's ID to OpenGL.
	 * After that you have to call glBegin()
	 * to unbind, see unuse()
	 */
	public void use() {
		if (works) {
			ARBShaderObjects.glUseProgramObjectARB(pointerAdress);
		} else {
			UniPrint.printerrf(this, "Cannot use a non-working ShaderProgram!\n");
		}
	}
	
	/**
	 * Detaches the Program from OpenGL.
	 * Standard OpenGL Shaders will be used from here,
	 * until use() is called.
	 */
	public void unuse() {
		if (works) {
			ARBShaderObjects.glUseProgramObjectARB(0);
		}
	}

}
