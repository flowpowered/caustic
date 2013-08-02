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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.spout.renderer.GLVersion;
import org.spout.renderer.gl.Material;
import org.spout.renderer.gl.Texture;
import org.spout.renderer.util.RenderUtil;

/**
 * An OpenGL 2.0 implementation of {@link Material}.
 *
 * @see Material
 */
public class OpenGL20Material extends Material {
	private final OpenGL20Program program = new OpenGL20Program();
	// Textures by unit
	protected TIntObjectMap<OpenGL20Texture> textures;

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Material has already been created");
		}
		program.create();
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		program.destroy();
		if (textures != null) {
			for (OpenGL20Texture texture : textures.valueCollection()) {
				if (texture.isCreated()) {
					texture.destroy();
				}
			}
		}
		uniforms.clear();
		super.destroy();
	}

	@Override
	public void bind() {
		checkCreated();
		program.bind();
		if (textures != null) {
			for (OpenGL20Texture texture : textures.valueCollection()) {
				if (texture.isCreated()) {
					texture.bind();
					program.bindTexture(texture.getUnit());
				}
			}
		}
	}

	@Override
	public void uploadUniforms() {
		program.upload(uniforms);
	}

	@Override
	public void unbind() {
		checkCreated();
		program.unbind();
		if (textures != null) {
			for (OpenGL20Texture texture : textures.valueCollection()) {
				if (texture.isCreated()) {
					texture.unbind();
				}
			}
		}
	}

	@Override
	public OpenGL20Program getProgram() {
		return program;
	}

	@Override
	public void addTexture(Texture texture) {
		RenderUtil.checkVersion(this, texture);
		if (textures == null) {
			textures = new TIntObjectHashMap<>();
		}
		final OpenGL20Texture gl20Texture = (OpenGL20Texture) texture;
		textures.put(gl20Texture.getUnit(), gl20Texture);
	}

	@Override
	public boolean hasTexture(int unit) {
		return textures != null && textures.containsKey(unit);
	}

	@Override
	public OpenGL20Texture getTexture(int unit) {
		return textures != null ? textures.get(unit) : null;
	}

	@Override
	public void removeTexture(int unit) {
		if (textures != null) {
			textures.remove(unit);
		}
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
