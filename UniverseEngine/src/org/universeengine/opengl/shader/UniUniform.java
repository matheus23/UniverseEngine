package org.universeengine.opengl.shader;

import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH;
import static org.lwjgl.opengl.GL20.glGetProgram;

import org.lwjgl.opengl.ARBShaderObjects;

public class UniUniform {
	
	private int location;
	private String name;
	
	/**
	 * Creates a new Uniform "wrapper", used to
	 * manage Uniforms for a specific Shader.
	 * 
	 * @param name the variable's name of the Uniform,
	 * specified in the Shader's source.
	 * @param program the UniShaderProgram to change
	 * the uniforms from.
	 */
	public UniUniform(String name, UniShaderProgram program) {
		this.name = name;
		
		program.use();
		int shaderProgram = program.getAdress();
		program.unuse();
		
		location = ARBShaderObjects.glGetUniformLocationARB(shaderProgram, name);
		
		if (location == -1) {
			System.err.printf("Uniform could not be found\n" +
					"Shader %d, Uniform %s\n", shaderProgram, name);
		}
		System.out.printf("Uniform \"%s\" loaded into shader %d\n" +
				"Uniform location: %d\n" +
				"Active Uniforms: %d\n" +
				"Active Uniform length: %d\n", name, shaderProgram, 
				location,  glGetProgram(shaderProgram, GL_ACTIVE_UNIFORMS), 
				glGetProgram(shaderProgram, GL_ACTIVE_UNIFORM_MAX_LENGTH));
	}
	
	/**
	 * @return the name, given in the Constructor.
	 */
	public String getName() {
		return name;
	}
	
	public void uniform1f(float f1) {
		ARBShaderObjects.glUniform1fARB(location, f1);
	}

	public void uniform1i(int i1) {
		ARBShaderObjects.glUniform1iARB(location, i1);
	}
	
	public void uniform2f(float f1, float f2) {
		ARBShaderObjects.glUniform2fARB(location, f1, f2);
	}
	
	public void uniform2i(int i1, int i2) {
		ARBShaderObjects.glUniform2iARB(location, i1, i2);
	}
	
	public void uniform3f(float f1, float f2, float f3) {
		ARBShaderObjects.glUniform3fARB(location, f1, f2, f3);
	}
	
	public void uniform3i(int i1, int i2, int i3) {
		ARBShaderObjects.glUniform3iARB(location, i1, i2, i3);
	}
	
	public void uniform4f(float f1, float f2, float f3, float f4) {
		ARBShaderObjects.glUniform4fARB(location, f1, f2, f3, f4);
	}
	
	public void uniform4i(int i1, int i2, int i3, int i4) {
		ARBShaderObjects.glUniform4iARB(location, i1, i2, i3, i4);
	}
	
}
