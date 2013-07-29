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
package org.spout.renderer.gl20;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import org.spout.renderer.GLVersion;
import org.spout.renderer.VertexArray;
import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.util.RenderUtil;

/**
 * Represents an OpenGL 2.0 vertex array. Since core OpenGL doesn't actually support vertex array
 * objects until 3.0, this class doesn't use the that, but it does use the vertex array methods
 * available to define the attributes, just not individually for an array. Thus, they have to be
 * redefined on each render call. Basically, it's like if there was only one vao available. After
 * constructing it, set the vertex data source with {@link #setVertexData(org.spout.renderer.data.VertexData)}.
 * It can then be created in the OpenGL context with {@link #create()}. To dispose of it, use {@link
 * #destroy()}.
 */
public class OpenGL20VertexArray extends VertexArray {
	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("VertexArray has already been created");
		}
		if (vertexData == null) {
			throw new IllegalStateException("Vertex data has not been set");
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
		attributeBufferIDs = new int[vertexData.getAttributeCount()];
		// For each attribute, generate, bind and fill the vbo,
		// no vao setup here, this is done during the render call
		for (int i = 0; i < vertexData.getAttributeCount(); i++) {
			final int bufferID = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData.getAttribute(i).getBuffer(), GL15.GL_STATIC_DRAW);
			attributeBufferIDs[i] = bufferID;
		}
		// Unbind the last vbo
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// Update state
		super.create();
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Unbind any bound buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// Unbind and delete indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(indicesBufferID);
		// Delete the attribute buffers
		for (int i = 0; i < vertexData.getAttributeCount(); i++) {
			GL15.glDeleteBuffers(attributeBufferIDs[i]);
		}
		// Reset the data and state
		indicesBufferID = 0;
		attributeBufferIDs = null;
		super.destroy();
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void draw() {
		checkCreated();
		// Bind and enable the vertex attributes
		for (int i = 0; i < vertexData.getAttributeCount(); i++) {
			// Bind the buffer
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, attributeBufferIDs[i]);
			// Define the attribute
			final VertexAttribute attribute = vertexData.getAttribute(i);
			GL20.glVertexAttribPointer(i, attribute.getSize(), attribute.getType().getGLConstant(), attribute.getUploadMode().normalize(), 0, 0);
			// Enable it
			GL20.glEnableVertexAttribArray(i);
		}
		// Unbind the last buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// Bind the indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		// Draw all indices with the provided mode
		GL11.glDrawElements(drawingMode.getGLConstant(), indicesCount, GL11.GL_UNSIGNED_INT, indicesOffset * DataType.INT.getByteSize());
		// Unbind the indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		// Disable all attributes
		for (int i = 0; i < vertexData.getAttributeCount(); i++) {
			GL20.glDisableVertexAttribArray(i);
		}
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
