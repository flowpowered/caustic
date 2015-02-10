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
import com.flowpowered.caustic.api.data.VertexData;

/**
 * Represent an OpenGL vertex array. The vertex data must be set with {@link #setData(com.flowpowered.caustic.api.data.VertexData)} before it can be created.
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
     * Sets the vertex array's drawing mode.
     *
     * @param mode The drawing mode to use
     */
    public abstract void setDrawingMode(DrawingMode mode);

    /**
     * Sets the vertex array's polygon mode. This describes how to rasterize each primitive. The default is {@link com.flowpowered.caustic.api.gl.VertexArray.PolygonMode#FILL}. This can be used to draw
     * only the wireframes of the polygons.
     *
     * @param mode The polygon mode
     */
    public abstract void setPolygonMode(PolygonMode mode);

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
     * Represents the different drawing modes for the vertex array
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
            glConstant = constant;
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

    /**
     * Represents the different polygon modes for the vertex array
     */
    public static enum PolygonMode {
        POINT(0x1B00), // GL11.GL_POINT
        LINE(0x1B01), // GL11.GL_LINE
        FILL(0x1B02); // GL11.GL_FILL
        private final int glConstant;

        private PolygonMode(int constant) {
            glConstant = constant;
        }

        /**
         * Returns the OpenGL constant associated to the polygon mode
         *
         * @return The OpenGL constant
         */
        public int getGLConstant() {
            return glConstant;
        }
    }
}
