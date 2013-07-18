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

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.lwjgl.opengl.GL30;

import org.spout.renderer.VertexData;

public class OpenGL30VertexArray {
	// State
	private boolean created = false;
	// ID
	private int id;
	//
	private final VertexData vertices;
	private final TObjectIntMap<String> buffers = new TObjectIntHashMap<>();

	public OpenGL30VertexArray(VertexData vertices) {
		this.vertices = vertices;
	}

	public void create() {
		if (created) {
			throw new IllegalStateException("Vertex array has already been created.");
		}
		id = GL30.glGenVertexArrays();
	}

	public void destroy() {
		if (!created) {
			throw new IllegalStateException("Vertex array has not been created yet.");
		}
		GL30.glDeleteVertexArrays(id);
		id = 0;
	}
}
