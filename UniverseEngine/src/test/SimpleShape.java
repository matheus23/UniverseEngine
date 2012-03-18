package test;

import org.lwjgl.opengl.GL11;
import org.universeengine.exceptions.UniGLVersionException;
import org.universeengine.opengl.model.UniMesh;
import org.universeengine.opengl.model.renderer.UniMeshRenderer;
import org.universeengine.opengl.model.renderer.UniInterleavedVBORenderer;
import org.universeengine.opengl.texture.UniTexture;
import org.universeengine.opengl.vertex.UniColor4f;
import org.universeengine.opengl.vertex.UniTexCoord2f;
import org.universeengine.opengl.vertex.UniVertex3f;

/**
 * This Class is only for Testing!
 * @author matheus23
 */
public class SimpleShape {
	
	public UniMeshRenderer rend1;
	public UniMeshRenderer rend2;
	public UniMesh mesh1;
	public UniMesh mesh2;
	public UniTexture t;
	
	public SimpleShape(UniTexture t) {
		this.t = t;
		UniVertex3f[] vertices = new UniVertex3f[4];
		vertices[0] = new UniVertex3f(0f,   0f,   0f);
		vertices[1] = new UniVertex3f(100f, 0f,   0f);
		vertices[2] = new UniVertex3f(100f, 100f, 0f);
		vertices[3] = new UniVertex3f(0f,   100f, 0f);
		
		UniColor4f[] colors = new UniColor4f[4];
		colors[0] = new UniColor4f(1f, 0f, 0f, 1f);
		colors[1] = new UniColor4f(0f, 1f, 0f, 1f);
		colors[2] = new UniColor4f(0f, 0f, 1f, 1f);
		colors[3] = new UniColor4f(1f, 1f, 1f, 1f);
		
		UniTexCoord2f[] texCoords = new UniTexCoord2f[4];
		texCoords[0] = new UniTexCoord2f(0f, 1f);
		texCoords[1] = new UniTexCoord2f(1f, 1f);
		texCoords[2] = new UniTexCoord2f(1f, 0f);
		texCoords[3] = new UniTexCoord2f(0f, 0f);
		
		try {
			rend1 = new UniInterleavedVBORenderer(vertices, null, colors, null, null);
			rend2 = new UniInterleavedVBORenderer(vertices, null, null, texCoords, null);
		} catch (UniGLVersionException e) {
			e.printStackTrace();
		}
		rend1.create();
		rend2.create();
		
		mesh1 = new UniMesh(rend1);
		mesh2 = new UniMesh(rend2);
	}
	
	public void render() {
		mesh1.render(GL11.GL_QUADS);
		t.bind();
		mesh2.render(GL11.GL_QUADS);
		t.unbind();
	}

}
