package org.universeengine.opengl.vertex;

import static org.lwjgl.opengl.GL11.*;

import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniColor3f implements UniElement, UniPrintable {
	
	public static final int BANDWIDTH = 3;
	public static final int TYPE = FLOAT;
	
	private float[] col;
	
	public UniColor3f(float r, float g, float b) {
		set(r, g, b);
	}
	
	public UniColor3f(float[] col) {
		if (col.length != getBandwidth()) {
			UniPrint.printerrf(this, "Color4f has wrong length: %d\n" +
					" >> Has to be %d\n", col.length, BANDWIDTH);
			return;
		}
		this.col = col;
	}
	
	public void set(float r, float g, float b) {
		if (col == null) col = new float[getBandwidth()];
		col[0] = r;
		col[1] = g;
		col[2] = b;
	}
	
	public float[] get() {
		return col;
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
		glColor3f(col[0], col[1], col[2]);
	}
	
	public String toString() {
		return String.format("Color3f: [%G, %G, %G]", col[0], col[1], col[2]);
	}

}
