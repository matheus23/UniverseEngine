package org.universeengine.opengl.lighting;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniStdLight implements UniPrintable {

	private int lightMode;
	private float x;
	private float y;
	private float z;
	private float w;

	public UniStdLight(int lightNum, float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		lightMode = GL_LIGHT0 + lightNum;
		setPosition(x, y, z, 1f);
		if (lightMode > GL_LIGHT7) {
			UniPrint.printerrf(this, "You cannot have more than 7 Lights with StdLight: %d", lightNum);
			System.exit(1);
		}
	}
	
	public UniStdLight(int lightNum, float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		lightMode = GL_LIGHT0 + lightNum;
		setPosition(x, y, z, w);
		if (lightMode > GL_LIGHT7) {
			UniPrint.printerrf(this, "You cannot have more than 7 Lights with StdLight: %d", lightNum);
			System.exit(1);
		}
	}
	
	public void render() {
		glColor4f(1f, 1f, 1f, 1f);
		glPointSize(10f);
		glPushMatrix();
		{
			glBegin(GL_POINTS); 
			{
				glVertex3f(x, y, z);
			}
			glEnd();
		}
		glPopMatrix();
	}
	
	public void updatePosition() {
		setPosition(x, y, z, w);
	}
	
	public void bind() {
		glEnable(lightMode);
	}
	
	public void setPosition(float x, float y, float z, float w) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(4);
		buf.put(x).put(y).put(z).put(w).rewind();
		glLight(lightMode, GL_POSITION, buf);
	}
	
	public void setAmbient(float r, float g, float b, float a) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(4);
		buf.put(r).put(g).put(b).put(a).rewind();
		glLight(lightMode, GL_AMBIENT, buf);
	}
	
	public void setDiffuse(float r, float g, float b, float a) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(4);
		buf.put(r).put(g).put(b).put(a).rewind();
		glLight(lightMode, GL_DIFFUSE, buf);
	}
	
	public void setSpecular(float r, float g, float b, float a) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(4);
		buf.put(r).put(g).put(b).put(a).rewind();
		glLight(lightMode, GL_SPECULAR, buf);
	}
	
	public String getClassName() {
		return getClass().getSimpleName();
	}

}
