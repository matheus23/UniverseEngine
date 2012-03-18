package org.universeengine.opengl.vertex;

import static org.lwjgl.opengl.GL11.*;

import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniNormal3f implements UniElement, UniPrintable {
	
	public static final int BANDWIDTH = 3;
	public static final int TYPE = FLOAT;
	
	private float[] norm;
	
	public UniNormal3f(float nx, float ny, float nz) {
		set(nx, ny, nz);
	}
	
	public UniNormal3f(float[] norm) {
		if (norm.length != getBandwidth()) {
			UniPrint.printerrf(this, "Normal3f has wrong length: %d\n" +
					" >> Has to be %d\n", norm.length, BANDWIDTH);
			return;
		}
		this.norm = norm;
	}
	
	public void set(float nx, float ny, float nz) {
		if (norm == null) norm = new float[getBandwidth()];
		norm[0] = nx;
		norm[1] = ny;
		norm[2] = nz;
	}
	
	public float[] get() {
		return norm;
	}

	public String getClassName() {
		return getClass().getSimpleName();
	}
	
	public int getBandwidth() {
		return BANDWIDTH;
	}

	public int getType() {
		return TYPE;
	}

	public void render() {
		glNormal3f(norm[0], norm[1], norm[2]);
	}
	
	public String toString() {
		return String.format("Normal3f: [%G, %G, %G]", norm[0], norm[1], norm[2]);
	}

}
