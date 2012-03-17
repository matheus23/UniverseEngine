package org.universeengine.util.render;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;

import org.universeengine.opengl.model.UniMesh;

public class UniDisplayList {

	private UniMesh mesh;
	private int index;
	private int mode;
	private boolean deleted;

	public UniDisplayList(UniMesh mesh, int mode) {
		this.mesh = mesh;
		this.mode = mode;
		deleted = true;
	}

	public void create() {
		index = glGenLists(1);
		glNewList(index, GL_COMPILE);
		mesh.render(mode);
		glEndList();
		deleted = false;
	}

	public void render() {
		glCallList(index);
	}
	
	public void delete() {
		if (!deleted) {
			glDeleteLists(index, 1);
			deleted = true;
		}
	}

}
