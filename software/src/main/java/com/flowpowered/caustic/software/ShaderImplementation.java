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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.api.gl.Shader.ShaderType;

/**
 *
 */
public abstract class ShaderImplementation {
    private final DataFormat[] outputFormat;
    private final Map<String, Field> uniforms = new HashMap<>();
    private final TIntObjectMap<Sampler> samplers = new TIntObjectHashMap<>();

    protected ShaderImplementation() {
        this(null);
    }

    protected ShaderImplementation(DataFormat[] outputFormat) {
        if (getType() == ShaderType.VERTEX) {
            if (outputFormat == null || outputFormat.length <= 0 || outputFormat[0].getCount() != 4 || outputFormat[0].getType() != DataType.FLOAT) {
                throw new IllegalArgumentException("Vertex shader output format must have 4 floats as the first output type in the declared format");
            }
        }
        this.outputFormat = outputFormat;
    }

    public abstract void main(InBuffer in, OutBuffer out);

    public abstract ShaderType getType();

    void doReflection() {
        findUniforms();
        findSamplers();
    }

    private void findUniforms() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Uniform.class)) {
                field.setAccessible(true);
                uniforms.put(field.getName(), field);
            }
        }
    }

    private void findSamplers() {
        for (Field field : getClass().getDeclaredFields()) {
            if (Sampler.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                final Sampler sampler;
                try {
                    sampler = (Sampler) field.get(this);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Can't access sampler \"" + field.getName() + "\" in shader implementation", ex);
                }
                if (sampler == null) {
                    throw new IllegalArgumentException("Sampler \"" + field.getName() + "\" hasn't been initialized");
                }
                final TextureLayout layoutAnnotation = field.getAnnotation(TextureLayout.class);
                final int layout;
                if (layoutAnnotation != null) {
                    layout = layoutAnnotation.value();
                } else {
                    layout = samplers.size();
                }
                samplers.put(layout, sampler);
            }
        }
    }

    DataFormat[] getOutputFormat() {
        return outputFormat;
    }

    void setUniform(String name, Object o) {
        final Field field = uniforms.get(name);
        if (field != null) {
            try {
                field.set(this, o);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException("Could not set uniform in shader", ex);
            } catch (IllegalArgumentException ex) {
                throw new IllegalStateException("Uniform \"" + name + "\" is not of type \"" + o.getClass().getCanonicalName() + "\"", ex);
            }
        }
    }

    void bindTexture(int unit, SoftwareTexture texture) {
        final Sampler sampler = samplers.get(unit);
        if (sampler != null) {
            sampler.setTexture(texture);
        }
    }

    Set<String> getUniformNames() {
        return uniforms.keySet();
    }
}
