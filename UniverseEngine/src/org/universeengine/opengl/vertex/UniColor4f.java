package org.universeengine.opengl.vertex;

import static org.lwjgl.opengl.GL11.*;

import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniColor4f implements UniVertex, UniPrintable {
	
	public static final int BANDWIDTH = 4;
	public static final int TYPE = FLOAT;
	
	private float[] col;
	
	public UniColor4f(float r, float g, float b, float a) {
		set(r, g, b, a);
	}
	
	public UniColor4f(float[] col) {
		if (col.length != getBandwidth()) {
			UniPrint.printerrf(this, "Color4f has wrong length: %d\n" +
					" >> Has to be %d\n", col.length, BANDWIDTH);
			return;
		}
		this.col = col;
	}
	
	public void set(float r, float g, float b, float a) {
		if (col == null) col = new float[getBandwidth()];
		col[0] = r;
		col[1] = g;
		col[2] = b;
		col[3] = a;
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
		glColor4f(col[0], col[1], col[2], col[3]);
	}
	
	public String toString() {
		return String.format("Color4f: [%G, %G, %G, %G]", col[0], col[1], col[2], col[3]);
	}

}
