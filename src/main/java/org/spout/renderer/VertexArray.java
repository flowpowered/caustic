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
package org.spout.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;

import org.spout.renderer.data.VertexData;

/**
 * Represent an OpenGL vertex array. The vertex data must be set with {@link
 * #setVertexData(org.spout.renderer.data.VertexData)} before it can be created.
 */
public abstract class VertexArray extends Creatable implements GLVersioned {
	protected int id = 0;
	// Buffers IDs
	protected int indicesBufferID = 0;
	protected int[] attributeBufferIDs;
	// Amount of indices to render
	protected int indicesCountCache;
	protected int indicesCount = 0;
	// First and last index to render
	protected int indicesOffset = 0;
	// Vertex attributes
	protected VertexData vertexData;
	// Drawing mode
	protected DrawingMode drawingMode = DrawingMode.TRIANGLES;

	@Override
	public void destroy() {
		id = 0;
		indicesCountCache = 0;
		resetIndicesCountAndOffset();
		vertexData = null;
		super.destroy();
	}

	/**
	 * Draws the vertex data to the screen.
	 */
	public abstract void draw();

	/**
	 * Sets the vertex data source to use.
	 *
	 * @param vertexData The vertex data source
	 */
	public void setVertexData(VertexData vertexData) {
		this.vertexData = vertexData;
	}

	/**
	 * Gets the ID for this vertex array as assigned by OpenGL.
	 *
	 * @return The ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the model's drawing mode.
	 *
	 * @param mode The drawing mode to use
	 */
	public void setDrawingMode(DrawingMode mode) {
		this.drawingMode = mode;
	}

	/**
	 * Sets the number of indices to render during each draw call.
	 *
	 * @param count The number of indices
	 */
	public void setIndicesCount(int count) {
		this.indicesCount = count;
	}

	/**
	 * Sets the offset in the indices buffer to start at when rendering.
	 *
	 * @param offset The offset in the indices buffer
	 */
	public void setIndicesOffset(int offset) {
		this.indicesOffset = offset;
	}

	/**
	 * Resets the indices count to the full count and the offset to zero.
	 */
	public void resetIndicesCountAndOffset() {
		indicesCount = indicesCountCache;
		indicesOffset = 0;
	}

	/**
	 * Represents the different drawing modes for the model
	 */
	public static enum DrawingMode {
		POINTS(GL11.GL_POINTS),
		LINE_STRIP(GL11.GL_LINE_STRIP),
		LINE_LOOP(GL11.GL_LINE_LOOP),
		LINES(GL11.GL_LINES),
		LINE_STRIP_ADJACENCY(GL32.GL_LINE_STRIP_ADJACENCY),
		LINES_ADJACENCY(GL32.GL_LINES_ADJACENCY),
		TRIANGLES_STRIP(GL11.GL_TRIANGLE_STRIP),
		TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN),
		TRIANGLES(GL11.GL_TRIANGLES),
		TRIANGLE_STRIP_ADJACENCY(GL32.GL_TRIANGLE_STRIP_ADJACENCY),
		TRIANGLES_ADJACENCY(GL32.GL_TRIANGLES_ADJACENCY),
		PATCHES(GL40.GL_PATCHES);
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
