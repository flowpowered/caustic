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

import org.spout.renderer.data.VertexData;

/**
 * Represent a vertex array for OpenGL. The {@link org.spout.renderer.Renderer} should always be
 * created before the vertex array. The vertex data source must be set with {@link
 * #setVertexData(org.spout.renderer.data.VertexData)} before it can be created.
 */
public class VertexArray extends Creatable {
	protected int id = -1;
	// Amount of indices to render
	protected int renderingIndicesCount = 0;
	// Vertex attributes
	protected VertexData vertexData;

	protected VertexArray() {
	}

	@Override
	public void destroy() {
		id = -1;
		renderingIndicesCount = 0;
		vertexData = null;
		super.destroy();
	}

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
}
