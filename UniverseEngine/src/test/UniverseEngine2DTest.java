package test;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.universeengine.UniverseEngineEnterPoint;
import org.universeengine.display.UniAWTDisplay;
import org.universeengine.display.UniLoop;
import org.universeengine.opengl.texture.UniTexture;
import org.universeengine.opengl.texture.UniTextureLoader;

public class UniverseEngine2DTest implements UniverseEngineEnterPoint {
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	private UniAWTDisplay display;
	private boolean pressedF2 = false;
	private UniLoop loop;
	private UniTexture tex;
	private SimpleShape triangle;
	
	public UniverseEngine2DTest() {
		display = new UniAWTDisplay(WIDTH, HEIGHT, "OpenGLTest");
		loop = new UniLoop(this, display);
		loop.start();
	}

	public void start() {
		display.centerOnDefaultDisplay();
		display.setVisible(true);
		Display.setVSyncEnabled(false);
		
		loop.setDelay(false);
		loop.setFrameRecalculationFrames(1000);
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, WIDTH, 0.0, HEIGHT, -1.0, 1.0);
		// gluPerspective(45.0f, width/height, 0.01f, 256.0f);
		glMatrixMode(GL_MODELVIEW);
		glClearColor(0f, 0f, 0f, 1f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		System.out.printf("GLSL version: %s\n", glGetString(GL_SHADING_LANGUAGE_VERSION));
		System.out.printf("OpenGL version: %s\n", glGetString(GL_VERSION));

		// The Folder "res" is ignored by GIT, you will have to create it on your own.
		tex = UniTextureLoader.loadTexture("res/yo.png");
		triangle = new SimpleShape(tex);
	}

	public void tick() {
		if (Keyboard.isKeyDown(Keyboard.KEY_F2) && !pressedF2) {
			loop.saveScreenshot("screenshot0.png");
			pressedF2 = true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			pressedF2 = false;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			loop.stop();
		}
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		float mx = Mouse.getX();
		float my = Mouse.getY();
		glTranslatef(mx, my, 0f);
		
		glColor4f(0.2f, 1f, 0.2f, 1f);
		glBegin(GL_QUADS);
		{
			glVertex2f(- 20.0f, - 20.0f);
			glVertex2f(+ 20.0f, - 20.0f);
			glVertex2f(+ 20.0f, + 20.0f);
			glVertex2f(- 20.0f, + 20.0f);
		}
		glEnd();
		
		tex.bind();
		glBegin(GL_QUADS);
		{
			glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			glTexCoord2f(0f, 1f); glVertex2f(- 20.0f, - 20.0f);
			glTexCoord2f(1f, 1f); glVertex2f(+ 20.0f, - 20.0f);
			glTexCoord2f(1f, 0f); glVertex2f(+ 20.0f, + 20.0f);
			glTexCoord2f(0f, 0f); glVertex2f(- 20.0f, + 20.0f);
		}
		glEnd();
		tex.unbind();
		glColor4f(1f, 0f, 0f, 1f);
		triangle.render();
	}
	
	public void onResize(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		if ((oldWidth != loop.display.getSize().width)
				|| (oldHeight != loop.display.getSize().height)) {
			glViewport(0, 0, newWidth, newHeight);
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0.0, loop.display.getSize().width, 0.0,
					loop.display.getSize().height, -1.0, 1.0);
			glMatrixMode(GL_MODELVIEW);
		}
		oldWidth = loop.display.getSize().width;
		oldWidth = loop.display.getSize().height;
	}
	
	public void pause() {
		System.out.printf("Pausing\n");
	}
	
	public void resume() {
		System.out.printf("Resuming\n");
	}

	public void end() {
	}
	
	public static void main(String[] args) {
		new UniverseEngine2DTest();
	}
	
}
