package org.universeengine.mains.shadertests;

import org.universeengine.opengl.shader.UniShader;
import org.universeengine.opengl.shader.UniShaderProgram;

public interface UniShaderTestUniform {
	
	public void init(UniShaderProgram prog, UniShader vert, UniShader frag);
	
	public void uniformAction();

}
