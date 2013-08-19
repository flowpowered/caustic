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

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GLContext;

import org.spout.renderer.GLVersion;
import org.spout.renderer.gl.RenderBuffer;
import org.spout.renderer.lwjgl.LWJGLUtil;

/**
 * An OpenGL 2.0 implementation of {@link RenderBuffer} using EXT.
 *
 * @see RenderBuffer
 */
public class GL20RenderBuffer extends RenderBuffer {
	/**
	 * Constructs a new render buffer for OpenGL 2.0. If no EXT extension for render buffers is available, an exception is thrown.
	 *
	 * @throws UnsupportedOperationException If the hardware doesn't support EXT render buffers.
	 */
	public GL20RenderBuffer() {
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
			throw new UnsupportedOperationException("Render buffers are not supported by this hardware");
		}
	}

	@Override
	public void create() {
		if (format == null) {
			throw new IllegalStateException("Format has not been set");
		}
		if (width == -1) {
			throw new IllegalStateException("Width has not been set");
		}
		if (height == -1) {
			throw new IllegalStateException("Height has not been set");
		}
		// Generate and bind the render buffer
		id = EXTFramebufferObject.glGenRenderbuffersEXT();
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, id);
		// Set the storage format and size
		EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, format.getGLConstant(), width, height);
		// Unbind the render buffer
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
		// Update the state
		super.create();
		// Check for errors
		LWJGLUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Unbind and delete the render buffer
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
		EXTFramebufferObject.glDeleteRenderbuffersEXT(id);
		// Update state
		super.destroy();
		// Check for errors
		LWJGLUtil.checkForOpenGLError();
	}

	@Override
	public void bind() {
		checkCreated();
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, id);
		// Check for errors
		LWJGLUtil.checkForOpenGLError();
	}

	@Override
	public void unbind() {
		checkCreated();
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
		// Check for errors
		LWJGLUtil.checkForOpenGLError();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
