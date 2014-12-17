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
package com.flowpowered.caustic.lwjgl.gl20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.flowpowered.math.matrix.Matrix2f;
import com.flowpowered.math.matrix.Matrix3f;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.flowpowered.caustic.api.gl.Program;
import com.flowpowered.caustic.api.gl.Shader;
import com.flowpowered.caustic.api.util.CausticUtil;
import com.flowpowered.caustic.lwjgl.LWJGLUtil;

/**
 * An OpenGL 2.0 implementation of {@link Program}.
 *
 * @see Program
 */
public class GL20Program extends Program {
    // Represents an unset value for a uniform
    private static final Object UNSET = new Object() {
        @Override
        public String toString() {
            return "UNSET";
        }
    };
    // Set of all shaders in this program
    private final Set<Shader> shaders = new HashSet<>();
    // Map of the attribute names to their vao index (optional for GL30 as they can be defined in the shader instead)
    private final TObjectIntMap<String> attributeLayouts = new TObjectIntHashMap<>();
    // Map of the texture units to their names
    private final TIntObjectMap<String> textureLayouts = new TIntObjectHashMap<>();
    // Map of the uniform names to their locations
    private final TObjectIntMap<String> uniforms = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);
    // Map of the uniform names to their values
    private final Map<String, Object> uniformValues = new HashMap<>();

    @Override
    public void create() {
        checkNotCreated();
        // Create program
        id = GL20.glCreateProgram();
        // Update the state
        super.create();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Delete the program
        GL20.glDeleteProgram(id);
        // Check for errors
        LWJGLUtil.checkForGLError();
        // Clear the data
        shaders.clear();
        attributeLayouts.clear();
        textureLayouts.clear();
        uniforms.clear();
        uniformValues.clear();
        // Update the state
        super.destroy();
    }

    @Override
    public void attachShader(Shader shader) {
        checkCreated();
        // Attach the shader
        GL20.glAttachShader(id, shader.getID());
        // Check for errors
        LWJGLUtil.checkForGLError();
        // Add the shader to the set
        shaders.add(shader);
        // Add all attribute and texture layouts
        attributeLayouts.putAll(shader.getAttributeLayouts());
        textureLayouts.putAll(shader.getTextureLayouts());
    }

    @Override
    public void detachShader(Shader shader) {
        checkCreated();
        // Attach the shader
        GL20.glDetachShader(id, shader.getID());
        // Check for errors
        LWJGLUtil.checkForGLError();
        // Remove the shader from the set
        shaders.remove(shader);
        // Remove all attribute and texture layouts
        for (String attribute : shader.getAttributeLayouts().keySet()) {
            attributeLayouts.remove(attribute);
        }
        for (int unit : shader.getTextureLayouts().keys()) {
            textureLayouts.remove(unit);
        }
    }

    @Override
    public void link() {
        checkCreated();
        // Add the attribute layouts to the program state
        final TObjectIntIterator<String> iterator = attributeLayouts.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            // Bind the index to the name
            GL20.glBindAttribLocation(id, iterator.value(), iterator.key());
        }
        // Link program
        GL20.glLinkProgram(id);
        // Check program link status
        if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new IllegalStateException("Program could not be linked\n" + GL20.glGetProgramInfoLog(id, 1000));
        }
        if (CausticUtil.isDebugEnabled()) {
            // Validate program
            GL20.glValidateProgram(id);
            // Check program validation status
            if (GL20.glGetProgrami(id, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
                final Logger logger = CausticUtil.getCausticLogger();
                logger.log(Level.WARNING, "Program validation failed. This doesn''t mean it won''t work, so you maybe able to ignore it\n{0}", GL20.glGetProgramInfoLog(id, 1000));
            }
        }
        // Load uniforms
        uniforms.clear();
        final int uniformCount = GL20.glGetProgrami(id, GL20.GL_ACTIVE_UNIFORMS);
        final int maxLength = GL20.glGetProgrami(id, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH);
        final IntBuffer lengthBuffer = CausticUtil.createIntBuffer(1);
        final IntBuffer ignored1 = CausticUtil.createIntBuffer(1);
        final IntBuffer ignored2 = CausticUtil.createIntBuffer(1);
        final ByteBuffer nameBuffer = CausticUtil.createByteBuffer(maxLength);
        final byte[] nameBytes = new byte[maxLength];
        for (int i = 0; i < uniformCount; i++) {
            lengthBuffer.clear();
            ignored1.clear();
            ignored2.clear();
            nameBuffer.clear();
            GL20.glGetActiveUniform(id, i, lengthBuffer, ignored1, ignored2, nameBuffer);
            final int length = lengthBuffer.get();
            nameBuffer.get(nameBytes, 0, length);
            // Simplify array names
            final String name = new String(nameBytes, 0, length).replaceFirst("\\[\\d+\\]", "");
            uniforms.put(name, GL20.glGetUniformLocation(id, name));
            uniformValues.put(name, UNSET);
        }
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void use() {
        checkCreated();
        // Bind the program
        GL20.glUseProgram(id);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void bindSampler(int unit) {
        if (!textureLayouts.containsKey(unit)) {
            throw new IllegalArgumentException("No texture layout has been set for the unit: " + unit);
        }
        setUniform(textureLayouts.get(unit), unit);
    }

    // TODO: Support int and boolean vectors
    @Override
    public void setUniform(String name, boolean b) {
        checkCreated();
        if (!isDirty(name, b)) {
            return;
        }
        GL20.glUniform1i(uniforms.get(name), b ? 1 : 0);
        uniformValues.put(name, b);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, int i) {
        checkCreated();
        if (!isDirty(name, i)) {
            return;
        }
        GL20.glUniform1i(uniforms.get(name), i);
        uniformValues.put(name, i);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, float f) {
        checkCreated();
        if (!isDirty(name, f)) {
            return;
        }
        GL20.glUniform1f(uniforms.get(name), f);
        uniformValues.put(name, f);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, float[] fs) {
        checkCreated();
        if (!isDirty(name, fs)) {
            return;
        }
        final FloatBuffer floatBuffer = CausticUtil.createFloatBuffer(fs.length);
        for (float f : fs) {
            floatBuffer.put(f);
        }
        floatBuffer.flip();
        GL20.glUniform1(uniforms.get(name), floatBuffer);
        uniformValues.put(name, fs);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector2f v) {
        checkCreated();
        if (!isDirty(name, v)) {
            return;
        }
        GL20.glUniform2f(uniforms.get(name), v.getX(), v.getY());
        uniformValues.put(name, v);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector2f[] vs) {
        checkCreated();
        if (!isDirty(name, vs)) {
            return;
        }
        final FloatBuffer vectorBuffer = CausticUtil.createFloatBuffer(vs.length * 2);
        for (Vector2f v : vs) {
            vectorBuffer.put(v.getX());
            vectorBuffer.put(v.getY());
        }
        vectorBuffer.flip();
        GL20.glUniform2(uniforms.get(name), vectorBuffer);
        uniformValues.put(name, vs);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector3f v) {
        checkCreated();
        if (!isDirty(name, v)) {
            return;
        }
        GL20.glUniform3f(uniforms.get(name), v.getX(), v.getY(), v.getZ());
        uniformValues.put(name, v);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector3f[] vs) {
        checkCreated();
        if (!isDirty(name, vs)) {
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
        uniformValues.put(name, vs);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Vector4f v) {
        checkCreated();
        if (!isDirty(name, v)) {
            return;
        }
        GL20.glUniform4f(uniforms.get(name), v.getX(), v.getY(), v.getZ(), v.getW());
        uniformValues.put(name, v);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Matrix2f m) {
        checkCreated();
        if (!isDirty(name, m)) {
            return;
        }
        final FloatBuffer buffer = CausticUtil.createFloatBuffer(4);
        buffer.put(m.toArray(true));
        buffer.flip();
        GL20.glUniformMatrix2(uniforms.get(name), false, buffer);
        uniformValues.put(name, m);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Matrix3f m) {
        checkCreated();
        if (!isDirty(name, m)) {
            return;
        }
        final FloatBuffer buffer = CausticUtil.createFloatBuffer(9);
        buffer.put(m.toArray(true));
        buffer.flip();
        GL20.glUniformMatrix3(uniforms.get(name), false, buffer);
        uniformValues.put(name, m);
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setUniform(String name, Matrix4f m) {
        checkCreated();
        if (!isDirty(name, m)) {
            return;
        }
        final FloatBuffer buffer = CausticUtil.createFloatBuffer(16);
        buffer.put(m.toArray(true));
        buffer.flip();
        GL20.glUniformMatrix4(uniforms.get(name), false, buffer);
        uniformValues.put(name, m);
        LWJGLUtil.checkForGLError();
    }

    private boolean isDirty(String name, Object newValue) {
        final Object oldValue = uniformValues.get(name);
        return oldValue != null && (oldValue == UNSET || !oldValue.equals(newValue));
    }

    @Override
    public Set<Shader> getShaders() {
        return Collections.unmodifiableSet(shaders);
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
