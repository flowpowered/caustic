/*
 * This file is part of Caustic LWJGL, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
