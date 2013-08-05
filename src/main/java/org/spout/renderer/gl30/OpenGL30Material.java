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

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.spout.renderer.GLVersion;
import org.spout.renderer.gl.Material;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Texture;
import org.spout.renderer.gl20.OpenGL20Texture;
import org.spout.renderer.util.RenderUtil;

/**
 * An OpenGL 3.0 implementation of {@link Material}.
 *
 * @see Material
 */
public class OpenGL30Material extends Material {
	private OpenGL30Program program;
	// Textures by unit
	protected TIntObjectMap<OpenGL30Texture> textures;

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Material has already been created");
		}
		if (program == null) {
			throw new IllegalStateException("Program has not been set");
		}
		program.checkCreated();
		if (textures != null) {
			for (OpenGL20Texture texture : textures.valueCollection()) {
				texture.checkCreated();
			}
		}
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		textures = null;
		super.destroy();
	}

	@Override
	public void uploadUniforms() {
		program.upload(uniforms);
	}

	@Override
	public void bind() {
		checkCreated();
		program.bind();
		if (textures != null) {
			final TIntObjectIterator<OpenGL30Texture> iterator = textures.iterator();
			while (iterator.hasNext()) {
				iterator.advance();
				// Bind the texture to the unit
				final int unit = iterator.key();
				iterator.value().bind(unit);
				// Bind the shader sampler uniform to the unit
				program.bindTextureUniform(unit);
			}
		}
	}

	@Override
	public void unbind() {
		checkCreated();
		program.unbind();
		if (textures != null) {
			for (OpenGL30Texture texture : textures.valueCollection()) {
				texture.unbind();
			}
		}
	}

	@Override
	public void setProgram(Program program) {
		RenderUtil.checkVersion(this, program);
		this.program = (OpenGL30Program) program;
	}

	@Override
	public OpenGL30Program getProgram() {
		return program;
	}

	@Override
	public void addTexture(int unit, Texture texture) {
		RenderUtil.checkVersion(this, texture);
		if (textures == null) {
			textures = new TIntObjectHashMap<>();
		}
		textures.put(unit, (OpenGL30Texture) texture);
	}

	@Override
	public boolean hasTexture(int unit) {
		return textures != null && textures.containsKey(unit);
	}

	@Override
	public OpenGL30Texture getTexture(int unit) {
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
		return GLVersion.GL30;
	}
}
