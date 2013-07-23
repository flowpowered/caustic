/*
 * This file is part of Caustic.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic is licensed under the Spout License Version 1.
 *
 * Caustic is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.renderer.gl20;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;

import org.spout.renderer.Texture;
import org.spout.renderer.util.RenderUtil;

/**
 * Represents a texture for OpenGL20.
 */
public class OpenGL20Texture extends Texture {
	@Override
	public void create() {
		if (source == null) {
			throw new IllegalStateException("Texture source has not been set.");
		}
		if (minFilter == null || magFilter == null) {
			throw new IllegalStateException("Texture filters have not been set.");
		}
		if (wrapT == null || wrapS == null) {
			throw new IllegalStateException("Texture wraps have not been set.");
		}
		// Obtain the image raw int data
		final BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read(source);
			source.close();
		} catch (IOException ex) {
			throw new IllegalStateException("Unreadable texture image", ex);
		}
		final int width = bufferedImage.getWidth();
		final int height = bufferedImage.getHeight();
		int[] pixels = new int[width * height];
		bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);
		// Place the data in a buffer
		final ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				final int pixel = pixels[x + y * width];
				buffer.put((byte) (pixel >> 16 & 0xff));
				buffer.put((byte) (pixel >> 8 & 0xff));
				buffer.put((byte) (pixel & 0xff));
				buffer.put((byte) (pixel >> 24 & 0xff));
			}
		}
		buffer.flip();
		// Generate and bind the texture in the unit
		id = GL11.glGenTextures();
		GL13.glActiveTexture(unit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		// Set pixel storage mode
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		// Upload the texture to the GPU
		if (minFilter.isMipMap() || magFilter.isMipMap()) {
			// Build mipmaps if using mip mapped filters
			GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, GL11.GL_RGBA8, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		} else {
			// Else just make it a normal texture
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		}
		// Set the vertical and horizontal texture wrap
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapT.getGLConstant());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapS.getGLConstant());
		// Set the min and max texture filter
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter.getGLConstant());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter.getGLConstant());
		// Unbind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		// Release texture input stream
		source = null;
		super.create();
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Activate the unit
		GL13.glActiveTexture(unit);
		// Unbind the texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		// Delete the texture
		GL11.glDeleteTextures(id);
		// Reset the data
		id = 0;
		super.destroy();
		// Check for errors
		RenderUtil.checkForOpenGLError();
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
}
