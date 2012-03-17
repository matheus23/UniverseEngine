package org.universeengine.opengl.vertex;

import static org.lwjgl.opengl.GL11.*;

import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniVertex3f implements UniVertex, UniPrintable {
	
	public static final int BANDWIDTH = 3;
	public static final int TYPE = FLOAT;
	
	private float[] vert;
	
	public UniVertex3f(float x, float y, float z) {
		set(x, y, z);
	}
	
	public UniVertex3f(float[] vert) {
		if (vert.length != getBandwidth()) {
			UniPrint.printerrf(this, "Vertex3f has wrong length: %d\n" +
					" >> Has to be %d\n", vert.length, BANDWIDTH);
			return;
		}
		this.vert = vert;
	}
	
	public void set(float x, float y, float z) {
		if (vert == null) vert = new float[getBandwidth()];
		vert[0] = x;
		vert[1] = y;
		vert[2] = z;
	}
	
	public float[] get() {
		return vert;
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
		glVertex3f(vert[0], vert[1], vert[2]);
	}
	
	public String toString() {
		return String.format("Vertex3f: [%G, %G, %G]", vert[0], vert[1], vert[2]);
	}

}
