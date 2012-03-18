package org.universeengine.opengl.model;

import org.universeengine.opengl.vertex.UniNormal3f;
import org.universeengine.opengl.vertex.UniTexCoord2f;
import org.universeengine.opengl.vertex.UniElement;
import org.universeengine.opengl.vertex.UniVertex2f;
import org.universeengine.opengl.vertex.UniVertex3f;
import org.universeengine.util.UniPrint;

public final class UniMeshUtil {

	public static final int VERTEX_ARRAY = 0;
	public static final int COLOR_ARRAY = 1;
	public static final int NORMAL_ARRAY = 2;
	public static final int TEXCOORD_ARRAY = 3;

	public static float[] createVertexArray(UniElement[] vertices, int type) {
		if (type < 0 || type > 3) {
		}
		int bandwidth = vertices[0].getBandwidth();
		int ind = 0;
		float[] array = new float[vertices.length * bandwidth];
		switch (type) {
		case VERTEX_ARRAY:
			switch (bandwidth) {
			case 2:
				UniVertex2f v2f = null;
				for (int i = 0; i < vertices.length; i++) {
					ind = i * bandwidth;
					v2f = (UniVertex2f) vertices[i];
					array[ind] = v2f.get()[0];
					array[ind + 1] = v2f.get()[1];
				}
				return array;
			case 3:
				UniVertex3f v3f = null;
				for (int i = 0; i < vertices.length; i++) {
					ind = i * bandwidth;
					v3f = (UniVertex3f) vertices[i];
					array[ind] = v3f.get()[0];
					array[ind + 1] = v3f.get()[1];
					array[ind + 2] = v3f.get()[2];
				}
				return array;
			default:
				UniPrint.printerrf("UniModelUtil: Unknown Bandwidth for Vertex array: %d\n", bandwidth);
			}
		case NORMAL_ARRAY:
			switch(bandwidth) {
			case 3:
				UniNormal3f n3f = null;
				for (int i = 0; i < vertices.length; i++) {
					ind = i * 3;
					n3f = (UniNormal3f) vertices[i];
					array[ind] = n3f.get()[0];
					array[ind + 1] = n3f.get()[1];
					array[ind + 2] = n3f.get()[2];
				}
				return array;
			default:
				UniPrint.printerrf("UniModelUtil: Unknown Bandwidth for Normal array: %d\n", bandwidth);
			}
		case TEXCOORD_ARRAY:
			switch(bandwidth) {
			case 2:
				UniTexCoord2f t2f = null;
				for (int i = 0; i < vertices.length; i++) {
					ind = i * 3;
					t2f = (UniTexCoord2f) vertices[i];
					array[ind] = t2f.get()[0];
					array[ind + 1] = t2f.get()[1];
				}
				return array;
			default:
				UniPrint.printerrf("UniModelUtil: Unknown Bandwidth for TexCoord array: %d\n", bandwidth);
			}
		default:
			UniPrint.printerrf("UniModelUtil: Unknown type %d\n", type);
		}
		return null;
	}

}
