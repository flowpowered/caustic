/*
 * This file is part of Caustic LWJGL.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic LWJGL is licensed under the Spout License Version 1.
 *
 * Caustic LWJGL is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic LWJGL is distributed in the hope that it will be useful, but WITHOUT ANY
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
package com.flowpowered.caustic.lwjgl.gl30;

import org.lwjgl.opengl.GL30;

import com.flowpowered.caustic.api.gl.RenderBuffer;
import com.flowpowered.caustic.api.gl.Texture.InternalFormat;
import com.flowpowered.caustic.lwjgl.LWJGLUtil;

/**
 * An OpenGL 3.0 implementation of {@link RenderBuffer}.
 *
 * @see RenderBuffer
 */
public class GL30RenderBuffer extends RenderBuffer {
    // The render buffer storage format
    private InternalFormat format;
    // The storage dimensions
    private int width = 1;
    private int height = 1;

    @Override
    public void create() {
        checkNotCreated();
        // Generate the render buffer
        id = GL30.glGenRenderbuffers();
        // Update the state
        super.create();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Delete the render buffer
        GL30.glDeleteRenderbuffers(id);
        // Update state
        super.destroy();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setStorage(InternalFormat format, int width, int height) {
        checkCreated();
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than zero");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero");
        }
        this.format = format;
        this.width = width;
        this.height = height;
        // Bind the render buffer
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
        // Set the storage format and size
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, format.getGLConstant(), width, height);
        // Unbind the render buffer
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public InternalFormat getFormat() {
        return format;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void bind() {
        checkCreated();
        // Bind the render buffer
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void unbind() {
        checkCreated();
        // Unbind the render buffer
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL30;
    }
}
