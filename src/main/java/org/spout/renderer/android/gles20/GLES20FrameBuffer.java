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
package org.spout.renderer.android.gles20;

import android.opengl.GLES20;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.spout.renderer.GLVersion;
import org.spout.renderer.android.AndroidUtil;
import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.gl.RenderBuffer;
import org.spout.renderer.gl.Texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An OpenGL 2.0 implementation of {@link org.spout.renderer.gl.FrameBuffer} using EXT.
 *
 * @see org.spout.renderer.gl.FrameBuffer
 */
public class GLES20FrameBuffer extends FrameBuffer {
	// The attached texture and render buffers
	private final Map<AttachmentPoint, GLES20Texture> textures = new EnumMap<>(AttachmentPoint.class);
	private final Map<AttachmentPoint, GLES20RenderBuffer> buffers = new EnumMap<>(AttachmentPoint.class);

	private int[] bufferId;
	/**
	 * Constructs a new frame buffer for OpenGL 2.0ES.
	 */
	public GLES20FrameBuffer() {
	}

	@Override
	public void create() {
		// Generate and bind the frame buffer
		GLES20.glGenFramebuffers(1, this.bufferId, 0);
		this.id = bufferId[0];
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id);
		// Track the color attachments to output for later use
		final TIntSet outputBuffers = new TIntHashSet();
		// Attach the textures
		for (Entry<AttachmentPoint, GLES20Texture> entry : textures.entrySet()) {
			final AttachmentPoint point = entry.getKey();
			final GLES20Texture texture = entry.getValue();
			texture.checkCreated();
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, point.getGLConstant(), GLES20.GL_TEXTURE_2D, texture.getID(), 0);
			if (point.isColor()) {
				outputBuffers.add(point.getGLConstant());
			}
		}
		// Attach the render buffers
		for (Entry<AttachmentPoint, GLES20RenderBuffer> entry : buffers.entrySet()) {
			final AttachmentPoint point = entry.getKey();
			final GLES20RenderBuffer buffer = entry.getValue();
			buffer.checkCreated();
			GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, point.getGLConstant(), GLES20.GL_RENDERBUFFER, buffer.getID());
			if (point.isColor()) {
				outputBuffers.add(point.getGLConstant());
			}
		}
		// Set the output to the proper buffers
		if (outputBuffers.isEmpty()) {
			// No color to output
			// TODO: Fix draw call for mobile: GLES20.glDrawBuffers(GL.GL_NONE);
		} else {
			// Keep track of the buffers to output
			final ByteBuffer buffer = ByteBuffer.allocateDirect(outputBuffers.size()).order(ByteOrder.nativeOrder());
			final int[] outputBuffersArray = outputBuffers.toArray();
			// Sorting the array ensures that attachments are in order n, n + 1, n + 2...
			// This is important!
			Arrays.sort(outputBuffersArray);
			for (int val : outputBuffersArray) {
				buffer.putInt(val);
			}
			buffer.flip();
			// TODO: Fix draw call for mobile: GLES20.glDrawBuffers(buffer);
		}
		// Check for success
		if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			throw new IllegalStateException("Failed to create the frame buffer");
		}
		// Unbind the frame buffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		// Update the state
		super.create();
		// Check for errors
		AndroidUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Unbind and delete the frame buffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glDeleteFramebuffers(1, new int[] {id}, 0);
		// Release some resources
		textures.clear();
		buffers.clear();
		// Update the state
		super.destroy();
		// Check for errors
		AndroidUtil.checkForOpenGLError();
	}

	@Override
	public void bind() {
		checkCreated();
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id);
		// Check for errors
		AndroidUtil.checkForOpenGLError();
	}

	@Override
	public void unbind() {
		checkCreated();
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		// Check for errors
		AndroidUtil.checkForOpenGLError();
	}

	@Override
	public void attach(AttachmentPoint point, Texture texture) {
		AndroidUtil.checkVersion(this, texture);
		buffers.remove(point);
		textures.put(point, (GLES20Texture) texture);
	}

	@Override
	public void attach(AttachmentPoint point, RenderBuffer buffer) {
		AndroidUtil.checkVersion(this, buffer);
		textures.remove(point);
		buffers.put(point, (GLES20RenderBuffer) buffer);
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GLES20;
	}
}
