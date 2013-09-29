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

import org.spout.renderer.GLVersioned;

/**
 * A factory for generating OpenGL objects of the correct version.
 */
public interface GLFactory extends GLVersioned {
	/**
	 * Creates a new frame buffer.
	 *
	 * @return A new frame buffer
	 */
	FrameBuffer createFrameBuffer();

	/**
	 * Creates a new program.
	 *
	 * @return A new program
	 */
	Program createProgram();

	/**
	 * Creates a new render buffer.
	 *
	 * @return A new render buffer
	 */
	RenderBuffer createRenderBuffer();

	/**
	 * Creates a new context.
	 *
	 * @return A new context
	 */
	Context createContext();

	/**
	 * Creates a new shader.
	 *
	 * @return A new shader
	 */
	Shader createShader();

	/**
	 * Creates a new texture.
	 *
	 * @return A new texture
	 */
	Texture createTexture();

	/**
	 * Creates a new vertex array.
	 *
	 * @return A new vertex array
	 */
	VertexArray createVertexArray();
}
