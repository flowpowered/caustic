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

import org.spout.renderer.Material;
import org.spout.renderer.Model;
import org.spout.renderer.gl20.OpenGL20Material;

/**
 * Represents a model for OpenGL 3.0. After constructing a new model, use {@link #getVertexData()}
 * to add data and specify the rendering indices. Next, specify the material with {@link
 * #setMaterial(org.spout.renderer.Material)}. Then use {@link #create()} to create model in the
 * current OpenGL context. It can now be added to the {@link OpenGL30Renderer}. Use {@link
 * #destroy()} to free the model's OpenGL resources. This doesn't delete the mesh. Make sure you add
 * the mesh before creating the model.
 */
public class OpenGL30Model extends Model {
	private final OpenGL30VertexArray vertexArray = new OpenGL30VertexArray();
	private OpenGL20Material material;

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Model has already been created");
		}
		if (material == null) {
			throw new IllegalStateException("Material has not been set");
		}
		vertexArray.setVertexData(vertices);
		vertexArray.create();
		super.create();
	}

	@Override
	public void destroy() {
		if (!created) {
			throw new IllegalStateException("Model has not been created yet");
		}
		vertexArray.destroy();
		super.destroy();
	}

	@Override
	protected void render() {
		if (!material.isCreated()) {
			throw new IllegalStateException("Material has not been created yet");
		}
		uniforms.getMatrix4("modelMatrix").set(getMatrix());
		material.getProgram().upload(uniforms);
		vertexArray.render(mode);
	}

	@Override
	public OpenGL20Material getMaterial() {
		return material;
	}

	@Override
	public void setMaterial(Material material) {
		// TODO: OpenGL30Material
		this.material = (OpenGL20Material) material;
	}
}
