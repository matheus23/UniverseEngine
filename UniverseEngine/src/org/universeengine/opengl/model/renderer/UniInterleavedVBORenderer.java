package org.universeengine.opengl.model.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glDrawRangeElements;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;
import org.universeengine.exceptions.UniGLVersionException;
import org.universeengine.opengl.vertex.UniElement;
import org.universeengine.util.UniPrint;

public class UniInterleavedVBORenderer extends UniMeshRenderer {

	private int vboID;
	private int indID;

	private UniElement[] vertices;
	private UniElement[] normals;
	private UniElement[] colors;
	private UniElement[] texCoords;
	private int[] indices;
	
	private int offV;
	private int offN;
	private int offC;
	private int offT;
	
	private int sizeV;
	private int sizeN;
	private int sizeC;
	private int sizeT;

	private int size;
	private int stride;
	
	private boolean print = true;

	public UniInterleavedVBORenderer(UniElement[] vertices, UniElement[] normals,
			UniElement[] colors, UniElement[] texCoords, int[] indices)
			throws UniGLVersionException {
		if (!GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
			throw new UniGLVersionException(
					"VertexBufferObjectsARB is not supported on this GPU");
		}
		this.vertices = vertices;
		this.normals = normals;
		this.colors = colors;
		this.texCoords = texCoords;
		this.indices = indices;
	}
	
	public void setPrint(boolean print) {
		this.print = print;
	}

	public void create() {
		boolean save = UniPrint.enabled;
		UniPrint.enabled = print;
		sizeV = vertices != null ? vertices[0].getBandwidth() : 0;
		sizeN = normals != null ? normals[0].getBandwidth() : 0;
		sizeC = colors != null ? colors[0].getBandwidth() : 0;
		sizeT = texCoords != null ? texCoords[0].getBandwidth() : 0;
		
		offV = 0;
		offN = sizeV;
		offC = sizeV + sizeN;
		offT = sizeV + sizeN + sizeC;
		
		stride = sizeV + sizeN + sizeC + sizeT;

		size = sizeV * (vertices != null ? vertices.length : 0)
				+ sizeN * (normals != null ? normals.length: 0)
				+ sizeC * (colors != null ? colors.length : 0)
				+ sizeT * (texCoords != null ? texCoords.length : 0);
		
		vboID = createID();
		
		if (indices != null) {
			indID = createID();
		
			bindIND();
			IntBuffer indBuffer = arrayToBuffer(indices);
			attachIndexBuffer(indID, indBuffer);
		}
		
		UniPrint.printf("VBO (id %d):\n" +
				" >> sizeV:  %d\n" +
				" >> sizeN:  %d\n" +
				" >> sizeC:  %d\n" +
				" >> sizeT:  %d\n" +
				" >> offV:   %d\n" +
				" >> offN:   %d\n" +
				" >> offC:   %d\n" +
				" >> offT:   %d\n" +
				" >> stride: %d\n" +
				" >> size:   %d\n", vboID,
				sizeV, sizeN, sizeC, sizeT,
				offV, offN, offC, offT,
				stride, size);
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(size);
		float[] fbuffer = new float[buffer.capacity()];
		
		int ind = 0;
		int iind = 0;
		
		for (int i = 0; i < vertices.length; i++) {
			ind = i * stride;
			iind = 0;
			UniPrint.printf("Vertex Group(%d):\n", i);
			if (vertices != null) {
				for (int v = 0; v < vertices[0].getBandwidth(); v++, iind++) {
					fbuffer[ind + iind] = vertices[i].get()[v];
					UniPrint.printf("(fbuffer[%d + %d] = vertices[%d].get()[%d]) = %G\n", 
							ind, iind, i, v, fbuffer[ind + iind]);
				}
			} if (normals != null) {
				for (int n = 0; n < normals[0].getBandwidth(); n++, iind++) {
					fbuffer[ind + iind] = normals[i].get()[n];
					UniPrint.printf("(fbuffer[%d + %d] = normals[%d].get()[%d]) = %G\n", 
							ind, iind, i, n, fbuffer[ind + iind]);
				}
			} if (colors != null) {
				for (int c = 0; c < colors[0].getBandwidth(); c++, iind++) {
					fbuffer[ind + iind] = colors[i].get()[c];
					UniPrint.printf("(fbuffer[%d + %d] = colors[%d].get()[%d]) = %G\n", 
							ind, iind, i, c, fbuffer[ind + iind]);
				}
			} if (texCoords != null) {
				for (int t = 0; t < texCoords[0].getBandwidth(); t++, iind++) {
					fbuffer[ind + iind] = texCoords[i].get()[t];
					UniPrint.printf("(fbuffer[%d + %d] = texCoords[%d].get()[%d]) = %G\n", 
							ind, iind, i, t, fbuffer[ind + iind]);
				}
			}
		}
		
		buffer.rewind();
		buffer.put(fbuffer);
		bindVBO();
		attachDrawBuffer(vboID, buffer);
		
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		
		if (vertices != null) glVertexPointer(sizeV, GL_FLOAT, stride * 4, offV * 4);
		if (normals != null) glNormalPointer(GL_FLOAT, stride * 4, offN * 4);
		if (colors != null) glColorPointer(sizeC, GL_FLOAT, stride * 4, offC * 4);
		if (texCoords != null) glTexCoordPointer(sizeT, GL_FLOAT, stride * 4, offT * 4);
		
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		UniPrint.enabled = save;
	}

	public void render(int mode) {
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		
		bindVBO();
		
		if (vertices != null) glVertexPointer(sizeV, GL_FLOAT, stride * 4, offV * 4);
		if (normals != null) glNormalPointer(GL_FLOAT, stride * 4, offN * 4);
		if (colors != null) glColorPointer(sizeC, GL_FLOAT, stride * 4, offC * 4);
		if (texCoords != null) glTexCoordPointer(sizeT, GL_FLOAT, stride * 4, offT * 4);
		
		if (indices != null) {
			bindIND();
			glDrawRangeElements(mode, 0, indices.length, indices.length, GL_UNSIGNED_INT, 0);
		} else {
			glDrawArrays(mode, 0, vertices.length);
		}
		
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_COLOR_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	}

	private int createID() {
		IntBuffer id = BufferUtils.createIntBuffer(1);
		ARBVertexBufferObject.glGenBuffersARB(id);
		return id.get(0);
	}

	private void attachDrawBuffer(int id, FloatBuffer buf) {
		buf.rewind();
		ARBVertexBufferObject.glBufferDataARB(
				ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buf,
				ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
	}
	
	private void attachIndexBuffer(int id, IntBuffer buf) {
		buf.rewind();
		ARBVertexBufferObject.glBufferDataARB(
				ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, buf, 
				ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
	}
	
	public void bindVBO() {
		bind(vboID, ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB);
	}
	
	public void bindIND() {
		bind(indID, ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB);
	}
	
	public void bind(int id, int target) {
		ARBVertexBufferObject.glBindBufferARB(target, id);
	}
	
	public void delete() {
		if (!deleted) {
			glDeleteBuffers(vboID);
			if (indices != null) glDeleteBuffers(indID);
			deleted = true;
		}
	}
	
}
