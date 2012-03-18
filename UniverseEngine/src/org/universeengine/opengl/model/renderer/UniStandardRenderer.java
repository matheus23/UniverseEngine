package org.universeengine.opengl.model.renderer;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

import org.universeengine.opengl.vertex.UniElement;

public class UniStandardRenderer extends UniMeshRenderer {

	private UniElement[] vertices;
	private UniElement[] normals;
	private UniElement[] colors;
	private UniElement[] texCoords;

	public UniStandardRenderer(UniElement[] vertices, UniElement[] normals, UniElement[] colors, UniElement[] texCoords) {
		this.vertices = vertices;
		this.normals = normals;
		this.colors = colors;
		this.texCoords = texCoords;
	}

	public void create() {
	}
	
	public void renderSolid() {
		if (vertices != null) {
			if (normals != null) {
				if (colors != null) {
					if (texCoords != null) {
						renderAll();
					} else {
						renderVertAndNormAndColors();
					}
				} else {
					if (texCoords != null) {
						renderVertAndNormAndTex();
					} else {
						renderVertAndNorm();
					}
				}
			} else {
				if (colors != null) {
					if (texCoords != null) {
						renderVertAndTex();
					} else {
						renderVertAndColors();
					}
				} else {
					if (texCoords != null) {
						renderVertAndColorsAndTex();
					} else {
						renderVert();
					}
				}
			}
		}
	}
	
	public void render(int mode) {
		glBegin(mode);
		{
			renderSolid();
		}
		glEnd();
	}

	private void renderVert() {
		for (int i = 0; i < vertices.length; i++) {
			vertices[i].render();
		}
	}

	private void renderAll() {
		for (int i = 0; i < vertices.length; i++) {
			texCoords[i].render();
			colors[i].render();
			normals[i].render();
			vertices[i].render();
		}
	}

	private void renderVertAndNorm() {
		for (int i = 0; i < vertices.length; i++) {
			normals[i].render();
			vertices[i].render();
		}
	}

	private void renderVertAndTex() {
		for (int i = 0; i < vertices.length; i++) {
			texCoords[i].render();
			vertices[i].render();
		}
	}
	
	private void renderVertAndNormAndColors() {
		for (int i = 0; i < vertices.length; i++) {
			colors[i].render();
			normals[i].render();
			vertices[i].render();
		}
	}
	
	public void renderVertAndNormAndTex() {
		for (int i = 0; i < vertices.length; i++) {
			texCoords[i].render();
			normals[i].render();
			vertices[i].render();
		}
	}
	
	public void renderVertAndColorsAndTex() {
		for (int i = 0; i < vertices.length; i++) {
			texCoords[i].render();
			colors[i].render();
			vertices[i].render();
		}
	}
	
	public void renderVertAndColors() {
		for (int i = 0; i < vertices.length; i++) {
			colors[i].render();
			vertices[i].render();
		}
	}
	
	public void delete() {
	}
	
}
