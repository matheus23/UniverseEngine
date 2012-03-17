package org.universeengine.opengl.model.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.universeengine.util.UniPrint;

public abstract class UniMeshRenderer {
	
	protected boolean deleted = false;
	
	public abstract void create();
	public abstract void render(int mode);
	public abstract void delete();
	
	protected FloatBuffer arrayToBuffer(float[] array) {
		return (BufferUtils.createFloatBuffer(array.length).put(array, 0, array.length));
	}

	protected IntBuffer arrayToBuffer(int[] array) {
		return (BufferUtils.createIntBuffer(array.length).put(array, 0, array.length));
	}
	
	public void finalize() {
		delete();
		UniPrint.printf("delete() invoked from finalize()\n");
	}

}
