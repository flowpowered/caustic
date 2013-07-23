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

import org.spout.renderer.gl20.OpenGL20Material;
import org.spout.renderer.gl20.OpenGL20Model;
import org.spout.renderer.gl20.OpenGL20Program;
import org.spout.renderer.gl20.OpenGL20Renderer;
import org.spout.renderer.gl20.OpenGL20Shader;
import org.spout.renderer.gl20.OpenGL20Texture;
import org.spout.renderer.gl20.OpenGL20VertexArray;
import org.spout.renderer.gl30.OpenGL30Model;
import org.spout.renderer.gl30.OpenGL30Renderer;
import org.spout.renderer.gl30.OpenGL30VertexArray;

/**
 * An enum of the supported OpenGL versions. Use this class to generate rendering objects compatible
 * with the version.
 */
public enum GLVersion {
	GL20,
	GL30;

	/**
	 * Creates a new renderer for the version.
	 *
	 * @return A new renderer
	 */
	public Renderer createRenderer() {
		switch (this) {
			case GL20:
				return new OpenGL20Renderer();
			case GL30:
				return new OpenGL30Renderer();
			default:
				return null;
		}
	}

	/**
	 * Creates a new model for the version.
	 *
	 * @return A new model
	 */
	public Model createModel() {
		switch (this) {
			case GL20:
				return new OpenGL20Model();
			case GL30:
				return new OpenGL30Model();
			default:
				return null;
		}
	}

	/**
	 * Creates a new vertex array for the version.
	 *
	 * @return A new vertex array
	 */
	public VertexArray createVertexArray() {
		switch (this) {
			case GL20:
				return new OpenGL20VertexArray();
			case GL30:
				return new OpenGL30VertexArray();
			default:
				return null;
		}
	}

	/**
	 * Creates a new material for the version.
	 *
	 * @return A new material
	 */
	public Material createMaterial() {
		switch (this) {
			case GL20:
			case GL30:
				return new OpenGL20Material();
			default:
				return null;
		}
	}

	/**
	 * Creates a new program for the version.
	 *
	 * @return A new program
	 */
	public Program createProgram() {
		switch (this) {
			case GL20:
			case GL30:
				return new OpenGL20Program();
			default:
				return null;
		}
	}

	/**
	 * Creates a new shader for the version.
	 *
	 * @return A new shader
	 */
	public Shader createShader() {
		switch (this) {
			case GL20:
			case GL30:
				return new OpenGL20Shader();
			default:
				return null;
		}
	}

	/**
	 * Creates a new texture for the version.
	 *
	 * @return A new texture
	 */
	public Texture createTexture() {
		switch (this) {
			case GL20:
			case GL30:
				return new OpenGL20Texture();
			default:
				return null;
		}
	}
}
