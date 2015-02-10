/*
 * This file is part of Caustic LWJGL, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.flowpowered.caustic.lwjgl.gl20;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.flowpowered.caustic.api.data.ShaderSource;
import com.flowpowered.caustic.api.gl.Shader;
import com.flowpowered.caustic.lwjgl.LWJGLUtil;

/**
 * An OpenGL 2.0 implementation of {@link Shader}.
 *
 * @see Shader
 */
public class GL20Shader extends Shader {
    private ShaderType type;
    // Map of the attribute names to their vao index (optional for GL30 as they can be defined in the shader instead)
    private final TObjectIntMap<String> attributeLayouts = new TObjectIntHashMap<>();
    // Map of the texture units to their names
    private final TIntObjectMap<String> textureLayouts = new TIntObjectHashMap<>();

    @Override
    public void create() {
        checkNotCreated();
        // Update the state
        super.create();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Delete the shader
        GL20.glDeleteShader(id);
        // Clear the data
        type = null;
        attributeLayouts.clear();
        textureLayouts.clear();
        // Update the state
        super.destroy();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setSource(ShaderSource source) {
        checkCreated();
        if (source == null) {
            throw new IllegalArgumentException("Shader source cannot be null");
        }
        if (!source.isComplete()) {
            throw new IllegalArgumentException("Shader source isn't complete");
        }
        // If we don't have a previous shader or the type isn't the same, we need to create a new one
        final ShaderType type = source.getType();
        if (id == 0 || this.type != type) {
            // Delete the old shader
            GL20.glDeleteShader(id);
            // Create a shader of the correct type
            id = GL20.glCreateShader(type.getGLConstant());
            // Store the current type
            this.type = type;
        }
        // Upload the new source
        GL20.glShaderSource(id, source.getSource());
        // Set the layouts from the source
        attributeLayouts.clear();
        attributeLayouts.putAll(source.getAttributeLayouts());
        textureLayouts.clear();
        textureLayouts.putAll(source.getTextureLayouts());
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void compile() {
        checkCreated();
        // Compile the shader
        GL20.glCompileShader(id);
        // Get the shader compile status property, check it's false and fail if that's the case
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new IllegalStateException("OPEN GL ERROR: Could not compile shader\n" + GL20.glGetShaderInfoLog(id, 1000));
        }
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public ShaderType getType() {
        return type;
    }

    @Override
    public TObjectIntMap<String> getAttributeLayouts() {
        return TCollections.unmodifiableMap(attributeLayouts);
    }

    @Override
    public TIntObjectMap<String> getTextureLayouts() {
        return TCollections.unmodifiableMap(textureLayouts);
    }

    @Override
    public void setAttributeLayout(String attribute, int layout) {
        attributeLayouts.put(attribute, layout);
    }

    @Override
    public void setTextureLayout(int unit, String sampler) {
        textureLayouts.put(unit, sampler);
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL20;
    }
}
