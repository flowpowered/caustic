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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Set;

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.renderer.GLVersion;
import org.spout.renderer.data.Uniform;
import org.spout.renderer.data.UniformHolder;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Shader.ShaderType;
import org.spout.renderer.util.RenderUtil;

/**
 * An OpenGL 2.0 implementation of {@link Program}.
 *
 * @see Program
 */
public class OpenGL20Program extends Program {
	// Map of the uniform names to their locations
	private final TObjectIntMap<String> uniforms = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Program has already been created");
		}
		if (!shaders.containsKey(ShaderType.VERTEX)) {
			throw new IllegalStateException("No source set for vertex shader");
		}
		if (!shaders.containsKey(ShaderType.FRAGMENT)) {
			throw new IllegalStateException("No source set for fragment shader");
		}
		// Create program
		id = GL20.glCreateProgram();
		// Create the vertex Shader
		GL20.glAttachShader(id, shaders.get(ShaderType.VERTEX).getID());
		// Create the fragment Shader
		GL20.glAttachShader(id, shaders.get(ShaderType.FRAGMENT).getID());
		// If the attribute layout has been setup, apply it
		if (attributeLayouts != null && !attributeLayouts.isEmpty()) {
			final TObjectIntIterator<String> iterator = attributeLayouts.iterator();
			while (iterator.hasNext()) {
				iterator.advance();
				// Bind the index to the name
				GL20.glBindAttribLocation(id, iterator.value(), iterator.key());
			}
		}
		// Link program
		GL20.glLinkProgram(id);
		// Check program link status
		if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			throw new IllegalStateException("Program could not be linked\n" + GL20.glGetProgramInfoLog(id, 1000));
		}
		// Validate program
		GL20.glValidateProgram(id);
		// Load uniforms
		final int uniformCount = GL20.glGetProgrami(id, GL20.GL_ACTIVE_UNIFORMS);
		for (int i = 0; i < uniformCount; i++) {
			final ByteBuffer nameBuffer = BufferUtils.createByteBuffer(256);
			GL20.glGetActiveUniform(id, i, BufferUtils.createIntBuffer(1), BufferUtils.createIntBuffer(1), BufferUtils.createIntBuffer(1), nameBuffer);
			nameBuffer.rewind();
			final byte[] nameBytes = new byte[256];
			nameBuffer.get(nameBytes);
			// Simplify array names
			final String name = new String(nameBytes).trim().replaceFirst("\\[\\d+\\]", "");
			uniforms.put(name, GL20.glGetUniformLocation(id, name));
		}
		super.create();
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();
		GL20.glDeleteProgram(id);
		uniforms.clear();
		super.destroy();
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void bind() {
		checkCreated();
		GL20.glUseProgram(id);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void unbind() {
		checkCreated();
		GL20.glUseProgram(0);
	}

	@Override
	public void bindTextureUniform(int unit) {
		if (textureLayouts == null || !textureLayouts.containsKey(unit)) {
			throw new IllegalArgumentException("No texture layout has been set for the unit: " + unit);
		}
		setUniform(textureLayouts.get(unit), unit);
	}

	@Override
	public void upload(Uniform uniform) {
		checkCreated();
		uniform.upload(this);
	}

	@Override
	public void upload(UniformHolder uniforms) {
		checkCreated();
		for (Uniform uniform : uniforms) {
			uniform.upload(this);
		}
	}

	// TODO: Support int and boolean vectors

	@Override
	public void setUniform(String name, boolean b) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		GL20.glUniform1i(uniforms.get(name), b ? 1 : 0);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, int i) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		GL20.glUniform1i(uniforms.get(name), i);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, float f) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		GL20.glUniform1f(uniforms.get(name), f);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, Vector2 v) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		GL20.glUniform2f(uniforms.get(name), v.getX(), v.getY());
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, Vector3 v) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		GL20.glUniform3f(uniforms.get(name), v.getX(), v.getY(), v.getZ());
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, Vector3[] vs) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		final FloatBuffer vectorBuffer = BufferUtils.createFloatBuffer(vs.length * 3);
		for (Vector3 v : vs) {
			vectorBuffer.put(v.getX());
			vectorBuffer.put(v.getY());
			vectorBuffer.put(v.getZ());
		}
		vectorBuffer.flip();
		GL20.glUniform3(uniforms.get(name), vectorBuffer);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, Vector4 v) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		GL20.glUniform4f(uniforms.get(name), v.getX(), v.getY(), v.getZ(), v.getW());
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, Matrix2 m) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		final FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(m.toArray(true));
		buffer.flip();
		GL20.glUniformMatrix2(uniforms.get(name), false, buffer);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, Matrix3 m) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		final FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
		buffer.put(m.toArray(true));
		buffer.flip();
		GL20.glUniformMatrix3(uniforms.get(name), false, buffer);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, Matrix4 m) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.put(m.toArray(true));
		buffer.flip();
		GL20.glUniformMatrix4(uniforms.get(name), false, buffer);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void setUniform(String name, Color c) {
		checkCreated();
		if (!uniforms.containsKey(name)) {
			return;
		}
		GL20.glUniform4f(uniforms.get(name), c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public Set<String> getUniformNames() {
		return Collections.unmodifiableSet(uniforms.keySet());
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
