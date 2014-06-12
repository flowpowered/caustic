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
package org.spout.renderer.software;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.spout.renderer.api.data.VertexAttribute.DataType;
import org.spout.renderer.api.gl.Shader.ShaderType;

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
