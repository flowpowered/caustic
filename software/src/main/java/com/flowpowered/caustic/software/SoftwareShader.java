/*
 * This file is part of Caustic Software.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic Software is licensed under the Spout License Version 1.
 *
 * Caustic Software is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic Software is distributed in the hope that it will be useful, but WITHOUT ANY
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
package com.flowpowered.caustic.software;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;

import com.flowpowered.caustic.api.data.ShaderSource;
import com.flowpowered.caustic.api.gl.Shader;

/**
 *
 */
public class SoftwareShader extends Shader {
    private Class<? extends ShaderImplementation> shaderClass;
    private ShaderImplementation shader;

    @Override
    @SuppressWarnings("unchecked")
    public void setSource(ShaderSource source) {
        try {
            shaderClass = (Class<? extends ShaderImplementation>) Class.forName(source.getSource().toString());
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Shader source not found", ex);
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("Shader class not of type " + ShaderImplementation.class.getCanonicalName());
        }
    }

    @Override
    public void compile() {
        try {
            shader = shaderClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Cannot instantiate new shader from shader class " + shaderClass.getCanonicalName(), ex);
        }
        shader.doReflection();
    }

    protected ShaderImplementation getImplementation() {
        return shader;
    }

    @Override
    public ShaderType getType() {
        return shader != null ? shader.getType() : null;
    }

    @Override
    public TObjectIntMap<String> getAttributeLayouts() {
        return null;
    }

    @Override
    public TIntObjectMap<String> getTextureLayouts() {
        return null;
    }

    @Override
    public void setAttributeLayout(String attribute, int layout) {

    }

    @Override
    public void setTextureLayout(int unit, String sampler) {

    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.SOFTWARE;
    }
}
