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
package org.spout.renderer.lwjgl.gl30;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.spout.renderer.api.data.VertexAttribute;
import org.spout.renderer.api.data.VertexAttribute.DataType;
import org.spout.renderer.api.data.VertexAttribute.UploadMode;
import org.spout.renderer.api.data.VertexData;
import org.spout.renderer.api.gl.VertexArray;
import org.spout.renderer.lwjgl.LWJGLUtil;

/**
 * An OpenGL 3.0 implementation of {@link VertexArray}.
 *
 * @see VertexArray
 */
public class GL30VertexArray extends VertexArray {
    private static final int[] EMPTY_ARRAY = {};
    // Buffers IDs
    private int indicesBufferID = 0;
    private int[] attributeBufferIDs = EMPTY_ARRAY;
    // Size of the attribute buffers
    private int[] attributeBufferSizes = EMPTY_ARRAY;
    // Amount of indices to render
    private int indicesCount = 0;
    private int indicesDrawCount = 0;
    // First and last index to render
    private int indicesOffset = 0;
    // Drawing mode
    private DrawingMode drawingMode = DrawingMode.TRIANGLES;

    @Override
    public void create() {
        checkNotCreated();
        // Generate the vao
        id = GL30.glGenVertexArrays();
        // Update state
        super.create();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Delete the indices buffer
        GL15.glDeleteBuffers(indicesBufferID);
        // Delete the attribute buffers
        for (int attributeBufferID : attributeBufferIDs) {
            GL15.glDeleteBuffers(attributeBufferID);
        }
        // Delete the vao
        GL30.glDeleteVertexArrays(id);
        // Reset the IDs and data
        indicesBufferID = 0;
        attributeBufferIDs = EMPTY_ARRAY;
        attributeBufferSizes = EMPTY_ARRAY;
        // Update the state
        super.destroy();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setData(VertexData vertexData) {
        checkCreated();
        // Generate a new indices buffer if we don't have one yet
        if (indicesBufferID == 0) {
            indicesBufferID = GL15.glGenBuffers();
        }
        // Bind the indices buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
        // Get the new count of indices
        final int newIndicesCount = vertexData.getIndicesCount();
        // If the new count is greater than or 50% smaller than the old one, we'll reallocate the memory
        // In the first case because we need more space, in the other to save space
        if (newIndicesCount > indicesCount || newIndicesCount <= indicesCount * 0.5) {
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, vertexData.getIndicesBuffer(), GL15.GL_STATIC_DRAW);
        } else {
            // Else, we replace the data with the new one, but we don't resize, so some old data might be left trailing in the buffer
            GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, vertexData.getIndicesBuffer());
        }
        // Unbind the indices buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        // Update the count to the new one
        indicesCount = newIndicesCount;
        indicesDrawCount = indicesCount;
        // Ensure that the indices offset and count fits inside the valid part of the buffer
        indicesOffset = Math.min(indicesOffset, indicesCount - 1);
        indicesDrawCount = Math.min(indicesDrawCount, indicesCount - indicesOffset);
        // Bind the vao
        GL30.glBindVertexArray(id);
        // Create a new array of attribute buffers ID of the correct size
        final int attributeCount = vertexData.getAttributeCount();
        final int[] newAttributeBufferIDs = new int[attributeCount];
        // Copy all the old buffer IDs that will fit in the new array so we can reuse them
        System.arraycopy(attributeBufferIDs, 0, newAttributeBufferIDs, 0, Math.min(attributeBufferIDs.length, newAttributeBufferIDs.length));
        // Delete any buffers that we don't need (new array is smaller than the previous one)
        for (int i = newAttributeBufferIDs.length; i < attributeBufferIDs.length; i++) {
            GL15.glDeleteBuffers(attributeBufferIDs[i]);
        }
        // Create new buffers if necessary (new array is larger than the previous one)
        for (int i = attributeBufferIDs.length; i < newAttributeBufferIDs.length; i++) {
            newAttributeBufferIDs[i] = GL15.glGenBuffers();
        }
        // Copy the old valid attribute buffer sizes
        final int[] newAttributeBufferSizes = new int[attributeCount];
        System.arraycopy(attributeBufferSizes, 0, newAttributeBufferSizes, 0, Math.min(attributeBufferSizes.length, newAttributeBufferSizes.length));
        // Upload the new vertex data
        for (int i = 0; i < attributeCount; i++) {
            final VertexAttribute attribute = vertexData.getAttribute(i);
            final ByteBuffer attributeData = attribute.getData();
            // Get the current buffer size
            final int bufferSize = newAttributeBufferSizes[i];
            // Get the new buffer size
            final int newBufferSize = attributeData.remaining();
            // Bind the target buffer
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, newAttributeBufferIDs[i]);
            // If the new count is greater than or 50% smaller than the old one, we'll reallocate the memory
            if (newBufferSize > bufferSize || newBufferSize <= bufferSize * 0.5) {
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attributeData, GL15.GL_STATIC_DRAW);
            } else {
                // Else, we replace the data with the new one, but we don't resize, so some old data might be left trailing in the buffer
                GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, attributeData);
            }
            // Update the buffer size to the new one
            newAttributeBufferSizes[i] = newBufferSize;
            // Next, we add the pointer to the data in the vao
            // We have three ways to interpret integer data
            if (attribute.getType().isInteger() && attribute.getUploadMode() == UploadMode.KEEP_INT) {
                // Directly as an int
                GL30.glVertexAttribIPointer(i, attribute.getSize(), attribute.getType().getGLConstant(), 0, 0);
            } else {
                // Or as a float, normalized or not
                GL20.glVertexAttribPointer(i, attribute.getSize(), attribute.getType().getGLConstant(), attribute.getUploadMode().normalize(), 0, 0);
            }
            // Finally enable the attribute
            GL20.glEnableVertexAttribArray(i);
        }
        // Unbind the last vbo
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        // Unbind the vao
        GL30.glBindVertexArray(0);
        // Update the attribute buffer IDs to the new ones
        attributeBufferIDs = newAttributeBufferIDs;
        // Update the attribute buffer sizes to the new ones
        attributeBufferSizes = newAttributeBufferSizes;
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setDrawingMode(DrawingMode drawingMode) {
        if (drawingMode == null) {
            throw new IllegalArgumentException("Drawing mode cannot be null");
        }
        this.drawingMode = drawingMode;
    }

    @Override
    public void setIndicesOffset(int offset) {
        indicesOffset = Math.min(offset, indicesCount - 1);
        indicesDrawCount = Math.min(indicesDrawCount, indicesCount - indicesOffset);
    }

    @Override
    public void setIndicesCount(int count) {
        if (count < 0) {
            indicesDrawCount = indicesCount;
        } else {
            indicesDrawCount = count;
        }
        indicesDrawCount = Math.min(indicesDrawCount, indicesCount - indicesOffset);
    }

    @Override
    public void draw() {
        checkCreated();
        // Bind the vao
        GL30.glBindVertexArray(id);
        // Bind the index buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
        // Draw all indices with the provided mode
        GL11.glDrawElements(drawingMode.getGLConstant(), indicesDrawCount, GL11.GL_UNSIGNED_INT, indicesOffset * DataType.INT.getByteSize());
        // Unbind the index buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        // Unbind the vao
        GL30.glBindVertexArray(0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL30;
    }
}
