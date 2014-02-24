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
package org.spout.renderer.lwjgl.gl21;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import org.spout.renderer.api.data.ShaderSource;
import org.spout.renderer.api.gl.Shader;
import org.spout.renderer.lwjgl.LWJGLUtil;

/**
 * An OpenGL 2.1 implementation of {@link Shader}.
 *
 * @see Shader
 */
public class GL21Shader extends Shader {
    private ShaderSource source;
    private ShaderType currentType;

    protected GL21Shader() {
    }

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
        currentType = null;
        // Delete the source
        source = null;
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
        if (id == 0 || currentType != type) {
            // Delete the old shader
            GL20.glDeleteShader(id);
            // Create a shader of the correct type
            id = GL20.glCreateShader(type.getGLConstant());
            // Store the current type
            currentType = type;
        }
        // Upload the new source
        GL20.glShaderSource(id, source.getSource());
        // Compile the shader
        GL20.glCompileShader(id);
        // Get the shader compile status property, check it's false and fail if that's the case
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new IllegalStateException("OPEN GL ERROR: Could not compile shader\n" + GL20.glGetShaderInfoLog(id, 1000));
        }
        // Check for errors
        LWJGLUtil.checkForGLError();
        // Finally set the source now that we know it's valid
        this.source = source;
    }

    @Override
    public ShaderType getType() {
        return currentType;
    }

    @Override
    public TObjectIntMap<String> getAttributeLayouts() {
        return source != null ? source.getAttributeLayouts() : null;
    }

    @Override
    public TIntObjectMap<String> getTextureLayouts() {
        return source != null ? source.getTextureLayouts() : null;
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL21;
    }
}
