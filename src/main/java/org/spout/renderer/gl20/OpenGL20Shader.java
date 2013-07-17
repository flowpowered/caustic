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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.OpenGLException;

import org.spout.renderer.util.RenderUtil;

/**
 * Represents a shader for OpenGL 2.1. After being constructed, the program needs to be created in
 * the OpenGL context with {@link #create(java.io.InputStream, int)}. This class is meant to be used
 * by the {@link OpenGL20Program} class.
 */
public class OpenGL20Shader {
	// State
	private boolean created = false;
	// ID
	private int id;

	/**
	 * Creates a new shader in the OpenGL context from the input stream for the shaders.
	 * @param shaderResource The shader input stream
	 * @param type The type of shader, either {@link GL20#GL_VERTEX_SHADER} or {@link
	 * GL20#GL_FRAGMENT_SHADER}
	 */
	public void create(InputStream shaderResource, int type) {
		if (created) {
			throw new IllegalStateException("Shader has already been created.");
		}
		final StringBuilder shaderSource = new StringBuilder();
		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(shaderResource));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
			shaderResource.close();
		} catch (IOException e) {
			System.out.println("IO exception: " + e.getMessage());
		}
		final int id = GL20.glCreateShader(type);
		GL20.glShaderSource(id, shaderSource);
		GL20.glCompileShader(id);
		if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			throw new OpenGLException("OPEN GL ERROR: Could not compile shader\n" + GL20.glGetShaderInfoLog(id, 1000));
		}
		this.id = id;
		created = true;
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Destroys this shader by deleting the OpenGL shader.
	 */
	public void destroy() {
		if (!created) {
			throw new IllegalStateException("Shader has not been created yet.");
		}
		GL20.glDeleteShader(id);
		id = 0;
		created = false;
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Gets the ID for this shader as assigned by OpenGL.
	 * @return The ID
	 */
	public int getID() {
		return id;
	}
}
