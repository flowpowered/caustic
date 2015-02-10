/*
 * This file is part of Caustic API, licensed under the MIT License (MIT).
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
package com.flowpowered.caustic.api.gl;

import com.flowpowered.caustic.api.Creatable;
import com.flowpowered.caustic.api.GLVersioned;
import com.flowpowered.caustic.api.gl.Texture.InternalFormat;

/**
 * Represents an OpenGL render buffer. A render buffer can be used as a faster alternative to a texture in a frame buffer when its rendering output doesn't need to be read. The storage format, width
 * and height dimensions need to be set with {@link #setStorage(com.flowpowered.caustic.api.gl.Texture.InternalFormat, int, int)}, before the render buffer can be used.
 */
public abstract class RenderBuffer extends Creatable implements GLVersioned {
    protected int id;

    @Override
    public void destroy() {
        id = 0;
        super.destroy();
    }

    /**
     * Sets the render buffer storage.
     *
     * @param format The format
     * @param width The width
     * @param height The height
     */
    public abstract void setStorage(InternalFormat format, int width, int height);

    /**
     * Returns the render buffer format.
     *
     * @return The format
     */
    public abstract InternalFormat getFormat();

    /**
     * Returns the render buffer width.
     *
     * @return The width
     */
    public abstract int getWidth();

    /**
     * Returns the render buffer height.
     *
     * @return The height
     */
    public abstract int getHeight();

    /**
     * Binds the render buffer to the OpenGL context.
     */
    public abstract void bind();

    /**
     * Unbinds the render buffer from the OpenGL context.
     */
    public abstract void unbind();

    /**
     * Gets the ID for this render buffer as assigned by OpenGL.
     *
     * @return The ID
     */
    public int getID() {
        return id;
    }
}
