package test;

import org.lwjgl.opengl.GL11;
import org.universeengine.mains.UniverseEngineShaderTest;
import org.universeengine.mains.shadertests.UniShaderTestUniform;
import org.universeengine.opengl.shader.UniShader;
import org.universeengine.opengl.shader.UniShaderProgram;

public class ShaderTest implements UniShaderTestUniform {
	
	private ShaderTest() {
	}

	public void init(UniShaderProgram prog, UniShader vert, UniShader frag) {
	}

	public void uniformAction() {
	}
	
	public static void main(String[] args) {
		UniverseEngineShaderTest.start("res/OrangeCharacter.obj", GL11.GL_QUADS, new ShaderTest());
	}

}
