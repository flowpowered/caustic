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
import org.spout.renderer.gl.Material;
import org.spout.renderer.gl.Model;
import org.spout.renderer.gl.VertexArray;
import org.spout.renderer.util.RenderUtil;

/**
 * An OpenGL 2.0 implementation of {@link Model}.
 *
 * @see Model
 */
public class OpenGL20Model extends Model {
	private OpenGL20VertexArray vertexArray;
	private OpenGL20Material material;

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Model has already been created");
		}
		if (vertexArray == null) {
			throw new IllegalStateException("Vertex array has not been set");
		}
		if (material == null) {
			throw new IllegalStateException("Material has not been set");
		}
		vertexArray.checkCreated();
		material.checkCreated();
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		vertexArray = null;
		material = null;
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
	public OpenGL20VertexArray getVertexArray() {
		return vertexArray;
	}

	@Override
	public void setVertexArray(VertexArray vertexArray) {
		RenderUtil.checkVersion(this, vertexArray);
		this.vertexArray = (OpenGL20VertexArray) vertexArray;
	}

	@Override
	public OpenGL20Material getMaterial() {
		return material;
	}

	@Override
	public void setMaterial(Material material) {
		RenderUtil.checkVersion(this, material);
		this.material = (OpenGL20Material) material;
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
