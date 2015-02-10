/*
 * This file is part of Caustic API, licensed under the MIT License (MIT).
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
package com.flowpowered.caustic.api.gl;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;

import com.flowpowered.caustic.api.Creatable;
import com.flowpowered.caustic.api.GLVersioned;
import com.flowpowered.caustic.api.data.ShaderSource;

/**
 * Represents an OpenGL shader. The shader source and type must be set with {@link #setSource(com.flowpowered.caustic.api.data.ShaderSource)}.
 */
public abstract class Shader extends Creatable implements GLVersioned {
    protected int id;

    @Override
    public void destroy() {
        id = 0;
        // Update the state
        super.destroy();
    }

    /**
     * Sets the shader source.
     *
     * @param source The shader source
     */
    public abstract void setSource(ShaderSource source);

    /**
     * Compiles the shader.
     */
    public abstract void compile();

    /**
     * Gets the shader type.
     *
     * @return The shader type
     */
    public abstract ShaderType getType();

    /**
     * Returns the attribute layouts parsed from the tokens in the shader source.
     *
     * @return A map of the attribute name to the layout index.
     */
    public abstract TObjectIntMap<String> getAttributeLayouts();

    /**
     * Returns the texture layouts parsed from the tokens in the shader source.
     *
     * @return A map of the texture name to the layout index.
     */
    public abstract TIntObjectMap<String> getTextureLayouts();

    /**
     * Sets an attribute layout.
     *
     * @param attribute The name of the attribute
     * @param layout The layout for the attribute
     */
    public abstract void setAttributeLayout(String attribute, int layout);

    /**
     * Sets a texture layout.
     *
     * @param unit The unit for the sampler
     * @param sampler The sampler name
     */
    public abstract void setTextureLayout(int unit, String sampler);

    /**
     * Gets the ID for this shader as assigned by OpenGL.
     *
     * @return The ID
     */
    public int getID() {
        return id;
    }

    /**
     * Represents a shader type.
     */
    public static enum ShaderType {
        FRAGMENT(0x8B30), // GL20.GL_FRAGMENT_SHADER
        VERTEX(0x8B31), // GL20.GL_VERTEX_SHADER
        GEOMETRY(0x8DD9), // GL32.GL_GEOMETRY_SHADER
        TESS_EVALUATION(0x8E87), // GL40.GL_TESS_EVALUATION_SHADER
        TESS_CONTROL(0x8E88), // GL40.GL_TESS_CONTROL_SHADER
        COMPUTE(0x91B9); // GL43.GL_COMPUTE_SHADER
        private final int glConstant;

        private ShaderType(int glConstant) {
            this.glConstant = glConstant;
        }

        /**
         * Returns the OpenGL constant associated to the shader type.
         *
         * @return The OpenGL constant
         */
        public int getGLConstant() {
            return glConstant;
        }
    }
}
