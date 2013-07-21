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

import java.awt.Color;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Set;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.renderer.Program;
import org.spout.renderer.Shader.ShaderType;
import org.spout.renderer.util.RenderUtil;

/**
 * Represents a program for OpenGL 2.0. A program is a composed of a vertex shader and a fragment
 * shader. After being constructed, set the shader sources with {@link
 * #setVertexShaderSource(java.io.InputStream)} and {@link #setFragmentShaderSource(java.io.InputStream)}.
 * The program then needs to be created in the OpenGL context with {@link #create()}.
 */
public class OpenGL20Program extends Program {
	// Shaders
	private final OpenGL20Shader vertexShader = new OpenGL20Shader();
	private final OpenGL20Shader fragmentShader = new OpenGL20Shader();
	// Map of the uniform name to the ID
	private final TObjectIntMap<String> uniforms = new TObjectIntHashMap<>();

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Program has already been created");
		}
		vertexShader.setType(ShaderType.VERTEX);
		vertexShader.create();
		fragmentShader.setType(ShaderType.FRAGMENT);
		fragmentShader.create();
		id = GL20.glCreateProgram();
		GL20.glAttachShader(id, vertexShader.getID());
		GL20.glAttachShader(id, fragmentShader.getID());
		GL20.glLinkProgram(id);
		GL20.glValidateProgram(id);
		final int uniformCount = GL20.glGetProgrami(id, GL20.GL_ACTIVE_UNIFORMS);
		for (int i = 0; i < uniformCount; i++) {
			final ByteBuffer nameBuffer = BufferUtils.createByteBuffer(256);
			GL20.glGetActiveUniform(id, i,
					BufferUtils.createIntBuffer(1),
					BufferUtils.createIntBuffer(1),
					BufferUtils.createIntBuffer(1),
					nameBuffer);
			nameBuffer.rewind();
			final byte[] nameBytes = new byte[256];
			nameBuffer.get(nameBytes);
			final String name = new String(nameBytes).trim();
			uniforms.put(name, GL20.glGetUniformLocation(id, name));
		}
		super.create();
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		GL20.glDetachShader(id, vertexShader.getID());
		GL20.glDetachShader(id, fragmentShader.getID());
		vertexShader.destroy();
		fragmentShader.destroy();
		GL20.glDeleteProgram(id);
		uniforms.clear();
		super.destroy();
		RenderUtil.checkForOpenGLError();
	}

	private void checkCreated() {
		if (!created) {
			throw new IllegalStateException("Program has not been created yet");
		}
	}

	@Override
	public void bind() {
		checkCreated();
		GL20.glUseProgram(id);
	}

	@Override
	public void unbind() {
		checkCreated();
		GL20.glUseProgram(0);
	}

	/**
	 * Sets the vertex shader source input stream.
	 *
	 * @param source The input stream to use
	 */
	public void setVertexShaderSource(InputStream source) {
		vertexShader.setSource(source);
	}

	/**
	 * Sets the fragment shader source input stream.
	 *
	 * @param source The input stream to use
	 */
	public void setFragmentShaderSource(InputStream source) {
		fragmentShader.setSource(source);
	}

	/**
	 * Sets a uniform boolean in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param b The boolean value
	 */
	public void setUniform(String name, boolean b) {
		checkCreated();
		checkContainsUniform(name);
		GL20.glUniform1i(uniforms.get(name), b ? 1 : 0);
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform integer in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param i The integer value
	 */
	public void setUniform(String name, int i) {
		checkCreated();
		checkContainsUniform(name);
		GL20.glUniform1i(uniforms.get(name), i);
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform float in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param f The float value
	 */
	public void setUniform(String name, float f) {
		checkCreated();
		checkContainsUniform(name);
		GL20.glUniform1f(uniforms.get(name), f);
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector3} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public void setUniform(String name, Vector2 v) {
		checkCreated();
		checkContainsUniform(name);
		GL20.glUniform2f(uniforms.get(name), v.getX(), v.getY());
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector3} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public void setUniform(String name, Vector3 v) {
		checkCreated();
		checkContainsUniform(name);
		GL20.glUniform3f(uniforms.get(name), v.getX(), v.getY(), v.getZ());
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector3} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public void setUniform(String name, Vector4 v) {
		checkCreated();
		checkContainsUniform(name);
		GL20.glUniform4f(uniforms.get(name), v.getX(), v.getY(), v.getZ(), v.getW());
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public void setUniform(String name, Matrix2 m) {
		checkCreated();
		checkContainsUniform(name);
		final FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(m.toArray(true));
		buffer.flip();
		GL20.glUniformMatrix2(uniforms.get(name), false, buffer);
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public void setUniform(String name, Matrix3 m) {
		checkCreated();
		checkContainsUniform(name);
		final FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
		buffer.put(m.toArray(true));
		buffer.flip();
		GL20.glUniformMatrix3(uniforms.get(name), false, buffer);
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public void setUniform(String name, Matrix4 m) {
		checkCreated();
		checkContainsUniform(name);
		final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.put(m.toArray(true));
		buffer.flip();
		GL20.glUniformMatrix4(uniforms.get(name), false, buffer);
		RenderUtil.checkForOpenGLError();
	}

	/**
	 * Sets a uniform {@link java.awt.Color} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param c The color value
	 */
	public void setUniform(String name, Color c) {
		checkCreated();
		checkContainsUniform(name);
		GL20.glUniform4f(uniforms.get(name),
				c.getRed() / 255f, c.getGreen() / 255f,
				c.getBlue() / 255f, c.getAlpha() / 255f);
		RenderUtil.checkForOpenGLError();
	}

	private void checkContainsUniform(String name) {
		if (!uniforms.containsKey(name)) {
			throw new IllegalArgumentException("The uniform \"" + name + "\" could not be found in the program");
		}
	}

	/**
	 * Returns an immutable set containing all of the uniform names for this program.
	 *
	 * @return A set of all the uniform names
	 */
	public Set<String> getUniformNames() {
		return Collections.unmodifiableSet(uniforms.keySet());
	}
}
