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

import java.io.InputStream;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;

/**
 * Represents an OpenGL shader. The shader source and type must be set with {@link #setSource(java.io.InputStream)} and {@link #setType(Shader.ShaderType)} respectively before it can be created.
 */
public abstract class Shader extends Creatable implements GLVersioned {
	protected int id;
	protected InputStream shaderSource;
	protected ShaderType shaderType;

	@Override
	public void create() {
		// Release the shader input stream
		shaderSource = null;
		super.create();
	}

	@Override
	public void destroy() {
		id = 0;
		shaderType = null;
		super.destroy();
	}

	/**
	 * Gets the ID for this shader as assigned by OpenGL.
	 *
	 * @return The ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the shader source input stream.
	 *
	 * @param shaderSource The source input stream
	 */
	public void setSource(InputStream shaderSource) {
		this.shaderSource = shaderSource;
	}

	/**
	 * Sets the shader type.
	 *
	 * @param type The shader type
	 */
	public void setType(ShaderType type) {
		this.shaderType = type;
	}

	/**
	 * Represents a shader type.
	 */
	public static enum ShaderType {
		VERTEX(GL20.GL_VERTEX_SHADER),
		FRAGMENT(GL20.GL_FRAGMENT_SHADER),
		GEOMETRY(GL32.GL_GEOMETRY_SHADER),
		TESS_CONTROL(GL40.GL_TESS_CONTROL_SHADER),
		TESS_EVALUATION(GL40.GL_TESS_EVALUATION_SHADER),
		COMPUTE(GL43.GL_COMPUTE_SHADER);
		private final int glConstant;

		private ShaderType(int glConstant) {
			this.glConstant = glConstant;
		}

		/**
		 * Returns the OpenGL constant associated to the shader type.
		 *
		 * @return The OpenGL constant
		 */
		public int getGLConstant() {
			return glConstant;
		}
	}
}
