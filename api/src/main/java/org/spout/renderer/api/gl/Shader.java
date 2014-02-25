/*
 * This file is part of Caustic API.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic API is licensed under the Spout License Version 1.
 *
 * Caustic API is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic API is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.api.gl;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;

import org.spout.renderer.api.Creatable;
import org.spout.renderer.api.GLVersioned;
import org.spout.renderer.api.data.ShaderSource;

/**
 * Represents an OpenGL shader. The shader source and type must be set with {@link #setSource(org.spout.renderer.api.data.ShaderSource)}.
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

    public abstract void setAttributeLayout(String attribute, int layout);

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
