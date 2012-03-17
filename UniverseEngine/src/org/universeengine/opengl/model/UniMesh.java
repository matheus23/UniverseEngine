package org.universeengine.opengl.model;

import static org.lwjgl.opengl.GL11.*;

import org.universeengine.opengl.model.renderer.UniMeshRenderer;

public class UniMesh {
	
	private UniMeshRenderer renderer;
	private float tx;
	private float ty;
	private float tz;
	private float rx;
	private float ry;
	private float rz;
	
	public UniMesh(UniMeshRenderer renderer) {
		this.renderer = renderer;
	}
	
	public void render(int mode) {
		glTranslatef(tx, ty, tz);
		glPushMatrix();
		{
			glRotatef(rx, 1f, 0f, 0f);
			glRotatef(ry, 0f, 1f, 0f);
			glRotatef(rz, 0f, 0f, 1f);
		}
		glPopMatrix();
		renderer.render(mode);
	}
	
	public void destroy() {
		renderer.delete();
	}
	
	public void finalize() {
		destroy();
	}
	
}
