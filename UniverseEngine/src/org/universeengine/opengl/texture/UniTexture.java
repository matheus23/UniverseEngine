package org.universeengine.opengl.texture;

import static org.lwjgl.opengl.GL11.*;

public class UniTexture {
	
	private int texID;
	private int w;
	private int h;
	
	/**
	 * Creates a new Texture "wrapper".
	 * Do not call this! Instead: use UniTextureLoader.
	 * 
	 * @param texturePointer the OpenGL's Texture ID.
	 * @param w width of the Texture.
	 * @param h height of the Texture.
	 */
	public UniTexture(int texturePointer, int w, int h) {
		this.texID = texturePointer;
		this.w = w;
		this.h = h;
	}
	
	/**
	 * @return the given Texture ID.
	 */
	public int getTexID() {
		return texID;
	}
	
	/**
	 * @return the Width of the Texture.
	 */
	public int getWidth() {
		return w;
	}
	
	/**
	 * @return the Height of the Texture.
	 */
	public int getHeight() {
		return h;
	}
	
	/**
	 * Binds the Texture with OpenGL Calls.
	 * Now the Texture will automatically be used
	 * with glTexCoord calls.
	 */
	public void bind() {
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, texID);
	}
	
	/**
	 * Preferred Way to unbind Textures.
	 * Calls glDisable(GL_TEXTURE_2D);
	 * You don't have to call glEnable before
	 * bind() the next time.
	 */
	public void unbind() {
		glDisable(GL_TEXTURE_2D);
	}
	
}
