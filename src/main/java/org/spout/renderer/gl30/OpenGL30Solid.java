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

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import org.spout.renderer.Model;
import org.spout.renderer.util.RenderUtil;

/**
 * Represents a model for OpenGL 3.2. It is made out of triangles. After constructing a new model,
 * use {@link #positions()} to add position data, {@link #normals()} to add normal data and {@link
 * #indices()} to specify the rendering indices. Then use {@link #create()} to create model in the
 * current OpenGL context. It can now be added to the {@link OpenGL30Renderer}. Use {@link
 * #destroy()} to free the model's OpenGL resources. This doesn't delete the mesh. Use {@link
 * #deleteMesh()} for that. Make sure you add the mesh before creating the model.
 */
public class OpenGL30Solid extends Model {
	// Vertex info
	private static final byte POSITION_COMPONENT_COUNT = 3;
	private static final byte NORMAL_COMPONENT_COUNT = 3;
	// Vertex data
	private final TFloatList positions = new TFloatArrayList();
	private final TFloatList normals = new TFloatArrayList();
	private final TIntList indices = new TIntArrayList();
	private int renderingIndicesCount;
	// OpenGL pointers
	private int vertexArrayID = 0;
	private int positionsBufferID = 0;
	private int normalsBufferID = 0;
	private int vertexIndexBufferID = 0;

	/**
	 * Creates the solid from it's mesh. It can now be rendered.
	 */
	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Solid has already been created.");
		}
		vertexIndexBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vertexIndexBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, RenderUtil.toBuffer(indices), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		renderingIndicesCount = indices.size();
		positionsBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, RenderUtil.toBuffer(positions), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		normalsBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalsBufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, RenderUtil.toBuffer(normals), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		vertexArrayID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionsBufferID);
		GL20.glVertexAttribPointer(0, POSITION_COMPONENT_COUNT, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, normalsBufferID);
		GL20.glVertexAttribPointer(1, NORMAL_COMPONENT_COUNT, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		created = true;
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Destroys the solid's resources. It can no longer be rendered.
	 */
	@Override
	public void destroy() {
		if (!created) {
			return;
		}
		deleteMesh();
		GL30.glBindVertexArray(vertexArrayID);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(positionsBufferID);
		GL15.glDeleteBuffers(normalsBufferID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vertexIndexBufferID);
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vertexArrayID);
		renderingIndicesCount = 0;
		created = false;
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Displays the current solid with the proper rotation and position to the render window.
	 */
	@Override
	protected void render() {
		GL30.glBindVertexArray(vertexArrayID);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vertexIndexBufferID);
		GL11.glDrawElements(GL11.GL_TRIANGLES, renderingIndicesCount, GL11.GL_UNSIGNED_INT, 0);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Deletes all the mesh generated so far.
	 */
	public void deleteMesh() {
		positions.clear();
		normals.clear();
		indices.clear();
	}

	/**
	 * Returns the list of indices used by OpenGL to pick the vertices to draw the object with in the
	 * correct order. Use it to add mesh data.
	 *
	 * @return The indices list
	 */
	public TIntList indices() {
		return indices;
	}

	/**
	 * Returns the list of vertex positions, which are the groups of three successive floats starting
	 * at 0 (x1, y1, z1, x2, y2, z2, x3, ...). Use it to add mesh data.
	 *
	 * @return The position list
	 */
	public TFloatList positions() {
		return positions;
	}

	/**
	 * Returns the list of vertex normals, which are the groups of three successive floats starting at
	 * 0 (x1, y1, z1, x2, y2, z2, x3, ...). Use it to add mesh data.
	 *
	 * @return The normal list
	 */
	public TFloatList normals() {
		return normals;
	}
}
