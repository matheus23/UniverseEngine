package org.universeengine.opengl.model.renderer;

import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;

import java.nio.FloatBuffer;

import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniVertexArrayRenderer extends UniMeshRenderer implements UniPrintable {
	
	private FloatBuffer vert;
	private FloatBuffer norm;
	private FloatBuffer tex;
	
	private boolean enableVert;
	private boolean enableNorm;
	private boolean enableTex;
	
	public UniVertexArrayRenderer(boolean enableVert, boolean enableNorm, boolean enableTex) {
		this.enableVert = enableVert;
		this.enableNorm = enableNorm;
		this.enableTex = enableTex;
		if (!enableVert) {
			UniPrint.printerrf(this, "Its not possible to use an\nVertex-Array without Vertices enabled!");
		}
	}

	public void create() {
		//TODO: Recreate this whole thing :/
//		if (enableVert) {
//			float[] vertArray = UniModelUtil.createModelArray(UniModelUtil.VERTEX_ARRAY, vertices);
//			vert = arrayToBuffer(vertArray);
//		} if (enableNorm) {
//			float[] normArray = UniModelUtil.createModelArray(UniModelUtil.NORMAL_ARRAY, vertices);
//			norm = arrayToBuffer(normArray);
//		} if (enableTex) {
//			float[] texArray = UniModelUtil.createModelArray(UniModelUtil.TEXCOORD_ARRAY, vertices);
//			tex = arrayToBuffer(texArray);
//		}
	}

	public void render(int mode) {
		int length = 0;
		glColor4f(1f, 1f, 1f, 1f);
		glEnableClientState(GL_VERTEX_ARRAY);
		
		if (enableVert) {
			glVertexPointer(3, 0, vert);
			length += vert.capacity();
		} if (enableNorm) {
			glNormalPointer(0, norm);
			length += norm.capacity();
		} if (enableTex) {
			glTexCoordPointer(2, 0, tex);
			length += tex.capacity();
		}
		
		glDrawArrays(mode, 0, length);
		glDisableClientState(GL_VERTEX_ARRAY);
	}
	
	public String getClassName() {
		return getClass().getSimpleName();
	}
	
	public void printVertexData() {
		UniPrint.printoutf(this, "Vertex-Array:\n");
		int ind = 0;
		for (int i = 0; i < vert.capacity()/3; i++) {
			ind = i*3;
			System.out.printf(" >> v %G %G %G\n", vert.get(ind), vert.get(ind+1), vert.get(ind+2));
		}
	}
	
	public void delete() {
	}

}
