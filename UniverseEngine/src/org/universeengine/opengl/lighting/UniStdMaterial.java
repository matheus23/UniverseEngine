package org.universeengine.opengl.lighting;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.universeengine.util.UniPrintable;

public class UniStdMaterial implements UniPrintable {
	
	private FloatBuffer ambient;
	private FloatBuffer diffuse;
	private FloatBuffer specular;
	private float shininess;
	
	public UniStdMaterial() {
		ambient = BufferUtils.createFloatBuffer(4);
		diffuse = BufferUtils.createFloatBuffer(4);
		specular = BufferUtils.createFloatBuffer(4);
	}
	
	public void setAmbient(float r, float g, float b, float a) {
		ambient.rewind();
		ambient.put(r).put(g).put(b).put(a).rewind();
	}
	
	public void setDiffuse(float r, float g, float b, float a) {
		diffuse.rewind();
		diffuse.put(r).put(g).put(b).put(a).rewind();
	}
	
	public void setSpecular(float r, float g, float b, float a) {
		specular.rewind();
		specular.put(r).put(g).put(b).put(a).rewind();
	}
	
	public void setShininess(float val) {
		this.shininess = val;
	}
	
	public void bind() {
		glMaterial(GL_FRONT, GL_AMBIENT, ambient);
		glMaterial(GL_FRONT, GL_DIFFUSE, diffuse);
		glMaterial(GL_FRONT, GL_SPECULAR, specular);
		glMaterialf(GL_FRONT, GL_SHININESS, shininess);
	}

	public String getClassName() {
		return getClass().getSimpleName();
	}

}
