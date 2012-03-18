package org.universeengine.mains;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.universeengine.UniverseEngineEnterPoint;
import org.universeengine.display.UniAWTDisplay;
import org.universeengine.display.UniLoop;
import org.universeengine.opengl.model.UniMesh;
import org.universeengine.opengl.model.UniModel;
import org.universeengine.opengl.model.modelloader.UniModelLoader;
import org.universeengine.opengl.model.modelloader.UniModelLoaderException;
import org.universeengine.opengl.model.renderer.UniStandardRenderer;
import org.universeengine.opengl.vertex.UniColor3f;
import org.universeengine.opengl.vertex.UniVertex3f;
import org.universeengine.util.cam.UniCamera;
import org.universeengine.util.input.UniInput;
import org.universeengine.util.input.UniInputListener;
import org.universeengine.util.render.UniDisplayList;

public class UniverseEngineModelViewer implements UniverseEngineEnterPoint, UniInputListener {

	private UniLoop loop;
	private UniAWTDisplay display;
	private UniMesh originLines;
	private boolean limitFPS = true;
	private UniInput input;
	private UniCamera cam;
	private UniModel model;
	private UniDisplayList linesDL; 
	private String modelpath;

	public UniverseEngineModelViewer(String modelpath) {
		this.modelpath = modelpath;
		display = new UniAWTDisplay(800, 600, "UniverseEngine 3D Test");
		loop = new UniLoop(this, display);
		loop.start();
	}

	public void start() {
		display.centerOnDefaultDisplay();
		display.setVisible(true);
		
		input = new UniInput(this);
		cam = new UniCamera(loop);
		
		setUpViewport(loop.display.getSize().width, loop.display.getSize().height);
		glClearColor(0f, 0f, 0f, 1f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		
		setupOriginLines();
		
		try {
			if (modelpath.contains(".uem"))
				model = UniModelLoader.UEM.load(modelpath);
			if (modelpath.contains(".obj"))
				model = UniModelLoader.OBJ.load(modelpath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UniModelLoaderException e) {
			e.printStackTrace();
		}
	}

	public void tick() {
		input.update();
		cam.update();
		if (input.isDown(Keyboard.KEY_ESCAPE)) {
			loop.stop();
		}
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		glColor4f(1f, 1f, 1f, 1f);
		cam.apply();
		
		model.render(GL_QUADS);
		
		glDisable(GL_DEPTH_TEST);
		linesDL.render();
		glEnable(GL_DEPTH_TEST);
	}

	public void pause() {
	}

	public void resume() {
	}

	public void onResize(int oldWidth, int oldHeight) {
		if ((oldWidth != loop.display.getSize().width)
				|| (oldHeight != loop.display.getSize().height)) {
			setUpViewport(loop.display.getSize().width, loop.display.getSize().height);
		}
	}

	public void end() {
	}
	
	public void keyPressed(int key) {
	}
	
	public void keyReleased(int key) {
		if (key == Keyboard.KEY_L) {
			limitFPS = !limitFPS;
			loop.setDelay(limitFPS);
		} if (key == Keyboard.KEY_G){
			Mouse.setGrabbed(!Mouse.isGrabbed());
		}
	}

	public void setUpViewport(int width, int height) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(50f, ((float)width) / ((float)height), 0.01f, 64f);
		glViewport(0, 0, width, height);
		glMatrixMode(GL_MODELVIEW);
	}
	
	private void setupOriginLines() {
		UniVertex3f[] v = new UniVertex3f[6];
		v[0] = new UniVertex3f(0f, 0f, 0f);
		v[1] = new UniVertex3f(1f, 0f, 0f);
		v[2] = new UniVertex3f(0f, 0f, 0f);
		v[3] = new UniVertex3f(0f, 1f, 0f);
		v[4] = new UniVertex3f(0f, 0f, 0f);
		v[5] = new UniVertex3f(0f, 0f, 1f);
		
		UniColor3f[] c = new UniColor3f[6];
		c[0] = new UniColor3f(1f, 0f, 0f);
		c[1] = new UniColor3f(1f, 0f, 0f);
		c[2] = new UniColor3f(0f, 1f, 0f);
		c[3] = new UniColor3f(0f, 1f, 0f);
		c[4] = new UniColor3f(0f, 0f, 1f);
		c[5] = new UniColor3f(0f, 0f, 1f);
		
		UniStandardRenderer std = new UniStandardRenderer(v, null, c, null);
		std.create();
		originLines = new UniMesh(std);
		
		linesDL = new UniDisplayList(originLines, GL_LINES);
		linesDL.create();
	}

	public static void main(String[] args) {
		new UniverseEngineModelViewer("res/box.obj");
	}

}
