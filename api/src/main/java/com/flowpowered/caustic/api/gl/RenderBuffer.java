/*
 * This file is part of Caustic API.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic API is licensed under the Spout License Version 1.
 *
 * Caustic API is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic API is distributed in the hope that it will be useful, but WITHOUT ANY
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
