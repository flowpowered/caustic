package org.spout.renderer.gl20;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.spout.renderer.GLVersion;
import org.spout.renderer.util.RenderUtil;
import org.spout.renderer.util.TextureAtlas;

/**
 * @author thehutch
 */
public class OpenGL20TextureAtlas extends TextureAtlas {

	@Override
	public void create() {
		//TODO
		super.create();
	}

	@Override
	public void destroy() {
		//TODO
		super.destroy();
	}

	@Override
	public void bind() {
		checkCreated();
		// Activate the unit
		GL13.glActiveTexture(unit);
		// Bind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void unbind() {
		checkCreated();
		// Activate the unit
		GL13.glActiveTexture(unit);
		// Unbind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}