package org.universeengine.mains;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import java.io.File;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.universeengine.UniverseEngineEnterPoint;
import org.universeengine.display.UniAWTDisplay;
import org.universeengine.display.UniLoop;
import org.universeengine.exceptions.UniGLVersionException;
import org.universeengine.opengl.fbo.UniFrameBufferObject;
import org.universeengine.opengl.lighting.UniStdLight;
import org.universeengine.opengl.lighting.UniStdMaterial;
import org.universeengine.opengl.model.UniMesh;
import org.universeengine.opengl.model.UniModel;
import org.universeengine.opengl.model.modelloader.UniModelLoader;
import org.universeengine.opengl.model.modelloader.UniModelLoaderException;
import org.universeengine.opengl.model.renderer.UniStandardRenderer;
import org.universeengine.opengl.shader.UniShader;
import org.universeengine.opengl.shader.UniShaderProgram;
import org.universeengine.opengl.shader.UniUniform;
import org.universeengine.opengl.texture.UniTexture;
import org.universeengine.opengl.texture.UniTextureLoader;
import org.universeengine.opengl.vertex.UniColor3f;
import org.universeengine.opengl.vertex.UniVertex3f;
import org.universeengine.util.UniPrint;
import org.universeengine.util.cam.UniCamera;
import org.universeengine.util.input.UniInput;
import org.universeengine.util.input.UniInputListener;
import org.universeengine.util.render.UniDisplayList;

public class UniverseEngineBlurShaderTest implements UniverseEngineEnterPoint, UniInputListener {
	
	public static final int FBO_WIDTH = 800;
	public static final int FBO_HEIGHT = 600;

	private UniLoop loop;
	private UniAWTDisplay display;
	private UniMesh originLines;
	private boolean limitFPS = true;
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
	private UniFrameBufferObject fbo;
	private boolean blurEnabled = false;
	private UniShaderProgram blurShader;
	private UniUniform uniformTexture;
	private UniUniform uniformShift;

