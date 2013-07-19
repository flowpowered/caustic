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

import org.spout.renderer.Model;
import org.spout.renderer.VertexData;
import org.spout.renderer.gl30.OpenGL30VertexArray.DrawMode;

/**
 * Represents a model for OpenGL 3.2. After constructing a new model, use {@link #getVertexData()}
 * to add data and specify the rendering indices. Then use {@link #create()} to create model in the
 * current OpenGL context. It can now be added to the {@link OpenGL30Renderer}. Use {@link
 * #destroy()} to free the model's OpenGL resources. This doesn't delete the mesh. Make sure you add
 * the mesh before creating the model.
 */
public class OpenGL30Model extends Model {
	// Vertex data
	private final VertexData vertices = new VertexData();
	private final OpenGL30VertexArray vertexArray = new OpenGL30VertexArray();
	// Drawing mode
	private DrawMode mode = DrawMode.TRIANGLES;

	public OpenGL30Model() {
		// TODO: remove these
		vertices.addFloatAttribute("positions", 3);
		vertices.addFloatAttribute("normals", 3);
	}

	/**
	 * Creates the solid from its mesh. It can now be rendered.
	 */
	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Solid has already been created.");
		}
		vertexArray.create(vertices);
		created = true;
	}

	/**
	 * Destroys the solid's resources. It can no longer be rendered.
	 */
	@Override
	public void destroy() {
		if (!created) {
			return;
		}
		vertexArray.destroy();
		created = false;
	}

	/**
	 * Displays the current solid with the proper rotation and position to the render window.
	 */
	@Override
	protected void render() {
		vertexArray.render(mode);
	}

	/**
	 * Returns the list of vertex positions, which are the groups of three successive floats starting
	 * at 0 (x1, y1, z1, x2, y2, z2, x3, ...). Use it to add mesh data.
	 * <p/>
	 * Returns the list of vertex normals, which are the groups of three successive floats starting at
	 * 0 (x1, y1, z1, x2, y2, z2, x3, ...). Use it to add mesh data.
	 *
	 * @return The position list
	 */
	public VertexData getVertexData() {
		return vertices;
	}

	public void clearVertexData() {
		vertices.clear();
	}

	public DrawMode getDrawMode() {
		return mode;
	}

	public void setDrawMode(DrawMode mode) {
		this.mode = mode;
	}
}
