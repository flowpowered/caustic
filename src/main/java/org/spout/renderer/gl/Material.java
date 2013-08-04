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
package org.spout.renderer.gl;

import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;
import org.spout.renderer.data.UniformHolder;

/**
 * Represents an OpenGL material. Materials are assigned to models, and these can share the same material. The material provides the shader program to use when rendering the models, the texture for
 * each unit (if any) and a set of uniforms that will be constant for all models using the material.
 */
public abstract class Material extends Creatable implements GLVersioned {
	// Material uniforms
	protected final UniformHolder uniforms = new UniformHolder();

	/**
	 * Binds the material to the OpenGL context.
	 */
	public abstract void bind();

	/**
	 * Uploads the material's uniforms to its program.
	 */
	public abstract void uploadUniforms();

	/**
	 * Unbinds the material from the OpenGL context.
	 */
	public abstract void unbind();

	/**
	 * Returns the material's program.
	 *
	 * @return The program
	 */
	public abstract Program getProgram();

	/**
	 * Adds a texture to the material. If a texture is a already present in the same unit as this one, it will be replaced.
	 *
	 * @param texture The texture to add
	 */
	public abstract void addTexture(Texture texture);

	/**
	 * Returns true if a texture is present in the unit.
	 *
	 * @param unit The unit to check
	 * @return Whether or not a texture is present
	 */
	public abstract boolean hasTexture(int unit);

	/**
	 * Returns the texture in the unit, or null if none is present.
	 *
	 * @param unit The unit to check
	 * @return The texture
	 */
	public abstract Texture getTexture(int unit);

	/**
	 * Removed the texture in the unit, if present.
	 *
	 * @param unit The unit to remove the texture from
	 */
	public abstract void removeTexture(int unit);

	/**
	 * Returns the uniform holder for this material.
	 *
	 * @return The uniforms
	 */
	public UniformHolder getUniforms() {
		return uniforms;
	}
}
