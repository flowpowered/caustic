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

import org.spout.renderer.util.RenderUtil;

/**
 *
 */
public class InstancedModel extends Model {
	private final Model instance;

	public InstancedModel(Model instance) {
		this.instance = instance;
	}

	@Override
	public void create() {
		if (!instance.isCreated()) {
			instance.create();
		}
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		if (instance.isCreated()) {
			instance.destroy();
		}
		super.destroy();
	}

	@Override
	public void uploadUniforms() {
		super.uploadUniforms();
		instance.getMaterial().getProgram().upload(uniforms);
	}

	@Override
	public void render() {
		checkCreated();
		instance.render();
	}

	@Override
	public Material getMaterial() {
		return instance.getMaterial();
	}

	@Override
	public void setMaterial(Material material) {
		RenderUtil.checkVersions(instance, material);
		instance.setMaterial(material);
	}

	@Override
	public VertexArray getVertexArray() {
		return instance.getVertexArray();
	}

	@Override
	public GLVersion getGLVersion() {
		return instance.getGLVersion();
	}
}
