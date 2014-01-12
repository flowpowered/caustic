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
package org.spout.renderer.lwjgl.gl32;

import org.lwjgl.opengl.GL30;

import org.spout.renderer.api.gl.RenderBuffer;
import org.spout.renderer.lwjgl.LWJGLUtil;

/**
 * An OpenGL 3.2 implementation of {@link RenderBuffer}.
 *
 * @see RenderBuffer
 */
public class GL32RenderBuffer extends RenderBuffer {
    protected GL32RenderBuffer() {
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
        id = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
        // Set the storage format and size
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, format.getGLConstant(), width, height);
        // Unbind the render buffer
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        // Update the state
        super.create();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Unbind and delete the render buffer
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        GL30.glDeleteRenderbuffers(id);
        // Update state
        super.destroy();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void bind() {
        checkCreated();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, id);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void unbind() {
        checkCreated();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL30;
    }
}
