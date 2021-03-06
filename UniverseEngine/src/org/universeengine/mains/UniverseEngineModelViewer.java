package org.universeengine.mains;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_LOCAL_VIEWER;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLightModeli;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.File;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.universeengine.UniverseEngineEntryPoint;
import org.universeengine.display.UniAWTDisplay;
import org.universeengine.display.UniLoop;
import org.universeengine.opengl.lighting.UniStdLight;
import org.universeengine.opengl.lighting.UniStdMaterial;
import org.universeengine.opengl.model.UniMesh;
import org.universeengine.opengl.model.UniModel;
import org.universeengine.opengl.model.modelloader.UniModelLoader;
import org.universeengine.opengl.model.modelloader.UniModelLoaderException;
import org.universeengine.opengl.model.renderer.UniStandardRenderer;
import org.universeengine.opengl.shader.UniShaderProgram;
import org.universeengine.opengl.texture.UniTexture;
import org.universeengine.opengl.texture.UniTextureLoader;
import org.universeengine.opengl.vertex.UniColor3f;
import org.universeengine.opengl.vertex.UniVertex3f;
import org.universeengine.util.UniPrint;
import org.universeengine.util.cam.UniCamera;
import org.universeengine.util.input.UniInput;
import org.universeengine.util.input.UniInputListener;
import org.universeengine.util.render.UniDisplayList;

public class UniverseEngineModelViewer implements UniverseEngineEntryPoint, UniInputListener {

	public static final float NORM_FOV = 100f;

	private UniLoop loop;
	private UniAWTDisplay display;
	private UniMesh originLines;
	private boolean limitFPS = false;
	private UniInput input;
	private UniCamera cam;
	private UniModel model;
	private UniDisplayList linesDL;
	private String modelpath;
	private String texturepath;
	private UniTexture tex;
	private boolean wireFrame = false;
	private int mode;
	private UniStdLight light;
	private UniStdMaterial mat;
	private UniShaderProgram prog;
	private boolean vsync = true;
	private float fov = NORM_FOV;

	public UniverseEngineModelViewer(String modelpath, String texturepath, int mode, boolean start) {
		this.modelpath = modelpath;
		this.texturepath = texturepath;
		this.mode = mode;
		this.prog = null;
		display = new UniAWTDisplay(800, 600, "UniverseEngine 3D Test");
		loop = new UniLoop(this, display);
		if (start) {
			lateStart();
		}
	}

	public void setShaderProgram(UniShaderProgram prog) {
		this.prog = prog;
	}

	public void setMaterial(UniStdMaterial mat) {
		this.mat = mat;
		this.mat.bind();
	}

	public void setLight(UniStdLight light) {
		this.light = light;
	}

	public void lateStart() {
		loop.start();
	}

	@Override
	public void start() {
		UniPrint.enabled = true;
		display.centerOnDefaultDisplay();
		display.setVisible(true);
		Display.setVSyncEnabled(vsync);
		loop.setDelay(limitFPS);

		input = new UniInput(this);
		cam = new UniCamera(loop);

		setUpViewport(loop.display.getSize().width, loop.display.getSize().height);
		glClearColor(0f, 0f, 0f, 0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

		if (light != null) {
			glEnable(GL_LIGHTING);
			glShadeModel(GL_SMOOTH);
			glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_TRUE);
		}

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
		if (texturepath != null && !texturepath.isEmpty()) {
			tex = UniTextureLoader.loadTexture(texturepath);
		}
	}

	@Override
	public void tick() {
		input.update();
		cam.update();
		if (input.isDown(Keyboard.KEY_ESCAPE)) {
			loop.stop();
		}
	}

	@Override
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		if (!wireFrame) {
			glColor4f(1f, 1f, 1f, 1f);
		} else {
			glColor4f(1f, 0.5f, 0f, 1f);
		}
		cam.apply();

		if (light != null) {
			glEnable(GL_LIGHTING);
			light.bind();
			light.updatePosition();
		}

		if (tex != null) tex.bind();
		if (prog != null) prog.use();
		model.render(mode);
		if (prog != null) prog.unuse();
		if (tex != null) tex.unbind();

		if (light != null) {
			glDisable(GL_LIGHTING);
			light.render();
		}

		glDisable(GL_DEPTH_TEST);
		linesDL.render();
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void onResize(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		setUpViewport(newWidth, newHeight);
	}

	@Override
	public void end() {
	}

	@Override
	public void keyPressed(int key) {
		if (key == Keyboard.KEY_F2) {
			String screenname = "screenshots/screenshot";
			String imageformat = ".png";
			String screenpath = null;
			int i = 0;
			File f = new File(screenname + i + imageformat);

			do {
				f = new File(screenname + i + imageformat);
				screenpath = screenname + i + imageformat;
				i++;
			} while(f.exists());

			loop.saveScreenshot(screenpath);
		}
		if (key == Keyboard.KEY_I) {
			fov += 2f;
			setUpViewport(display.getWidth(), display.getHeight());
			System.out.println("Setting fov to " + fov);
		}
		if (key == Keyboard.KEY_K) {
			fov -= 2f;
			setUpViewport(display.getWidth(), display.getHeight());
			System.out.println("Setting fov to " + fov);
		}
		if (key == Keyboard.KEY_L) {
			fov = NORM_FOV;
			setUpViewport(display.getWidth(), display.getHeight());
			System.out.println("Setting fov to " + fov);
		}
	}

	@Override
	public void keyReleased(int key) {
		if (key == Keyboard.KEY_L) {
			limitFPS = !limitFPS;
			loop.setDelay(limitFPS);
		} if (key == Keyboard.KEY_G){
			Mouse.setGrabbed(!Mouse.isGrabbed());
		} if (key == Keyboard.KEY_F) {
			wireFrame = !wireFrame;
			glPolygonMode(GL_FRONT_AND_BACK, wireFrame ? GL_LINE : GL_FILL);
		} if (key == Keyboard.KEY_V) {
			vsync = !vsync;
			Display.setVSyncEnabled(vsync);
		}
	}

	public void setUpViewport(int width, int height) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(fov, ((float)width) / ((float)height), 0.01f, 64f);
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
		start("res/OrangeCharacterHighRes.obj", null, GL_QUADS);
	}

	public static void start(String modelpath, String texturepath, int mode) {
		UniPrint.enabled = true;
		UniverseEngineModelViewer viewer =
				new UniverseEngineModelViewer(modelpath, texturepath, mode, false);

		UniStdLight light = new UniStdLight(0, 4f, 4f, 4f, 1f);
		light.setAmbient(0.2f, 0.2f, 0.2f, 1f);
		light.setDiffuse(0.8f, 0.8f, 0.8f, 1f);
		light.setSpecular(1f, 1f, 1f, 1f);

		UniStdMaterial mat = new UniStdMaterial();
		mat.setAmbient(1f, 0.7f, 0f, 1f);
		mat.setDiffuse(1f, 0.5f, 0f, 1f);
		mat.setSpecular(1f, 1f, 1f, 1f);
		mat.setShininess(60f);

		viewer.setLight(light);
		viewer.setMaterial(mat);

		viewer.lateStart();
	}

	@Override
	public void displayUpdate() {
		Display.update();
	}

	@Override
	public boolean isCloseRequested() {
		return Display.isCloseRequested();
	}

}
