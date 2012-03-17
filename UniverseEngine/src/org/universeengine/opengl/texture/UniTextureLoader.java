package org.universeengine.opengl.texture;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

public final class UniTextureLoader {
	
	/**
	 * Load a Texture from the given Filepath.
	 * Equals loadTexture(loadBufferedImage(filepath));
	 * 
	 * @param filepath the Filepath to load the Texture data from. 
	 * @return the UniTexture Instance.
	 */
	public static UniTexture loadTexture(String filepath) {
		return loadTexture(loadBufferedImage(filepath));
	}
	
	public static UniTexture loadTexture(BufferedImage img) {
		UniTexture tex = null;
		try {
			Texture slickTex = BufferedImageUtil.getTexture("", img);
			tex = new UniTexture(
					slickTex.getTextureID(),
					slickTex.getTextureWidth(),
					slickTex.getTextureHeight());
		} catch(IOException e) {
		}
		return tex;
	}

	public static int uploadTexture(BufferedImage img) {
		try {
			return BufferedImageUtil.getTexture("", img).getTextureID();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static BufferedImage loadBufferedImage(String path) {
		BufferedImage copy = null;
		try {
			GraphicsConfiguration gc = 
					GraphicsEnvironment.getLocalGraphicsEnvironment().
					getDefaultScreenDevice().getDefaultConfiguration();
			BufferedImage buffImg = ImageIO.read(new File(path));
			copy = gc.createCompatibleImage(buffImg.getWidth(), buffImg.getHeight(), buffImg.getTransparency());
			Graphics2D g2d = copy.createGraphics();
			g2d.drawImage(buffImg, 0, 0, null);
			g2d.dispose();
		} catch (IOException e) {
			System.err.printf("UniTextureLoader:\n" +
					" >> Failed to load BufferedImage from %s\n", path);
		}
		return copy;
	}

}
