/*
 * This file is part of Caustic Android.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic Android is licensed under the Spout License Version 1.
 *
 * Caustic Android is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic Android is distributed in the hope that it will be useful, but WITHOUT ANY
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

import org.spout.renderer.android.AndroidUtil;
import org.spout.renderer.api.gl.RenderBuffer;

/**
 * An OpenGLES 2.0 implementation of {@link org.spout.renderer.api.gl.RenderBuffer} using EXT.
 *
 * @see org.spout.renderer.api.gl.RenderBuffer
 */
public class GLES20RenderBuffer extends RenderBuffer {
    /**
     * Constructs a new render buffer for OpenGL 2.0. If no EXT extension for render buffers is available, an exception is thrown.
     *
     * @throws UnsupportedOperationException If the hardware doesn't support EXT render buffers.
     */
    protected GLES20RenderBuffer() {
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
        int[] bufferId = new int[1];
        GLES20.glGenRenderbuffers(1, bufferId, 0);
        id = bufferId[0];
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, id);
        // Set the storage format and size
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, format.getGLConstant(), width, height);
        // Unbind the render buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        // Update the state
        super.create();
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Unbind and delete the render buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glDeleteRenderbuffers(1, new int[]{id}, 0);
        // Update state
        super.destroy();
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void bind() {
        checkCreated();
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, id);
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void unbind() {
        checkCreated();
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GLES20;
    }
}
