/*
 * This file is part of Caustic Android.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic Android is licensed under the Spout License Version 1.
 *
 * Caustic Android is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic Android is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.android.gles20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Set;

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import android.opengl.GLES20;

import org.spout.math.matrix.Matrix2f;
import org.spout.math.matrix.Matrix3f;
import org.spout.math.matrix.Matrix4f;
import org.spout.math.vector.Vector2f;
import org.spout.math.vector.Vector3f;
import org.spout.math.vector.Vector4f;
import org.spout.renderer.android.AndroidUtil;
import org.spout.renderer.api.data.Uniform;
import org.spout.renderer.api.data.UniformHolder;
import org.spout.renderer.api.gl.Program;
import org.spout.renderer.api.gl.Shader.ShaderType;
import org.spout.renderer.api.util.CausticUtil;

/**
 * An OpenGLES 2.0 implementation of {@link org.spout.renderer.api.gl.Program}.
 *
 * @see org.spout.renderer.api.gl.Program
 */
public class GLES20Program extends Program {
    // Map of the uniform names to their locations
    private final TObjectIntMap<String> uniforms = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);

    protected GLES20Program() {
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
        id = GLES20.glCreateProgram();
        // Create the vertex Shader
        GLES20.glAttachShader(id, shaders.get(ShaderType.VERTEX).getID());
        // Create the fragment Shader
        GLES20.glAttachShader(id, shaders.get(ShaderType.FRAGMENT).getID());
        // If the attribute layout has been setup, apply it
        if (attributeLayouts != null && !attributeLayouts.isEmpty()) {
            final TObjectIntIterator<String> iterator = attributeLayouts.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                // Bind the index to the name
                GLES20.glBindAttribLocation(id, iterator.value(), iterator.key());
            }
        }
        // Link program
        GLES20.glLinkProgram(id);
        int[] param = new int[1];
        GLES20.glGetProgramiv(id, GLES20.GL_LINK_STATUS, param, 0);
        if (param[0] == GLES20.GL_FALSE) {
            throw new IllegalStateException("Program could not be linked\n" + GLES20.glGetProgramInfoLog(id));
        }

        // Validate program
        GLES20.glValidateProgram(id);
        // Load uniforms
        int[] params = new int[1];
        GLES20.glGetProgramiv(id, GLES20.GL_ACTIVE_UNIFORMS, params, 0);
        for (int i = 0; i < params[0]; i++) {
            byte nameByte = 0;
            GLES20.glGetActiveUniform(id, i, 1, IntBuffer.allocate(1), IntBuffer.allocate(1), IntBuffer.allocate(1), nameByte);
            // Simplify array names
            final String name = new String(new byte[]{nameByte}).trim().replaceFirst("\\[\\d+\\]", "");
            uniforms.put(name, GLES20.glGetUniformLocation(id, name));
        }
        super.create();
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void destroy() {
        checkCreated();
        GLES20.glDeleteProgram(id);
        uniforms.clear();
        super.destroy();
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void bind() {
        checkCreated();
        GLES20.glUseProgram(id);
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void unbind() {
        checkCreated();
        GLES20.glUseProgram(0);
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
        GLES20.glUniform1i(uniforms.get(name), b ? 1 : 0);
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void setUniform(String name, int i) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GLES20.glUniform1i(uniforms.get(name), i);
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void setUniform(String name, float f) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GLES20.glUniform1f(uniforms.get(name), f);
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void setUniform(String name, Vector2f v) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GLES20.glUniform2f(uniforms.get(name), v.getX(), v.getY());
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void setUniform(String name, Vector2f[] vs) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        int count = 0;
        final FloatBuffer vectorBuffer = CausticUtil.createFloatBuffer(vs.length * 2);
        for (Vector2f v : vs) {
            vectorBuffer.put(v.getX());
            vectorBuffer.put(v.getY());
            count++;
        }
        vectorBuffer.flip();
        GLES20.glUniform2fv(uniforms.get(name), count, vectorBuffer);
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void setUniform(String name, Vector3f v) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GLES20.glUniform3f(uniforms.get(name), v.getX(), v.getY(), v.getZ());
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void setUniform(String name, Vector3f[] vs) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        int count = 0;
        final FloatBuffer vectorBuffer = CausticUtil.createFloatBuffer(vs.length * 3);
        for (Vector3f v : vs) {
            vectorBuffer.put(v.getX());
            vectorBuffer.put(v.getY());
            vectorBuffer.put(v.getZ());
            count++;
        }
        vectorBuffer.flip();
        GLES20.glUniform3fv(uniforms.get(name), count, vectorBuffer);
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void setUniform(String name, Vector4f v) {
        checkCreated();
        if (!uniforms.containsKey(name)) {
            return;
        }
        GLES20.glUniform4f(uniforms.get(name), v.getX(), v.getY(), v.getZ(), v.getW());
        AndroidUtil.checkForGLESError();
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
        GLES20.glUniformMatrix2fv(uniforms.get(name), 1, false, buffer);
        AndroidUtil.checkForGLESError();
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
        GLES20.glUniformMatrix3fv(uniforms.get(name), 1, false, buffer);
        AndroidUtil.checkForGLESError();
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
        GLES20.glUniformMatrix4fv(uniforms.get(name), 1, false, buffer);
        AndroidUtil.checkForGLESError();
    }

    @Override
    public Set<String> getUniformNames() {
        return Collections.unmodifiableSet(uniforms.keySet());
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GLES20;
    }
}
