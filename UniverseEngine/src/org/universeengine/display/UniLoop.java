package org.universeengine.display;

import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.universeengine.UniverseEngineEntryPoint;
import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniLoop implements UniPrintable {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	private boolean running = false;
	private boolean pausing = false;
	private boolean forceExit = false;
	private boolean delay = true;
	private boolean saving = false;
	private UniverseEngineEntryPoint enterPoint;
	private DelayHandler delayHandler;
	public UniDisplay display;

	/**
	 * After calling this, you will only get back on the end of the game, when
	 * the user has pressed escape or has closed the window.
	 * @param enterPoint the enterPoint to call updates of rendering. 
	 * This is not allowed to be null!
	 * @param display the UniDisplay you should have created before.
	 */
	public UniLoop(UniverseEngineEntryPoint enterPoint, UniDisplay display) {
		this.enterPoint = enterPoint;
		this.display = display;
	}
	
	private void runLoop() {
		delayHandler = new DelayHandler();
		delayHandler.setFpsCalcDelay(60);
		int oldw = display.getSize().width;
		int oldh = display.getSize().height;
		enterPoint.start();
		running = true;
		while (running) {
			if (display == null) {
				UniPrint.printerrf(this,
					" >> Haven't got an UniDisplay attatched,\n" +
					" >> Will not execute tick() and render()\n");
				break;
			}
			if (pausing) {
				try {
					Thread.sleep(50);
					if (!display.isIconified()) resume();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				if (display.isIconified()) pause();
				onResize(oldw, oldh);
				tick();
				render();
				display.update();
				running = !(display.isCloseRequested()
						| delayHandler.update(delay));
				if (forceExit) break;
			}
		}
		enterPoint.end();
		System.gc();
		System.runFinalization();
		if (display != null) display.destroy();
	}

	/**
	 * Saves a Screenshot of OpenGL's Front-Buffer.
	 * 
	 * @param filepath Filepath to save the Screenshot to.
	 */
	public void saveScreenshot(String filepath) {
		if (!saving) {
			glReadBuffer(GL_FRONT);
			IntBuffer viewport = BufferUtils.createIntBuffer(16);
			viewport.rewind();
			glGetInteger(GL_VIEWPORT, viewport);
			viewport.rewind();
			UniPrint.printoutf(this, "Viewport size: (%d, %d) (%d, %d)\n", 
					viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
			int width = viewport.get(2);
			int height = viewport.get(3);
			int bpp = display.getBPP();
			
			if (display instanceof UniAWTDisplay) {
				UniAWTDisplay awtdisplay = (UniAWTDisplay) display;
				boolean maximized = awtdisplay.isMaximized();
				if (maximized) {
					bpp = 4;
				}
			}
			
			UniPrint.printoutf(this, 
				"Saving screenshot... Bytes per Pixel: %d\n", bpp);
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
			int format = 0;
			switch(bpp) {
			case 3: format = GL_RGB; UniPrint.printoutf(this, "Reading Pixels with GL_RBG format!\n"); break;
			case 4: format = GL_RGBA; UniPrint.printoutf(this, "Reading Pixels with GL_RBGA format!\n");  break;
			default: UniPrint.printerrf(this, "Unknown BPP while saving Screenshot: %d\n", bpp);
			}
			glReadPixels(0, 0, width, height, format, GL_UNSIGNED_BYTE, buffer);
			saving = true;
			SaveScreenshotThread sst = new SaveScreenshotThread(buffer, width,
					height, bpp, filepath, "PNG", this);
			sst.start();
		}
	}
	
	public void setFrameRecalculationFrames(long frames) {
		delayHandler.setFpsCalcDelay(frames);
	}
	
	/**
	 * @return the last updated fps.
	 */
	public float getLastFps() {
		return delayHandler.fps;
	}

	private void tick() {
		enterPoint.tick();
	}

	private void render() {
		enterPoint.render();
	}
	
	/**
	 * Pauses the loop and then calls
	 * UniverseEngineEnterPoint.pause()
	 */
	public void pause() {
		pausing = true;
		enterPoint.pause();
	}
	
	/**
	 * Resumes the loop and then calls
	 * UniverseEngineEnterPoint.resume();
	 */
	public void resume() {
		pausing = false;
		enterPoint.resume();
	}
	
	/**
	 * Calls the private method runLoop();
	 * Does NOT start a new Thread.
	 */
	public void start() {
		if (!running) {
			runLoop();
		}
	}
	
	/**
	 * Forces to Exit the Loop, and with that,
	 * destroying the display and OpenGL Context.
	 */
	public void stop() {
		forceExit = true;
	}
	
	public void setDelay(boolean delay) {
		this.delay = delay;
	}
	
	public boolean getDelay() {
		return delay;
	}
	
	public void setFrameCap(long fps) {
		delayHandler.setFpsTime(fps);
		delayHandler.setFpsCalcDelay(fps);
	}
	
	public String getClassName() {
		return getClass().getSimpleName();
	}
	
	private void onResize(int oldw, int oldh) {
		if ((oldw != display.getSize().width)
				|| (oldh != display.getSize().height)) {
			enterPoint.onResize(oldw, oldh, display.getSize().width, display.getSize().height);
		}
		oldw = display.getSize().width;
		oldh = display.getSize().height;
	}

	private class DelayHandler implements UniPrintable {

		/* Delay-Handling: */
		private long timeOld;
		private long time;
		private long fpsTime;
		private long yieldTime;
		private long overSleep;
		private long loopTime;
		private long variableYieldTime;

		/* FPS-Handling: */
		public float fps;
		private long frames;
		private long fpsTimeOld;
		private long up;

		public DelayHandler() {
			timeOld = 0;
			time = 0;
			fpsTime = 0;
			yieldTime = 0;
			overSleep = 0;
			loopTime = 0;
			fps = 0f;
			frames = 0;
			fpsTimeOld = 0;
			up = 60;
			setFpsTime(60);
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}

		private void calcFps() {
			frames++;
			time = getNano();

			if (frames >= up) {
				fps = ((up * 1000000000f) / (time - fpsTimeOld));
				frames = 0;
				UniPrint.printf("%s >> fps: %G\n", getClassName(), fps);
				fpsTimeOld = time;
			}
		}

		/**
		 * Call this after every frame.
		 * 
		 * @param fps
		 *            - The wanted FPS to sync to.
		 * @return whether you should exit the loop or not.
		 */
		public boolean update(boolean dodelay) {
			calcFps();
			if (dodelay) {
				yieldTime = Math.min(fpsTime, variableYieldTime + fpsTime % (1000*1000));
				overSleep = 0; // time the sync goes over by
				
				try {
					while (true) {
						loopTime = getNano() - timeOld;
						
						if (loopTime < fpsTime- yieldTime) {
							Thread.sleep(1);
						}
						else if (loopTime < fpsTime) {
							Thread.yield();
						}
						else {
							overSleep = loopTime - fpsTime;
							break;
						}
					}
				} catch (InterruptedException e) {
					UniPrint.printerrf(this, "Error while sleeping!\n");
					return true;
				}
				
				timeOld = getNano() - Math.min(overSleep, fpsTime);
				
				if (overSleep > variableYieldTime) {
					variableYieldTime = Math.min(variableYieldTime + 200*1000, fpsTime);
				}
				else if (overSleep < variableYieldTime - 200*1000) {
					variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
				}
			}
			return false;
		}
		
		public void setFpsTime(long fps) {
			fpsTime = (1000000000L / fps);
		}
		
		/**
		 * Set how much frames to wait, until the next
		 * frame rate is printed. Also affects fps
		 * calculation, because fps are calculated, 
		 * when they are printed.
		 * @param frames - the frames to wait.
		 */
		public void setFpsCalcDelay(long frames) {
			up = frames;
		}
		
		/**
		 * Get System Nano Time
		 * @return will return the current time in nano's
		 */
		private long getNano() {
		    return (Sys.getTime() * 1000000000) / Sys.getTimerResolution();
		}
		
		public String getClassName() {
			return getClass().getSimpleName();
		}
	}

	private class SaveScreenshotThread extends Thread implements UniPrintable {

		private ByteBuffer buffer;
		private int width;
		private int height;
		private int bpp;
		private String location;
		private String format;
		private UniLoop loop;

		public SaveScreenshotThread(ByteBuffer buffer, int width, int height,
				int bpp, String location, String format, UniLoop loop) {
			this.buffer = buffer;
			this.width = width;
			this.height = height;
			this.bpp = bpp;
			this.location = location;
			this.format = format;
			this.loop = loop;
		}

		public void run() {
			File file = new File(location);
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);

			UniPrint.printoutf(this,
					"Saving with w:%d h:%d\n",
					width, height);

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int i = (x + (width * y)) * bpp;
					int r = buffer.get(i) & 0xFF;
					int g = buffer.get(i + 1) & 0xFF;
					int b = buffer.get(i + 2) & 0xFF;
					image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16)
							| (g << 8) | b);
				}
			}

			try {
				ImageIO.write(image, format, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			UniPrint.printoutf(this,
					"SaveScreenshotThread finished! Saved into \"%s\".\n",
					location);
			loop.saving = false;
		}
		
		public String getClassName() {
			return getClass().getSimpleName();
		}
	}

}
