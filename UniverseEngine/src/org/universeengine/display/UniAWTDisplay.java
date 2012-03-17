package org.universeengine.display;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.universeengine.util.UniPrint;
import org.universeengine.util.UniPrintable;

public class UniAWTDisplay implements UniPrintable, UniDisplay {

	private GLFrame frame;
	private GLCanvas canvas;
	private boolean debug = false;

	
	/**
	 * Create A new Display, where the GLContext is attached to
	 * If you create two of them, the last one created, gets 
	 * the GLContext attached.
	 * Width and Height of this Display can change, if the
	 * Use resizes the Window.
	 * 
	 * @param width width of the display.
	 * @param height height of the display.
	 * @param caption caption of the display.
	 */
	public UniAWTDisplay(int width, int height, String caption) {
		Dimension size = new Dimension(width, height);
		canvas = new GLCanvas(size, this);
		frame = new GLFrame(size, caption, canvas, this);
		try {
			Display.setParent(canvas);
			Display.setVSyncEnabled(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Centers the Display on the Default GraphicsDevice.
	 */
	public void centerOnDefaultDisplay() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		if (ge == null) {
			return;
		}
		centerOnGraphicsDevice(ge.getDefaultScreenDevice());
	}

	/**
	 * Centers the Display on the given GraphicsDevice.
	 * If you don't know the GraphicsDevice to center
	 * the Display to, use centerOnDefaultDisplay();
	 * 
	 * @param gd the GraphicsDevice to center the display to.
	 */
	public void centerOnGraphicsDevice(GraphicsDevice gd) {
		Dimension displayDim = getSizeOfGraphicsDevice(gd);
		Point destiny = new Point(
				(displayDim.width - canvas.getSize().width) / 2,
				(displayDim.height - canvas.getSize().height) / 2);
		frame.setLocation(destiny);
	}

	/**
	 * Prints Simple DisplayDeviceInfo about all available 
	 * GraphicsDevice-s.
	 */
	public static void printDisplayDeviceInfo() {
		int displaynumb = UniAWTDisplay.getNumberOfDisplays();
		GraphicsDevice[] gds = UniAWTDisplay.getScreenDevices();
		for (int i = 0; i < displaynumb; i++) {
			Dimension size = UniAWTDisplay.getSizeOfGraphicsDevice(gds[i]);
			UniPrint.printf(
					"Display Number %d:\n >> Width: %d\n >> Height: %d\n", i,
					size.width, size.height);
		}
	}

	/**
	 * Has to be called with "true", after the Constructor, 
	 * if you want the Display to be shown.
	 * 
	 * @param visible whether the Display should be visible
	 * or not.
	 */
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
	
	public void setLocation(int x, int y) {
		frame.setLocation(x, y);
	}

	/**
	 * Sets whether the Display should print Debug info
	 * or not.
	 * 
	 * @param on whether to set it on, or not.
	 */
	public void setDebug(boolean on) {
		debug = true;
	}

	/**
	 * @return whether the frame's "X" has been clicked,
	 * or not.
	 */
	public boolean isCloseRequested() {
		return frame.isCloseRequested();
	}

	/**
	 * @return whether the frame is activated or not.
	 */
	public boolean isActivated() {
		return frame.isActivated();
	}
	
	/**
	 * @return whether the frame is iconified or not.
	 */
	public boolean isIconified() {
		return frame.isIconified();
	}

	/**
	 * @return whether setDebug() was set to true or false.
	 */
	public boolean isDebugEnabled() {
		return debug;
	}

	/**
	 * @return all available Screen Devices.
	 */
	public static GraphicsDevice[] getScreenDevices() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		if (ge == null) {
			return null;
		}
		return ge.getScreenDevices();
	}

	/**
	 * Returns the size of the given GraphicsDevice with 
	 * a Dimension.
	 * @param gd the GraphicsDevice to get the size of.
	 * @return the size.
	 */
	public static Dimension getSizeOfGraphicsDevice(GraphicsDevice gd) {
		Dimension d = new Dimension();
		DisplayMode dm = gd.getDisplayMode();
		d.setSize(dm.getWidth(), dm.getHeight());
		return d;
	}

	/**
	 * Equals:
	 * getScreenDevices().length.
	 * (With some error handling, you should prefer this)
	 * @return the number of available Displays.
	 */
	public static int getNumberOfDisplays() {
		GraphicsDevice[] devices = getScreenDevices();
		if (devices == null) {
			return 0;
		}
		return devices.length;
	}
	
	/**
	 * Do not call (does nothing bad ^^)
	 * This is only for printing via UniPrint
	 * (Inherited from UniPrintable)
	 */
	public String getClassName() {
		return getClass().getSimpleName();
	}

	/**
	 * @return the current size of the Display's Canvas.
	 */
	public Dimension getSize() {
		return canvas.getSize();
	}
	
	/**
	 * @return the width of the Display's canvas.
	 */
	public int getWidth() {
		return getSize().width;
	}
	
	/**
	 * @return the height of the Display's canvas.
	 */
	public int getHeight() {
		return getSize().height;
	}
	
	/**
	 * @return the current Position of the Display.
	 */
	public Point getWindowPos() {
		return frame.getLocation();
	}

	/**
	 * Sets the caption to the given String.
	 * @param caption the String to set the caption to.
	 */
	public void setCaption(String caption) {
		frame.setTitle(caption);
	}
	
	public int getBPP() {
		return frame.getColorModel().getPixelSize()/8;
	}
	
	/**
	 * Call this always after you rendered your scene
	 * and want to Draw that to the Display
	 * @see org.lwjgl.opengl.Display.update()
	 */
	public void update() {
		Display.update();
	}

	/**
	 * Destroys the Display and disposes the AWT Frame.
	 * @see org.lwjgl.opengl.Display.destroy();
	 */
	public void destroy() {
		Display.destroy();
		frame.dispose();
	}

	private class GLFrame extends Frame implements WindowListener,
			WindowFocusListener {
		private static final long serialVersionUID = -2063456624978688648L;

		private Canvas canvas;
		private boolean closeRequested;
		private boolean activated;
		private boolean iconified;
		private UniAWTDisplay father;

		GLFrame(Dimension size, String caption, Canvas canvas, UniAWTDisplay father) {
			super(caption);
			this.father = father;
			// setPreferredSize(size);
			// setSize(size);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Point location = new Point((screenSize.width - size.width) / 2,
					(screenSize.height - size.height) / 2);
			setLocation(location);
			
			addWindowListener(this);
			addWindowFocusListener(this);
			
			this.canvas = canvas;
			setLayout(new BorderLayout());
			add(canvas, BorderLayout.CENTER);
			
			pack();
		}

		public boolean isCloseRequested() {
			return closeRequested;
		}

		public boolean isActivated() {
			return activated;
		}
		
		public boolean isIconified() {
			return iconified;
		}

		// WindowListener

		public void windowActivated(WindowEvent e) {
			activated = true;
			if (father.isDebugEnabled())
				UniPrint.printf("Window Activated\n");
		}

		public void windowClosed(WindowEvent e) {
			if (father.isDebugEnabled()) UniPrint.printf("Window Closed\n");
		}

		public void windowClosing(WindowEvent e) {
			closeRequested = true;
			if (father.isDebugEnabled()) UniPrint.printf("Window Closing\n");
		}

		public void windowDeactivated(WindowEvent e) {
			activated = false;
			if (father.isDebugEnabled())
				UniPrint.printf("Window Deactivated\n");
		}

		public void windowDeiconified(WindowEvent e) {
			iconified = false;
			if (father.isDebugEnabled())
				UniPrint.printf("Window Deiconified\n");
		}

		public void windowIconified(WindowEvent e) {
			iconified = true;
			if (father.isDebugEnabled())
				UniPrint.printf("Window Iconified\n");
		}

		public void windowOpened(WindowEvent e) {
			if (father.isDebugEnabled()) UniPrint.printf("Window Opened\n");
		}

		// WindowFocusListener

		public void windowGainedFocus(WindowEvent e) {
			canvas.requestFocusInWindow();
			if (father.isDebugEnabled())
				UniPrint.printf("Window gained Focus\n");
		}

		public void windowLostFocus(WindowEvent e) {
			if (father.isDebugEnabled())
				UniPrint.printf("Window lost Focus\n");
		}

	}

	private class GLCanvas extends Canvas implements ComponentListener {
		private static final long serialVersionUID = -1546123056510296785L;

		private UniAWTDisplay father;

		GLCanvas(Dimension size, UniAWTDisplay father) {
			super();
			this.father = father;
			setSize(size);
			addComponentListener(this);
		}

		public void componentHidden(ComponentEvent e) {
			if (father.isDebugEnabled())
				UniPrint.printf("Component hidden\n");
		}

		public void componentMoved(ComponentEvent e) {
			if (father.isDebugEnabled())
				UniPrint.printf("Component moved\n");
		}

		public void componentResized(ComponentEvent e) {
			if (father.isDebugEnabled())
				UniPrint.printf("Component resized\n");
		}

		public void componentShown(ComponentEvent e) {
			if (father.isDebugEnabled())
				UniPrint.printf("Component shown\n");
		}

	}

}
