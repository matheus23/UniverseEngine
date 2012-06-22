package org.universeengine.display;

import java.awt.Dimension;
import java.awt.Point;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class UniDisplayAdapter implements UniDisplay {
	
	private boolean debug;
	private int bpp;

	public UniDisplayAdapter(int width, int height, String caption) {
		bpp = 32;
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setVSyncEnabled(true);
			Display.setTitle(caption);
			Display.setResizable(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public int getBPP() {
		return bpp/8;
	}

	public void setVisible(boolean visible) {
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isCloseRequested() {
		return Display.isCloseRequested();
	}

	public boolean isActivated() {
		return Display.isActive();
	}

	public boolean isIconified() {
		return false;
	}

	public boolean isDebugEnabled() {
		return debug;
	}

	public Dimension getSize() {
		return new Dimension(Display.getWidth(), Display.getHeight());
	}

	public int getWidth() {
		return Display.getWidth();
	}

	public int getHeight() {
		return Display.getHeight();
	}

	public Point getWindowPos() {
		return null;
	}

	public void setCaption(String caption) {
		Display.setTitle(caption);
	}

	public void update() {
		Display.update();
	}

	public void destroy() {
		Display.destroy();
	}

	public void setLocation(int x, int y) {
		Display.setLocation(x, y);
	}
	
	public void setFullscreen(int width, int height, boolean fullscreen) {
		try {
			DisplayMode toTake = null;
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			for (int i = 0; i < modes.length; i++) {
				if (modes[i].getWidth() < width || modes[i].getHeight() < height) {
					continue;
				}
				toTake = modes[i];
			}
			if (toTake != null) {
				Display.setDisplayModeAndFullscreen(toTake);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

}
