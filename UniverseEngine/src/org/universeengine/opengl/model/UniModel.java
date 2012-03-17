package org.universeengine.opengl.model;

public class UniModel {
	
	private UniMesh[] meshes;
	
	public UniModel(UniMesh[] meshes) {
		this.meshes = meshes;
	}
	
	public void render(int mode) {
		for (int i = 0; i < meshes.length; i++) {
			meshes[i].render(mode);
		}
	}
	
	public void destroy() {
		for (int i = 0; i < meshes.length; i++) {
			meshes[i].destroy();
		}
	}
	
	public void finalize() {
		destroy();
	}

}
