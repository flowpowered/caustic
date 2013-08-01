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

import java.util.Scanner;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.OpenGLException;

import org.spout.renderer.GLVersion;
import org.spout.renderer.Shader;
import org.spout.renderer.util.RenderUtil;

/**
 * An OpenGL 2.0 implementation of {@link Shader}.
 *
 * @see Shader
 */
public class OpenGL20Shader extends Shader {
	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Shader has already been created");
		}
		if (shaderSource == null) {
			throw new IllegalStateException("Shader source has not been set");
		}
		if (shaderType == null) {
			throw new IllegalStateException("Shader type has not been set");
		}
		// Attempt to read the source and place it into a string builder
		final StringBuilder source = new StringBuilder();
		try (Scanner reader = new Scanner(shaderSource)) {
			while (reader.hasNextLine()) {
				source.append(reader.nextLine()).append('\n');
			}
			shaderSource.close();
		} catch (Exception ex) {
			throw new IllegalStateException("Unreadable shader source", ex);
		}
		// Create a shader for the type
		final int id = GL20.glCreateShader(shaderType.getGLConstant());
		// Send the source
		GL20.glShaderSource(id, source);
		// Compile the shader
		GL20.glCompileShader(id);
		// Get the shader compile status property, check it's false and fail if that's the case
		if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			throw new OpenGLException("OPEN GL ERROR: Could not compile shader\n" + GL20.glGetShaderInfoLog(id, 1000));
		}
		this.id = id;
		super.create();
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		if (!created) {
			throw new IllegalStateException("Shader has not been created yet");
		}
		// Delete the shader
		GL20.glDeleteShader(id);
		super.destroy();
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
