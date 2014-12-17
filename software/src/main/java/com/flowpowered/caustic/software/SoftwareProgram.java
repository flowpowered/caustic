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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.flowpowered.math.matrix.Matrix2f;
import com.flowpowered.math.matrix.Matrix3f;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.gl.Program;
import com.flowpowered.caustic.api.gl.Shader;
import com.flowpowered.caustic.api.gl.Shader.ShaderType;
import com.flowpowered.caustic.api.util.CausticUtil;

/**
 *
 */
public class SoftwareProgram extends Program {
    private final SoftwareRenderer renderer;
    private final Map<ShaderType, SoftwareShader> shaders = new EnumMap<>(ShaderType.class);

    SoftwareProgram(SoftwareRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void attachShader(Shader shader) {
        CausticUtil.checkVersion(this, shader);
        shaders.put(shader.getType(), (SoftwareShader) shader);
    }

    SoftwareShader getShader(ShaderType type) {
        return shaders.get(type);
    }

    @Override
    public void detachShader(Shader shader) {
        final Shader found = shaders.get(shader.getType());
        if (shader.equals(found)) {
            shaders.remove(shader.getType());
        }
    }

    @Override
    public void link() {
        // Nothing to do
    }

    @Override
    public void use() {
        renderer.setProgram(this);
    }

    @Override
    public void bindSampler(int unit) {
        final SoftwareTexture texture = renderer.getTexture(unit);
        if (texture == null) {
            throw new IllegalArgumentException("No texture bound at unit " + unit);
        }
        for (SoftwareShader shader : shaders.values()) {
            shader.getImplementation().bindTexture(unit, texture);
        }
    }

    @Override
    public void setUniform(String name, boolean b) {
        setUniform(name, (Object) b);
    }

    @Override
    public void setUniform(String name, int i) {
        setUniform(name, (Object) i);
    }

    @Override
    public void setUniform(String name, float f) {
        setUniform(name, (Object) f);
    }

    @Override
    public void setUniform(String name, float[] fs) {
        setUniform(name, (Object) fs);
    }

    @Override
    public void setUniform(String name, Vector2f v) {
        setUniform(name, (Object) v);
    }

    @Override
    public void setUniform(String name, Vector2f[] vs) {
        setUniform(name, (Object) vs);
    }

    @Override
    public void setUniform(String name, Vector3f v) {
        setUniform(name, (Object) v);
    }

    @Override
    public void setUniform(String name, Vector3f[] vs) {
        setUniform(name, (Object) vs);
    }

    @Override
    public void setUniform(String name, Vector4f v) {
        setUniform(name, (Object) v);
    }

    @Override
    public void setUniform(String name, Matrix2f m) {
        setUniform(name, (Object) m);
    }

    @Override
    public void setUniform(String name, Matrix3f m) {
        setUniform(name, (Object) m);
    }

    @Override
    public void setUniform(String name, Matrix4f m) {
        setUniform(name, (Object) m);
    }

    private void setUniform(String name, Object o) {
        for (SoftwareShader shader : shaders.values()) {
            shader.getImplementation().setUniform(name, o);
        }
    }

    @Override
    public Collection<SoftwareShader> getShaders() {
        return Collections.unmodifiableCollection(shaders.values());
    }

    @Override
    public Set<String> getUniformNames() {
        final Set<String> names = new HashSet<>();
        for (SoftwareShader shader : shaders.values()) {
            names.addAll(shader.getImplementation().getUniformNames());
        }
        return names;
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.SOFTWARE;
    }
}
