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

import org.lwjgl.opengl.APPLEVertexArrayObject;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.gl.VertexArray;
import org.spout.renderer.lwjgl.LWJGLUtil;

/**
 * An OpenGL 2.0 implementation of {@link VertexArray}. <p/> Vertex arrays will be used if the ARB or APPLE extension is supported by the hardware. Else, since core OpenGL doesn't support them until
 * 3.0, the vertex attributes will have to be redefined on each render call.
 *
 * @see VertexArray
 */
public class GL20VertexArray extends VertexArray {
    private final VertexArrayExtension extension;
    private int[] attributeSizes;
    private int[] attributeTypes;
    private boolean[] attributeNormalizing;

    protected GL20VertexArray() {
        final ContextCapabilities capabilities = GLContext.getCapabilities();
        if (capabilities.GL_ARB_vertex_array_object) {
            extension = VertexArrayExtension.ARB;
        } else if (capabilities.GL_APPLE_vertex_array_object) {
            extension = VertexArrayExtension.APPLE;
        } else {
            extension = VertexArrayExtension.NONE;
        }
    }

    @Override
    public void create() {
        if (isCreated()) {
            throw new IllegalStateException("VertexArray has already been created");
        }
        if (vertexData == null) {
            throw new IllegalStateException("Vertex data has not been set");
        }
        if (extension.has()) {
            // Generate and bind the vao
            id = extension.glGenVertexArrays();
            extension.glBindVertexArray(id);
        }
        // Generate, bind and fill the indices vbo then unbind
        indicesBufferID = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, vertexData.getIndicesBuffer(), GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        // Save the count of indices to draw
        indicesCountCache = vertexData.getIndicesCount();
        resetIndicesCountAndOffset();
        // Create the map for attribute index to buffer ID
        final int attributeCount = vertexData.getAttributeCount();
        attributeBufferIDs = new int[attributeCount];
        if (!extension.has()) {
            // If we don't have a vao, we have to save these manually
            attributeSizes = new int[attributeCount];
            attributeTypes = new int[attributeCount];
            attributeNormalizing = new boolean[attributeCount];
        }
        // For each attribute, generate, bind and fill the vbo
        // Setup the vao if available
        for (int i = 0; i < attributeCount; i++) {
            final VertexAttribute attribute = vertexData.getAttribute(i);
            final int bufferID = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attribute.getData(), GL15.GL_STATIC_DRAW);
            attributeBufferIDs[i] = bufferID;
            if (extension.has()) {
                // Or as a float, normalized or not
                GL20.glVertexAttribPointer(i, attribute.getSize(), attribute.getType().getGLConstant(), attribute.getUploadMode().normalize(), 0, 0);
            } else {
                // We save the properties for rendering
                attributeSizes[i] = attribute.getSize();
                attributeTypes[i] = attribute.getType().getGLConstant();
                attributeNormalizing[i] = attribute.getUploadMode().normalize();
            }
        }
        // Unbind the last vbo
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        // Update state
        super.create();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Unbind any bound buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        // Unbind and delete indices buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(indicesBufferID);
        if (extension.has()) {
            // Bind the vao for deletion
            extension.glBindVertexArray(id);
        }
        // Delete the attribute buffers
        for (int i = 0; i < attributeBufferIDs.length; i++) {
            if (extension.has()) {
                // Disable the attribute
                GL20.glDisableVertexAttribArray(i);
            }
            GL15.glDeleteBuffers(attributeBufferIDs[i]);
        }
        if (extension.has()) {
            // Unbind the vao and delete it
            extension.glBindVertexArray(0);
            extension.glDeleteVertexArrays(id);
        } else {
            // Delete the attribute properties
            attributeSizes = null;
            attributeTypes = null;
            attributeNormalizing = null;
        }
        super.destroy();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void draw() {
        checkCreated();
        if (extension.has()) {
            // Bind the vao and enable all attributes
            extension.glBindVertexArray(id);
        }
        // Enable the vertex attributes
        for (int i = 0; i < attributeBufferIDs.length; i++) {
            if (!extension.has()) {
                // Bind the buffer
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, attributeBufferIDs[i]);
                // Define the attribute
                GL20.glVertexAttribPointer(i, attributeSizes[i], attributeTypes[i], attributeNormalizing[i], 0, 0);
            }
            // Enable it
            GL20.glEnableVertexAttribArray(i);
        }
        if (!extension.has()) {
            // Unbind the last buffer
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
        // Bind the indices buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
        // Draw all indices with the provided mode
        GL11.glDrawElements(drawingMode.getGLConstant(), indicesCount, GL11.GL_UNSIGNED_INT, indicesOffset * DataType.INT.getByteSize());
        // Unbind the indices buffer
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        // Disable all attributes
        for (int i = 0; i < attributeBufferIDs.length; i++) {
            GL20.glDisableVertexAttribArray(i);
        }
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL20;
    }

    private static enum VertexArrayExtension {
        NONE,
        ARB,
        APPLE;

        private boolean has() {
            return this != NONE;
        }

        private int glGenVertexArrays() {
            switch (this) {
                case ARB:
                    return ARBVertexArrayObject.glGenVertexArrays();
                case APPLE:
                    return APPLEVertexArrayObject.glGenVertexArraysAPPLE();
                default:
                    return 0;
            }
        }

        private void glBindVertexArray(int array) {
            switch (this) {
                case ARB:
                    ARBVertexArrayObject.glBindVertexArray(array);
                    break;
                case APPLE:
                    APPLEVertexArrayObject.glBindVertexArrayAPPLE(array);
                    break;
            }
        }

        private void glDeleteVertexArrays(int array) {
            switch (this) {
                case ARB:
                    ARBVertexArrayObject.glDeleteVertexArrays(array);
                    break;
                case APPLE:
                    APPLEVertexArrayObject.glDeleteVertexArraysAPPLE(array);
                    break;
            }
        }
    }
}
