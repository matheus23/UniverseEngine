package org.universeengine.util.render;

import org.universeengine.exceptions.UniGLVersionException;
import org.universeengine.opengl.model.UniMesh;
import org.universeengine.opengl.model.renderer.UniVBORenderer;
import org.universeengine.opengl.texture.UniTexture;
import org.universeengine.opengl.vertex.UniColor3f;
import org.universeengine.opengl.vertex.UniTexCoord2f;
import org.universeengine.opengl.vertex.UniVertex3f;

public class UniCube {

	private UniMesh mesh;
	private UniTexture tex;
	
	/**
	 * C'tor. Will create a Cube, with a VBO-Rendering
	 * Strategy, using a Vertex3f, Color3f and
	 * Integer-index array. The Color of the Vertex
	 * will represend the Vertex itself. 
	 * Color = (r = x negative?, g = y negative?, b = z negative?)
	 * @param s the size of the cube.
	 * @param texture the Texture to use for drawing. If null, 
	 * no UV-Coords will be generated, and no Texture will be bound
	 * on rendering.
	 */
	public UniCube(float s, UniTexture texture) {
		tex = texture;
		
		UniVertex3f[] v = new UniVertex3f[8];
		v[0] = new UniVertex3f( s,  s,  s);
		v[1] = new UniVertex3f( s,  s, -s);
		v[2] = new UniVertex3f( s, -s,  s);
		v[3] = new UniVertex3f( s, -s, -s);
		v[4] = new UniVertex3f(-s,  s,  s);
		v[5] = new UniVertex3f(-s,  s, -s);
		v[6] = new UniVertex3f(-s, -s,  s);
		v[7] = new UniVertex3f(-s, -s, -s);
		
		UniColor3f[] c = new UniColor3f[8];
		c[0] = new UniColor3f( 0f,  0f,  0f);
		c[1] = new UniColor3f( 0f,  0f,  1f);
		c[2] = new UniColor3f( 0f,  1f,  0f);
		c[3] = new UniColor3f( 0f,  1f,  1f);
		c[4] = new UniColor3f( 1f,  0f,  0f);
		c[5] = new UniColor3f( 1f,  0f,  1f);
		c[6] = new UniColor3f( 1f,  1f,  0f);
		c[7] = new UniColor3f( 1f,  1f,  1f);

		UniTexCoord2f[] t = null;
		if (texture != null) {
			t = new UniTexCoord2f[8];
			t[0] = new UniTexCoord2f(1f, 0f);
			t[1] = new UniTexCoord2f(0f, 0f);
			t[2] = new UniTexCoord2f(1f, 1f);
			t[3] = new UniTexCoord2f(0f, 1f);
			t[4] = new UniTexCoord2f(0f, 0f);
			t[5] = new UniTexCoord2f(1f, 0f);
			t[6] = new UniTexCoord2f(0f, 1f);
			t[7] = new UniTexCoord2f(1f, 1f);
		}
		
		int[] i = new int[24];
		i[0] =  7;
		i[1] =  6;
		i[2] =  4;
		i[3] =  5;
		i[4] =  3;
		i[5] =  2;
		i[6] =  0;
		i[7] =  1;
		i[8] =  7;
		i[9] =  6;
		i[10] = 2;
		i[11] = 3;
		i[12] = 5;
		i[13] = 4;
		i[14] = 0;
		i[15] = 1;
		i[16] = 7;
		i[17] = 5;
		i[18] = 1;
		i[19] = 3;
		i[20] = 6;
		i[21] = 4;
		i[22] = 0;
		i[23] = 2;
		
		try {
			UniVBORenderer rend = new UniVBORenderer(v, null, c, t, i);
			rend.create();
			mesh = new UniMesh(rend);
		} catch (UniGLVersionException e) {
			e.printStackTrace();
		}
	}

	public void render(int mode) {
		if (tex != null) tex.bind();
		mesh.render(mode);
		if (tex != null) tex.unbind();
	}
	
	public void destroy() {
		mesh.destroy();
	}
	
}
