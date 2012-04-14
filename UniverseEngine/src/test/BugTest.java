package test;

import static org.lwjgl.opengl.ARBBufferObject.GL_STATIC_DRAW_ARB;
import static org.lwjgl.opengl.ARBBufferObject.glBindBufferARB;
import static org.lwjgl.opengl.ARBBufferObject.glBufferDataARB;
import static org.lwjgl.opengl.ARBBufferObject.glDeleteBuffersARB;
import static org.lwjgl.opengl.ARBBufferObject.glGenBuffersARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class BugTest {

	public static void main(String[] args) {
		try {
			int w = 640;
			int h = 480;

			Display.setDisplayMode(new DisplayMode(w, h));
			Display.setFullscreen(false);
			Display.create();
			glViewport(0, 0, w, h);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		while (!Display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
					| GL_STENCIL_BUFFER_BIT);

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();

			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();

			// create geometry buffers
			FloatBuffer cBuffer = BufferUtils.createFloatBuffer(9);
			cBuffer.put(1).put(0).put(0);
			cBuffer.put(0).put(1).put(0);
			cBuffer.put(0).put(0).put(1);
			cBuffer.flip();

			FloatBuffer vBuffer = BufferUtils.createFloatBuffer(9);
			vBuffer.put(-0.5f).put(-0.5f).put(0.0f);
			vBuffer.put(+0.5f).put(-0.5f).put(0.0f);
			vBuffer.put(+0.5f).put(+0.5f).put(0.0f);
			vBuffer.flip();

			// create index buffer
			ShortBuffer iBuffer = BufferUtils.createShortBuffer(3);
			iBuffer.put((short) 0);
			iBuffer.put((short) 1);
			iBuffer.put((short) 2);
			iBuffer.flip();

			//

			IntBuffer ib = BufferUtils.createIntBuffer(3);

			glGenBuffersARB(ib);
			int vHandle = ib.get(0);
			int cHandle = ib.get(1);
			int iHandle = ib.get(2);

			glEnableClientState(GL_VERTEX_ARRAY);
			glEnableClientState(GL_COLOR_ARRAY);

			glBindBufferARB(GL_ARRAY_BUFFER_ARB, vHandle);
			glBufferDataARB(GL_ARRAY_BUFFER_ARB, vBuffer, GL_STATIC_DRAW_ARB);
			glVertexPointer(3, GL_FLOAT, /* stride */3 << 2, 0L);

			glBindBufferARB(GL_ARRAY_BUFFER_ARB, cHandle);
			glBufferDataARB(GL_ARRAY_BUFFER_ARB, cBuffer, GL_STATIC_DRAW_ARB);
			glColorPointer(3, GL_FLOAT, /* stride */3 << 2, 0L);

			glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, iHandle);
			glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, iBuffer,
					GL_STATIC_DRAW_ARB);

			glDrawElements(GL_TRIANGLES, /* elements */3, GL_UNSIGNED_SHORT, 0L);

			glBindBufferARB(GL_ARRAY_BUFFER_ARB, 0);
			glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, 0);

			glDisableClientState(GL_COLOR_ARRAY);
			glDisableClientState(GL_VERTEX_ARRAY);

			// cleanup VBO handles
			ib.put(0, vHandle);
			ib.put(1, cHandle);
			ib.put(2, cHandle);
			glDeleteBuffersARB(ib);

			Display.update();
		}

		Display.destroy();
	}

}
