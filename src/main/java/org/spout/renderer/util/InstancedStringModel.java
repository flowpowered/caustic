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
package org.spout.renderer.util;

import org.spout.renderer.GLVersion;
import org.spout.renderer.gl.Material;
import org.spout.renderer.gl.Model;
import org.spout.renderer.gl.VertexArray;

/**
 * Represents an instance of another string model.
 *
 * @see InstancedModel
 */
public class InstancedStringModel extends Model {
	private final StringModel main;
	private String string;

	/**
	 * Constructs a new instanced model from the main model.
	 *
	 * @param main The main model
	 */
	public InstancedStringModel(StringModel main) {
		this.main = main;
	}

	@Override
	public void create() {
		if (!main.isCreated()) {
			main.create();
		}
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		if (main.isCreated()) {
			main.destroy();
		}
		super.destroy();
	}

	@Override
	public void uploadUniforms() {
		super.uploadUniforms();
		main.getMaterial().getProgram().upload(uniforms);
	}

	@Override
	public void render() {
		checkCreated();
		final String mainString = main.getString();
		main.setString(string);
		main.render();
		main.setString(mainString);
	}

	@Override
	public VertexArray getVertexArray() {
		return main.getVertexArray();
	}

	@Override
	public void setVertexArray(VertexArray vertexArray) {
		main.setVertexArray(vertexArray);
	}

	@Override
	public Material getMaterial() {
		return main.getMaterial();
	}

	@Override
	public void setMaterial(Material material) {
		RenderUtil.checkVersion(main, material);
		main.setMaterial(material);
	}

	public void setString(String string) {
		this.string = string;
	}

	@Override
	public GLVersion getGLVersion() {
		return main.getGLVersion();
	}
}
