/*
 * This file is part of Caustic LWJGL.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic LWJGL is licensed under the Spout License Version 1.
 *
 * Caustic LWJGL is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic LWJGL is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.lwjgl.gl21;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Set;

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import org.spout.math.matrix.Matrix2f;
import org.spout.math.matrix.Matrix3f;
import org.spout.math.matrix.Matrix4f;
import org.spout.math.vector.Vector2f;
import org.spout.math.vector.Vector3f;
import org.spout.math.vector.Vector4f;
import org.spout.renderer.api.data.Uniform;
import org.spout.renderer.api.data.UniformHolder;
import org.spout.renderer.api.gl.Program;
import org.spout.renderer.api.gl.Shader.ShaderType;
import org.spout.renderer.api.util.CausticUtil;
import org.spout.renderer.lwjgl.LWJGLUtil;

/**
 * An OpenGL 2.1 implementation of {@link Program}.
 *
 * @see Program
 */
public class GL21Program extends Program {
    // Map of the uniform names to their locations
    private final TObjectIntMap<String> uniforms = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);

    protected GL21Program() {
    }

    @Override
    public void create() {
        if (isCreated()) {
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
            final ByteBuffer nameBuffer = CausticUtil.createByteBuffer(256);
            GL20.glGetActiveUniform(id, i, CausticUtil.createIntBuffer(1), CausticUtil.createIntBuffer(1), CausticUtil.createIntBuffer(1), nameBuffer);
            nameBuffer.rewind();
            final byte[] nameBytes = new byte[256];
            nameBuffer.get(nameBytes);
            // Simplify array names
            final String name = new String(nameBytes).trim().replaceFirst("\\[\\d+\\]", "");
            uniforms.put(name, GL20.glGetUniformLocation(id, name));
        }
        super.create();
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void destroy() {
        checkCreated();
        GL20.glDeleteProgram(id);
        uniforms.clear();
        super.destroy();
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void bind() {
        checkCreated();
        GL20.glUseProgram(id);
        LWJGLUtil.checkForGLError();
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
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, int i) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GL20.glUniform1i(uniforms.get(name), i);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, float f) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GL20.glUniform1f(uniforms.get(name), f);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector2f v) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GL20.glUniform2f(uniforms.get(name), v.getX(), v.getY());
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector2f[] vs) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        final FloatBuffer vectorBuffer = CausticUtil.createFloatBuffer(vs.length * 2);
        for (Vector2f v : vs) {
            vectorBuffer.put(v.getX());
            vectorBuffer.put(v.getY());
        }
        vectorBuffer.flip();
        GL20.glUniform2(uniforms.get(name), vectorBuffer);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector3f v) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GL20.glUniform3f(uniforms.get(name), v.getX(), v.getY(), v.getZ());
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector3f[] vs) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        final FloatBuffer vectorBuffer = CausticUtil.createFloatBuffer(vs.length * 3);
        for (Vector3f v : vs) {
            vectorBuffer.put(v.getX());
            vectorBuffer.put(v.getY());
            vectorBuffer.put(v.getZ());
        }
        vectorBuffer.flip();
        GL20.glUniform3(uniforms.get(name), vectorBuffer);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector4f v) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GL20.glUniform4f(uniforms.get(name), v.getX(), v.getY(), v.getZ(), v.getW());
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Matrix2f m) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        final FloatBuffer buffer = CausticUtil.createFloatBuffer(4);
        buffer.put(m.toArray(true));
        buffer.flip();
        GL20.glUniformMatrix2(uniforms.get(name), false, buffer);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Matrix3f m) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        final FloatBuffer buffer = CausticUtil.createFloatBuffer(9);
        buffer.put(m.toArray(true));
        buffer.flip();
        GL20.glUniformMatrix3(uniforms.get(name), false, buffer);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Matrix4f m) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        final FloatBuffer buffer = CausticUtil.createFloatBuffer(16);
        buffer.put(m.toArray(true));
        buffer.flip();
        GL20.glUniformMatrix4(uniforms.get(name), false, buffer);
        LWJGLUtil.checkForGLError();
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
