package org.universeengine.display;

import java.awt.Dimension;
import java.awt.Point;

public interface UniDisplay {
	
	public int getBPP();
	public void setVisible(boolean visible);
	public void setDebug(boolean debug);
	public boolean isCloseRequested();
	public boolean isActivated();
	public boolean isIconified();
	public boolean isDebugEnabled();
	public Dimension getSize();
	public int getWidth();
	public int getHeight();
	public Point getWindowPos();
	public void setCaption(String caption);
	public void setLocation(int x, int y);
	public void update();
	public void destroy();
	public void setFullscreen(int width, int height, boolean fullscreen);

}