	public UniverseEngineBlurShaderTest(String modelpath, String texturepath, int mode, boolean start) {
		this.modelpath = modelpath;
		this.texturepath = texturepath;
		this.mode = mode;
		display = new UniAWTDisplay(800, 600, "UniverseEngine 3D Test");
		loop = new UniLoop(this, display);
		if (start) {
			lateStart();
		}
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

	public void start() {
		UniPrint.enabled = true;
		display.centerOnDefaultDisplay();
		display.setVisible(true);
		
		input = new UniInput(this);
		cam = new UniCamera(loop);
		
		setUpViewport(loop.display.getSize().width, loop.display.getSize().height);
		glClearColor(0f, 0f, 0f, 0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
			if (modelpath.endsWith(".uem"))
				model = UniModelLoader.UEM.load(modelpath);
			if (modelpath.endsWith(".obj"))
				model = UniModelLoader.OBJ.load(modelpath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UniModelLoaderException e) {
			e.printStackTrace();
		}
		if (texturepath != null && !texturepath.isEmpty()) {
			tex = UniTextureLoader.loadTexture(texturepath);
		}
		try {
			fbo = new UniFrameBufferObject(FBO_WIDTH, FBO_HEIGHT, true);
		} catch (UniGLVersionException e) {
			UniPrint.printerrf("FBO not supported!\n");
			e.printStackTrace();
		}
		blurShader = new UniShaderProgram(
				new UniShader("shaders/blur.vert", UniShader.VERTEX_SHADER),
				new UniShader("shaders/blur.frag", UniShader.FRAGMENT_SHADER));
		uniformTexture = new UniUniform("texture", blurShader);
		uniformShift = new UniUniform("shift", blurShader);
	}

	public void tick() {
		input.update();
		cam.update();
		if (input.isDown(Keyboard.KEY_ESCAPE)) {
			loop.stop();
		}
	}

	public void render() {
		// FBO Render Pass:
		renderFBO();

		// Real render pass:
		renderFromFBO();
	}
	
	public void renderFBO() {
		// Bind FBO:
		fbo.bind(false, 50f, 0.1f, 64f);
		// Render into FBO:
		glClearColor(0.3f, 0.5f, 0.8f, 0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		if (!wireFrame) {
			glColor3f(1f, 1f, 1f);
		} else {
			glColor3f(1f, 0.5f, 0f);
		}
		cam.apply();
		
		if (light != null) {
			glEnable(GL_LIGHTING);
			light.bind();
		}
		
		if (tex != null) tex.bind();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		model.render(mode);
		glDisable(GL_BLEND);
		if (tex != null) tex.unbind();

		if (light != null) {
			glDisable(GL_LIGHTING);
		}
		
		glDisable(GL_DEPTH_TEST);
		linesDL.render();
		glEnable(GL_DEPTH_TEST);
		// Unbind FBO:
		fbo.unbind();
	}
	
	public void renderFromFBO() {
		float w = display.getWidth();
		float h = display.getHeight();
		
		setUpViewport(display.getWidth(), display.getHeight());
		
		glClearColor(0f, 0f, 0f, 0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		glDisable(GL_DEPTH_TEST);
		if (blurEnabled) {
			glActiveTexture(GL_TEXTURE0);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE);
			glColor4f(1f, 1f, 1f, 1f);
			blurShader.use();
			uniformTexture.uniform1i(0);
			uniformShift.uniform2f(1.4f/w, 0f);
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, fbo.getTextureID());
			glBegin(GL_QUADS);
			{
				glMultiTexCoord2f(GL_TEXTURE0, 0f, 0f); glVertex2f(0f, 0f);
				glMultiTexCoord2f(GL_TEXTURE0, 1f, 0f); glVertex2f( w, 0f);
				glMultiTexCoord2f(GL_TEXTURE0, 1f, 1f); glVertex2f( w,  h);
				glMultiTexCoord2f(GL_TEXTURE0, 0f, 1f); glVertex2f(0f,  h);
			}
			glEnd();
			
			glColor4f(1f, 1f, 1f, 0.5f);
			uniformTexture.uniform1i(0);
			uniformShift.uniform2f(0f, 1.4f/h);
			glBegin(GL_QUADS);
			{
				glMultiTexCoord2f(GL_TEXTURE0, 0f, 0f); glVertex2f(0f, 0f);
				glMultiTexCoord2f(GL_TEXTURE0, 1f, 0f); glVertex2f( w, 0f);
				glMultiTexCoord2f(GL_TEXTURE0, 1f, 1f); glVertex2f( w,  h);
				glMultiTexCoord2f(GL_TEXTURE0, 0f, 1f); glVertex2f(0f,  h);
			}
			glEnd();
			glDisable(GL_TEXTURE_2D);
			blurShader.unuse();
			glDisable(GL_BLEND);
		} else {
			glDisable(GL_BLEND);
			glColor4f(1f, 1f, 1f, 1f);
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, fbo.getTextureID());
			glBegin(GL_QUADS);
			{
				glMultiTexCoord2f(GL_TEXTURE0, 0f, 0f); glVertex2f(0f, 0f);
				glMultiTexCoord2f(GL_TEXTURE0, 1f, 0f); glVertex2f( w, 0f);
				glMultiTexCoord2f(GL_TEXTURE0, 1f, 1f); glVertex2f( w,  h);
				glMultiTexCoord2f(GL_TEXTURE0, 0f, 1f); glVertex2f(0f,  h);
			}
			glEnd();
			glDisable(GL_TEXTURE_2D);
		}
		glEnable(GL_DEPTH_TEST);
	}

	public void pause() {
	}

	public void resume() {
	}

	public void onResize(int oldWidth, int oldHeight, int newWidth, int newHeight) {
		setUpViewport(newWidth, newHeight);
	}

	public void end() {
		model.destroy();
		fbo.destroy();
	}
	
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
	}
	
	public void keyReleased(int key) {
		if (key == Keyboard.KEY_L) {
			limitFPS = !limitFPS;
			loop.setDelay(limitFPS);
		} if (key == Keyboard.KEY_G){
			Mouse.setGrabbed(!Mouse.isGrabbed());
		} if (key == Keyboard.KEY_F) {
			wireFrame = !wireFrame;
			glPolygonMode(GL_FRONT_AND_BACK, wireFrame ? GL_LINE : GL_FILL);
		} if (key == Keyboard.KEY_B) {
			blurEnabled = !blurEnabled;
		}
	}

	public void setUpViewport(int width, int height) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glViewport(0, 0, width, height);
//		gluPerspective(50f, ((float)width) / ((float)height), 0.1f, 64f);
		glOrtho(0, width, 0, height, -1, 1);
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
		UniverseEngineBlurShaderTest viewer = 
				new UniverseEngineBlurShaderTest(modelpath, texturepath, mode, false);
		
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

}
