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
package org.spout.renderer.gl30;

import java.nio.IntBuffer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.GLVersion;
import org.spout.renderer.gl.RenderBuffer;
import org.spout.renderer.gl.Texture;
import org.spout.renderer.util.RenderUtil;

/**
 * An OpenGL 3.0 implementation of {@link FrameBuffer}.
 *
 * @see FrameBuffer
 */
public class OpenGL30FrameBuffer extends FrameBuffer {
	// The attached texture and render buffers
	private final Map<AttachmentPoint, OpenGL30Texture> textures = new EnumMap<>(AttachmentPoint.class);
	private final Map<AttachmentPoint, OpenGL30RenderBuffer> buffers = new EnumMap<>(AttachmentPoint.class);

	@Override
	public void create() {
		// Generate and bind the frame buffer
		id = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		// Track the color attachments to output for later use
		final TIntSet outputBuffers = new TIntHashSet();
		// Attach the textures
		for (Entry<AttachmentPoint, OpenGL30Texture> entry : textures.entrySet()) {
			final AttachmentPoint point = entry.getKey();
			final OpenGL30Texture texture = entry.getValue();
			texture.checkCreated();
			GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, point.getGLConstant(), texture.getID(), 0);
			if (point.isColor()) {
				outputBuffers.add(point.getGLConstant());
			}
		}
		// Attach the render buffers
		for (Entry<AttachmentPoint, OpenGL30RenderBuffer> entry : buffers.entrySet()) {
			final AttachmentPoint point = entry.getKey();
			final OpenGL30RenderBuffer buffer = entry.getValue();
			buffer.checkCreated();
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, point.getGLConstant(), GL30.GL_RENDERBUFFER, buffer.getID());
			if (point.isColor()) {
				outputBuffers.add(point.getGLConstant());
			}
		}
		// Set the output to the proper buffers
		if (outputBuffers.isEmpty()) {
			// No color to output
			GL20.glDrawBuffers(GL11.GL_NONE);
		} else {
			// Keep track of the buffers to output
			final IntBuffer buffer = BufferUtils.createIntBuffer(outputBuffers.size());
			buffer.put(outputBuffers.toArray());
			buffer.flip();
			GL20.glDrawBuffers(buffer);
		}
		// Check for success
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new IllegalStateException("Failed to create the frame buffer");
		}
		// Unbind the frame buffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		// Update the state
		super.create();
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Unbind and delete the frame buffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL30.glDeleteFramebuffers(id);
		// Destroy the textures and buffers
		for (OpenGL30Texture texture : textures.values()) {
			if (texture.isCreated()) {
				texture.destroy();
			}
		}
		for (OpenGL30RenderBuffer buffer : buffers.values()) {
			if (buffer.isCreated()) {
				buffer.destroy();
			}
		}
		// Release some resources
		textures.clear();
		buffers.clear();
		// Update the state
		super.destroy();
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void bind() {
		checkCreated();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void unbind() {
		checkCreated();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void attach(AttachmentPoint point, Texture texture) {
		RenderUtil.checkVersion(this, texture);
		buffers.remove(point);
		textures.put(point, (OpenGL30Texture) texture);
	}

	@Override
	public void attach(AttachmentPoint point, RenderBuffer buffer) {
		RenderUtil.checkVersion(this, buffer);
		textures.remove(point);
		buffers.put(point, (OpenGL30RenderBuffer) buffer);
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL30;
	}
}
