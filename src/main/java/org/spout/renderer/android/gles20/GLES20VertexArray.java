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
package org.spout.renderer.android.gles20;

import android.opengl.GLES20;

import org.spout.renderer.android.AndroidUtil;
import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.gl.VertexArray;

/**
 * An OpenGLES 2.0 implementation of {@link org.spout.renderer.gl.VertexArray}. <p/> Vertex arrays will be used if the ARB or APPLE extension is supported by the hardware. Else, since core OpenGL
 * doesn't support them until 3.0, the vertex attributes will have to be redefined on each render call.
 *
 * @see org.spout.renderer.gl.VertexArray
 */
public class GLES20VertexArray extends VertexArray {
	private int[] attributeSizes;
	private int[] attributeTypes;
	private boolean[] attributeNormalizing;

	protected GLES20VertexArray() {
	}

	@Override
	public void create() {
		if (isCreated()) {
			throw new IllegalStateException("VertexArray has already been created");
		}
		if (vertexData == null) {
			throw new IllegalStateException("Vertex data has not been set");
		}

		// Generate, bind and fill the indices vbo then unbind
		int params[] = new int[1];
		GLES20.glGenBuffers(1, params, 0);
		id = params[0];
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		// GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexData.getIndicesBuffer(), GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		// Save the count of indices to draw
		indicesCountCache = vertexData.getIndicesCount();
		resetIndicesCountAndOffset();
		// Create the map for attribute index to buffer ID
		final int attributeCount = vertexData.getAttributeCount();
		attributeBufferIDs = new int[attributeCount];

		// If we don't have a vao, we have to save these manually
		attributeSizes = new int[attributeCount];
		attributeTypes = new int[attributeCount];
		attributeNormalizing = new boolean[attributeCount];

		// For each attribute, generate, bind and fill the vbo
		// Setup the vao if available
		for (int i = 0; i < attributeCount; i++) {
			final VertexAttribute attribute = vertexData.getAttribute(i);
			GLES20.glGenBuffers(1, params, 0);
			final int bufferID = params[0];
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, bufferID);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, attribute.getSize(), attribute.getData(), GLES20.GL_STATIC_DRAW);
			attributeBufferIDs[i] = bufferID;
			// Or as a float, normalized or not
			GLES20.glVertexAttribPointer(i, attribute.getSize(), attribute.getType().getGLConstant(), attribute.getUploadMode().normalize(), 0, 0);
		}
		// Unbind the last vbo
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		// Update state
		super.create();
		// Check for errors
		AndroidUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Unbind any bound buffer
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		// Unbind and delete indices buffer
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDeleteBuffers(1, new int[]{indicesBufferID}, 0);
		// Delete the attribute buffers
		for (int i = 0; i < attributeBufferIDs.length; i++) {
			// Disable the attribute
			GLES20.glDisableVertexAttribArray(i);
		}
		GLES20.glDeleteBuffers(attributeBufferIDs.length, attributeBufferIDs, 1);
		// Delete the attribute properties
		attributeSizes = null;
		attributeTypes = null;
		attributeNormalizing = null;
		super.destroy();
		// Check for errors
		AndroidUtil.checkForOpenGLError();
	}

	@Override
	public void draw() {
		checkCreated();
		// Enable the vertex attributes
		for (int i = 0; i < attributeBufferIDs.length; i++) {
			// Bind the buffer
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, attributeBufferIDs[i]);
			// Define the attribute
			GLES20.glVertexAttribPointer(i, attributeSizes[i], attributeTypes[i], attributeNormalizing[i], 0, 0);
			// Enable it
			GLES20.glEnableVertexAttribArray(i);
		}
		// Unbind the last buffer
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		// Bind the indices buffer
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		// Draw all indices with the provided mode
		GLES20.glDrawElements(drawingMode.getGLConstant(), indicesCount, GLES20.GL_UNSIGNED_INT, indicesOffset * DataType.INT.getByteSize());
		// Unbind the indices buffer
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		// Disable all attributes
		for (int i = 0; i < attributeBufferIDs.length; i++) {
			GLES20.glDisableVertexAttribArray(i);
		}
		// Check for errors
		AndroidUtil.checkForOpenGLError();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GLES20;
	}
}
