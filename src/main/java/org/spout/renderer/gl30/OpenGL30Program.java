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

import org.spout.renderer.GLVersion;
import org.spout.renderer.gl20.OpenGL20Program;

/**
 * Represents a program for OpenGL 3.0. A program is a composed of a vertex shader and a fragment
 * shader. After being constructed, set the shader sources with {@link
 * #addShaderSource(org.spout.renderer.Shader.ShaderType, java.io.InputStream)}, for the {@link
 * org.spout.renderer.Shader.ShaderType#VERTEX} and {@link org.spout.renderer.Shader.ShaderType#FRAGMENT}
 * types. The program then needs to be created in the OpenGL context with {@link #create()}. The
 * OpenGL 3.0 version is an extension of the OpenGL 2.0 version that offers support for the new data
 * types.
 */
public class OpenGL30Program extends OpenGL20Program {
	// TODO: support unsigned int scalars and vectors

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL30;
	}
}
