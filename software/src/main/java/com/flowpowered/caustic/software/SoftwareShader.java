/*
 * This file is part of Caustic Software, licensed under the MIT License (MIT).
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
