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
package org.spout.renderer.gl30;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.spout.renderer.VertexData;
import org.spout.renderer.VertexData.VertexAttribute;
import org.spout.renderer.util.RenderUtil;

public class OpenGL30VertexArray {
	// State
	private boolean created = false;
	// ID
	private int id = 0;
	// Amount of indices to render
	private int renderingIndicesCount = 0;
	// Buffer IDs
	private int indicesBufferID = 0;
	private int[] attributeBufferIDs;

	public void create(VertexData vertices) {
		if (created) {
			throw new IllegalStateException("Vertex array has already been created.");
		}
		// Generate and bind the vao
		id = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(id);
		// Generate, bind and fill the indices vbo then unbind
		indicesBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, vertices.getIndicesBuffer(), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		// Save the count of indices to draw
		renderingIndicesCount = vertices.getIndicesCount();
		// create the map for attribute index to buffer ID
		attributeBufferIDs = new int[vertices.getAttributeCount()];
		// For each attribute, generate, bind and fill the vbo,
		// then setup the attribute in the vao and save the buffer ID for the index
		for (int i = 0; i <= vertices.getLastAttributeIndex(); i++) {
			final VertexAttribute attribute = vertices.getAttribute(i);
			final int bufferID = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attribute.getBuffer(), GL15.GL_STATIC_DRAW);
			// TODO: proper support for integers (normalized or not) and double
			GL20.glVertexAttribPointer(i, attribute.getSize(), attribute.getType().getGLConstant(), false, 0, 0);
			attributeBufferIDs[i] = bufferID;
		}
		// Unbind the vbo and vao
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		// Check for errors
		RenderUtil.checkForOpenGLError();
		// Update state
		created = true;
	}

	public void destroy() {
		if (!created) {
			throw new IllegalStateException("Vertex array has not been created yet.");
		}
		// Unbind any bound buffer
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		// Unbind and delete the indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(indicesBufferID);
		// Bind the vao for deletion
		GL30.glBindVertexArray(id);
		// Disable each attribute and delete its buffer
		for (int i = 0; i < attributeBufferIDs.length; i++) {
			GL20.glDisableVertexAttribArray(i);
			GL15.glDeleteBuffers(attributeBufferIDs[i]);
		}
		// Unbind the vao and delete it
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(id);
		// Reset the data and state
		id = 0;
		renderingIndicesCount = 0;
		created = false;
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	public void render(DrawMode mode) {
		// Bind the vao and enable all attributes
		GL30.glBindVertexArray(id);
		for (int i = 0; i < attributeBufferIDs.length; i++) {
			GL20.glEnableVertexAttribArray(i);
		}
		// Bind the indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		// Draw all indices with the provided mode
		GL11.glDrawElements(mode.getGLConstant(), renderingIndicesCount, GL11.GL_UNSIGNED_INT, 0);
		// Unbind the indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		// Disable all attributes and unbind the vao
		for (int i = 0; i < attributeBufferIDs.length; i++) {
			GL20.glDisableVertexAttribArray(i);
		}
		GL30.glBindVertexArray(0);
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	public static enum DrawMode {
		LINES(GL11.GL_LINES),
		TRIANGLES(GL11.GL_TRIANGLES);
		private final int constant;

		private DrawMode(int constant) {
			this.constant = constant;
		}

		public int getGLConstant() {
			return constant;
		}
	}
}
