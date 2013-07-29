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

import org.spout.renderer.GLVersion;
import org.spout.renderer.Material;
import org.spout.renderer.Model;
import org.spout.renderer.util.RenderUtil;

/**
 * Represents a model for OpenGL 2.0. After constructing a new model, use {@link #getVertexData()}
 * to add data and specify the rendering indices. Next, specify the material with {@link
 * #setMaterial(org.spout.renderer.Material)}. Then use {@link #create()} to create model in the
 * current OpenGL context. It can now be added to the {@link org.spout.renderer.gl30.OpenGL30Renderer}.
 * Use {@link #destroy()} to free the model's OpenGL resources. This doesn't delete the mesh. Make
 * sure you add the mesh before creating the model.
 */
public class OpenGL20Model extends Model {
	private final OpenGL20VertexArray vertexArray = new OpenGL20VertexArray();
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
		checkCreated();
		vertexArray.destroy();
		super.destroy();
	}

	@Override
	public void uploadUniforms() {
		super.uploadUniforms();
		material.getProgram().upload(uniforms);
	}

	@Override
	public void render() {
		checkCreated();
		vertexArray.draw();
	}

	@Override
	public OpenGL20Material getMaterial() {
		return material;
	}

	@Override
	public OpenGL20VertexArray getVertexArray() {
		return vertexArray;
	}

	@Override
	public void setMaterial(Material material) {
		RenderUtil.checkVersions(this, material);
		this.material = (OpenGL20Material) material;
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
