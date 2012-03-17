package org.universeengine.display;

import org.universeengine.UniverseEngineEnterPoint;
import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniLoop implements UniPrintable {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	private boolean running = false;
	private boolean pausing = false;
	private boolean forceExit = false;
	private boolean delay = true;
	private UniverseEngineEnterPoint enterPoint;
	private DelayHandler delayHandler;
	public UniDisplay display;

	/**
	 * After calling this, you will only get back on the end of the game, when
	 * the user has pressed escape or has closed the window.
	 * @param enterPoint the enterPoint to call updates of rendering. 
	 * This is not allowed to be null!
	 * @param display the UniDisplay you should have created before.
	 */
	public UniLoop(UniverseEngineEnterPoint enterPoint, UniDisplay display) {
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
		enterPoint.onResize(oldw, oldh);
		oldw = display.getSize().width;
		oldh = display.getSize().height;
	}

	private class DelayHandler implements UniPrintable {

		/* Delay-Handling: */
		private HighResTime sleep;
		private HighResTime delta;
		private HighResTime timeOld;
		private HighResTime time;
		private HighResTime fpsTime;
		private HighResTime realSleepBegin;
		private HighResTime realSleepEnd;
		private HighResTime realSleep;

		/* FPS-Handling: */
		public float fps;
		private long frames;
		private HighResTime fpsTimeOld;
		private long up;

		public DelayHandler() {
			sleep = new HighResTime(0, 0);
			delta = new HighResTime(0, 0);
			timeOld = new HighResTime(0, 0);
			time = new HighResTime(0, 0);
			fpsTime = new HighResTime(0, 0);
			realSleepBegin = new HighResTime(0, 0);
			realSleepEnd = new HighResTime(0, 0);
			realSleep = new HighResTime(0, 0);
			fps = 0f;
			frames = 0;
			fpsTimeOld = new HighResTime(0, 0);
			up = 60;
			setFpsTime(60);
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}

		private HighResTime getDelta() {
			frames++;
			time.set();
			delta.setSubtract(time, timeOld);

			if (frames >= up) {
				fps = ((up * 1000f) / (time.time - fpsTimeOld.time));
				frames = 0;
				UniPrint.printf("%s >> fps: %G\n", getClassName(), fps);
				fpsTimeOld.set(time.time, time.timeNano);
			}

			return delta;
		}

		/**
		 * Call this after every frame.
		 * 
		 * @param fps
		 *            - The wanted FPS to sync to.
		 * @return whether you should exit the loop or not.
		 */
		public boolean update(boolean dodelay) {
			sleep.setSubtract(fpsTime, getDelta());
			sleep.normalizeNano();
				if (dodelay) {
				try {
					realSleepBegin.set();
					while (sleep.time > 0) {
						Thread.sleep(sleep.time, (int) sleep.timeNano);
						realSleepEnd.set();
						realSleep.setSubtract(realSleepEnd, realSleepBegin);
						sleep.setSubtract(sleep, realSleep);
					}
					timeOld.set();
					return false;
				} catch (InterruptedException e) {
					UniPrint.printerrf(this, "Error while sleeping!\n");
					e.printStackTrace();
					return true;
				}
			}
			return false;
		}
		
		public void setFpsTime(long fps) {
			fpsTime.set(1000L/fps, (1000000000L/fps)%1000000L);
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
		
		public String getClassName() {
			return getClass().getSimpleName();
		}

		private class HighResTime {
			
			public long timeNano;
			public long time;
			
			public HighResTime(long timeNano, long time) {
				this.timeNano = timeNano;
				this.time = time;
			}
			
			public void set() {
				timeNano = System.nanoTime();
				time = System.currentTimeMillis();
			}
			
			public void set(long t, long tn) {
				time = t;
				timeNano = tn;
			}
			
			public void setSubtract(HighResTime t1, HighResTime t2) {
				timeNano = t1.timeNano - t2.timeNano;
				time = t1.time -t2.time;
			}
			
			public void normalizeNano() {
				timeNano = timeNano % 1000000L;
				timeNano = timeNano < 0 ? timeNano+1000000L : timeNano;
			}
			
		}
	}

}
