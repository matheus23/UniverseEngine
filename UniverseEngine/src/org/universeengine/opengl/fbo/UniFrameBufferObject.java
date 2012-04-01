package org.universeengine.opengl.fbo;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GLContext;
import org.universeengine.exceptions.UniGLVersionException;

public class UniFrameBufferObject {
	
	private int textureID;
	private int fboID;
	private int depthBufferID;
	
	public UniFrameBufferObject(int width, int height, boolean linear) throws UniGLVersionException {
		this(width, height, GL_RGBA8, GL_RGBA, linear);
	}
	
	public UniFrameBufferObject(int width, int height, int internalFormat, int format, boolean linear) throws UniGLVersionException {
		this(width, height, internalFormat, format, linear, linear);
	}
	
	public UniFrameBufferObject(int width, int height, int internalFormat, int format, boolean minLinear, boolean magLinear) throws UniGLVersionException {
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
			throw new UniGLVersionException("FrameBufferObject not supported with this GPU / Drivers");
		}
		fboID = glGenFramebuffersEXT();
		textureID = glGenTextures();
		depthBufferID = glGenRenderbuffersEXT();
		
		bind();
		
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minLinear ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magLinear ? GL_LINEAR : GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT, (ByteBuffer) null);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, textureID, 0);
		
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthBufferID);
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT24, width, height);
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, depthBufferID);
		
		checkCompleteness();
		
		unbind();
	}
	
	private void checkCompleteness() {
		int framebuffer = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
		switch ( framebuffer ) {
		case GL_FRAMEBUFFER_COMPLETE_EXT:
			break;
		case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
			throw new RuntimeException("FrameBuffer: " + fboID
					+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
		case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
			throw new RuntimeException("FrameBuffer: " + fboID
					+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
		case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
			throw new RuntimeException("FrameBuffer: " + fboID
					+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
		case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
			throw new RuntimeException("FrameBuffer: " + fboID
					+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
		case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
			throw new RuntimeException("FrameBuffer: " + fboID
				+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
		case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
			throw new RuntimeException("FrameBuffer: " + fboID
					+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
		default:
			throw new RuntimeException("Unexpected reply from glCheckFramebufferStatusEXT: " + framebuffer);
		}
	}
	
	public void bind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboID);
	}
	
	public void unbind() {
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
	public int getTextureID() {
		return textureID;
	}
	
	public int getFBOID() {
		return fboID;
	}
	
	public int getDepthBufferID() {
		return depthBufferID;
	}

}
