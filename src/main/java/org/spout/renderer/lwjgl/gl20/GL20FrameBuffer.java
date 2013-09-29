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
package org.spout.renderer.lwjgl.gl20;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Map.Entry;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.gl.RenderBuffer;
import org.spout.renderer.gl.Texture;
import org.spout.renderer.lwjgl.LWJGLUtil;
import org.spout.renderer.util.CausticUtil;

/**
 * An OpenGL 2.0 implementation of {@link FrameBuffer} using EXT.
 *
 * @see FrameBuffer
 */
public class GL20FrameBuffer extends FrameBuffer {

	/**
	 * Constructs a new frame buffer for OpenGL 2.0. If no EXT extension for frame buffers is available, an exception is thrown.
	 *
	 * @throws UnsupportedOperationException If the hardware doesn't support EXT frame buffers
	 */
	protected GL20FrameBuffer() {
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
			throw new UnsupportedOperationException("Frame buffers are not supported by this hardware");
		}
	}

	@Override
	public void create() {
		// Generate and bind the frame buffer
		id = EXTFramebufferObject.glGenFramebuffersEXT();
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, id);
		// Track the color attachments to output for later use
		final TIntSet outputBuffers = new TIntHashSet();
		// Attach the textures
		for (Entry<AttachmentPoint, Texture> entry : textures.entrySet()) {
			final AttachmentPoint point = entry.getKey();
			final Texture texture = entry.getValue();
			texture.checkCreated();
			EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, point.getGLConstant(), GL11.GL_TEXTURE_2D, texture.getID(), 0);
			if (point.isColor()) {
				outputBuffers.add(point.getGLConstant());
			}
		}
		// Attach the render buffers
		for (Entry<AttachmentPoint, RenderBuffer> entry : buffers.entrySet()) {
			final AttachmentPoint point = entry.getKey();
			final RenderBuffer buffer = entry.getValue();
			buffer.checkCreated();
			EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, point.getGLConstant(), EXTFramebufferObject.GL_RENDERBUFFER_EXT, buffer.getID());
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
			final IntBuffer buffer = CausticUtil.createIntBuffer(outputBuffers.size());
			final int[] outputBuffersArray = outputBuffers.toArray();
			// Sorting the array ensures that attachments are in order n, n + 1, n + 2...
			// This is important!
			Arrays.sort(outputBuffersArray);
			buffer.put(outputBuffersArray);
			buffer.flip();
			GL20.glDrawBuffers(buffer);
		}
		// Disable input buffers
		GL11.glReadBuffer(GL11.GL_NONE);
		// Check for success
		if (EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT) != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
			System.out.println(Integer.toHexString(EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT)));
			throw new IllegalStateException("Failed to create the frame buffer");
		}
		// Unbind the frame buffer
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		// Update the state
		super.create();
		// Check for errors
		LWJGLUtil.checkForGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Unbind and delete the frame buffer
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		EXTFramebufferObject.glDeleteFramebuffersEXT(id);
		// Release some resources
		textures.clear();
		buffers.clear();
		// Update the state
		super.destroy();
		// Check for errors
		LWJGLUtil.checkForGLError();
	}

	@Override
	public void bind() {
		checkCreated();
		// Bind the frame buffer
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, id);
		// Check for errors
		LWJGLUtil.checkForGLError();
	}

	@Override
	public void unbind() {
		checkCreated();
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		// Check for errors
		LWJGLUtil.checkForGLError();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
