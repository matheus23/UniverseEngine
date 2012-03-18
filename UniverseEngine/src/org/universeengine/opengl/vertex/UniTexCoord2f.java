package org.universeengine.opengl.vertex;

import static org.lwjgl.opengl.GL11.*;

import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniTexCoord2f implements UniElement, UniPrintable {
	
	public static final int BANDWIDTH = 2;
	public static final int TYPE = FLOAT;
	
	private float[] tex;
	
	public UniTexCoord2f(float u, float v) {
		set(u, v);
	}
	
	public UniTexCoord2f(float[] tex) {
		if (tex.length != getBandwidth()) {
			UniPrint.printerrf(this, "TexCoord2f has wrong length: %d\n" +
					" >> Has to be %d\n", tex.length, BANDWIDTH);
			return;
		}
		this.tex = tex;
	}
	
	public void set(float u, float v) {
		if (tex == null) tex = new float[getBandwidth()];
		tex[0] = u;
		tex[1] = v;
	}
	
	public float[] get() {
		return tex;
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
		glTexCoord2f(tex[0], tex[1]);
	}
	
	public String toString() {
		return String.format("TexCoord2f: [%G, %G]", tex[0], tex[1]);
	}

}
