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
package org.spout.renderer.api.gl;

import org.spout.renderer.api.Creatable;
import org.spout.renderer.api.GLVersioned;
import org.spout.renderer.api.data.VertexData;

/**
 * Represent an OpenGL vertex array. The vertex data must be set with {@link #setData(org.spout.renderer.api.data.VertexData)} before it can be created.
 */
public abstract class VertexArray extends Creatable implements GLVersioned {
    protected int id = 0;

    @Override
    public void destroy() {
        id = 0;
        super.destroy();
    }

    /**
     * Sets the vertex data source to use. The indices offset is kept but maybe reduced if it doesn't fit inside the new data. The count is set to the size from the offset to the end of the data.
     *
     * @param vertexData The vertex data source
     */
    public abstract void setData(VertexData vertexData);

    /**
     * Sets the model's drawing mode.
     *
     * @param mode The drawing mode to use
     */
    public abstract void setDrawingMode(DrawingMode mode);

    /**
     * Sets the starting offset in the indices buffer. Defaults to 0.
     *
     * @param offset The offset in the indices buffer
     */
    public abstract void setIndicesOffset(int offset);

    /**
     * Sets the number of indices to render during each draw call, starting at the offset set by {@link #setIndicesOffset(int)}. Setting this to a value smaller than zero results in rendering of the
     * whole list. If the value is larger than the list (starting at the offset), it will be maxed to that value.
     *
     * @param count The number of indices
     */
    public abstract void setIndicesCount(int count);

    /**
     * Draws the primitives defined by the vertex data.
     */
    public abstract void draw();

    /**
     * Gets the ID for this vertex array as assigned by OpenGL.
     *
     * @return The ID
     */
    public int getID() {
        return id;
    }

    /**
     * Represents the different drawing modes for the model
     */
    public static enum DrawingMode {
        POINTS(0x0), // GL11.GL_POINTS
        LINES(0x1), // GL11.GL_LINES
        LINE_LOOP(0x2), // GL11.GL_LINE_LOOP
        LINE_STRIP(0x3), // GL11.GL_LINE_STRIP
        TRIANGLES(0x4), // GL11.GL_TRIANGLES
        TRIANGLES_STRIP(0x5), // GL11.GL_TRIANGLE_STRIP
        TRIANGLE_FAN(0x7), // GL11.GL_TRIANGLE_FAN
        LINES_ADJACENCY(0xA), // GL32.GL_LINES_ADJACENCY
        LINE_STRIP_ADJACENCY(0xB), // GL32.GL_LINE_STRIP_ADJACENCY
        TRIANGLES_ADJACENCY(0xC), // GL32.GL_TRIANGLES_ADJACENCY
        TRIANGLE_STRIP_ADJACENCY(0xD), // GL32.GL_TRIANGLE_STRIP_ADJACENCY
        PATCHES(0xE); // GL40.GL_PATCHES
        private final int glConstant;

        private DrawingMode(int constant) {
            this.glConstant = constant;
        }

        /**
         * Returns the OpenGL constant associated to the drawing mode
         *
         * @return The OpenGL constant
         */
        public int getGLConstant() {
            return glConstant;
        }
    }
}
