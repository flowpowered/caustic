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
package org.spout.renderer.api;

import java.util.concurrent.atomic.AtomicInteger;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.spout.renderer.api.data.UniformHolder;
import org.spout.renderer.api.gl.Program;
import org.spout.renderer.api.gl.Texture;

/**
 * Represents an OpenGL material. Materials are assigned to models, and these can share the same material. The material provides the shader program to use when rendering the models, the texture for
 * each unit (if any) and a set of uniforms that will be constant for all models using the material.
 */
public class Material implements Comparable<Material> {
    // Reflects the current available ID
    private static final AtomicInteger ID_COUNTER = new AtomicInteger();
    // private ID for batching models per material for rendering
    private final int id = ID_COUNTER.getAndIncrement();
    // Shader program
    private Program program;
    // Textures by unit
    private TIntObjectMap<Texture> textures;
    // Material uniforms
    private final UniformHolder uniforms = new UniformHolder();

    public Material(Program program) {
        if (program == null) {
            throw new IllegalStateException("Program cannot be null");
        }
        program.checkCreated();
        this.program = program;
    }

    /**
     * Binds the material to the OpenGL context.
     */
    public void bind() {
        program.bind();
        if (textures != null) {
            final TIntObjectIterator<Texture> iterator = textures.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                // Bind the texture to the unit
                final int unit = iterator.key();
                iterator.value().bind(unit);
                // Bind the shader sampler uniform to the unit
                program.bindSampler(unit);
            }
        }
    }

    /**
     * Unbinds the material from the OpenGL context.
     */
    public void unbind() {
        program.unbind();
        if (textures != null) {
            for (Texture texture : textures.valueCollection()) {
                texture.unbind();
            }
        }
    }

    /**
     * Uploads the material's uniforms to its program.
     */
    public void uploadUniforms() {
        program.upload(uniforms);
    }

    /**
     * Sets the program to be used by this material to shade the models.
     *
     * @param program The program to use
     */
    public void setProgram(Program program) {
        if (program == null) {
            throw new IllegalStateException("Program cannot be null");
        }
        program.checkCreated();
        this.program = program;
    }

    /**
     * Returns the material's program.
     *
     * @return The program
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Adds a texture to the material. If a texture is a already present in the same unit as this one, it will be replaced.
     *
     * @param unit The unit to add the texture to
     * @param texture The texture to add
     */
    public void addTexture(int unit, Texture texture) {
        if (texture == null) {
            throw new IllegalStateException("Texture cannot be null");
        }
        texture.checkCreated();
        if (textures == null) {
            textures = new TIntObjectHashMap<>();
        }
        textures.put(unit, texture);
    }

    /**
     * Returns true if a texture is present in the unit.
     *
     * @param unit The unit to check
     * @return Whether or not a texture is present
     */
    public boolean hasTexture(int unit) {
        return textures != null && textures.containsKey(unit);
    }

    /**
     * Returns the texture in the unit, or null if none is present.
     *
     * @param unit The unit to check
     * @return The texture
     */
    public Texture getTexture(int unit) {
        return textures != null ? textures.get(unit) : null;
    }

    /**
     * Removed the texture in the unit, if present.
     *
     * @param unit The unit to remove the texture from
     */
    public void removeTexture(int unit) {
        if (textures != null) {
            textures.remove(unit);
        }
    }

    /**
     * Returns the uniform holder for this material.
     *
     * @return The uniforms
     */
    public UniformHolder getUniforms() {
        return uniforms;
    }

    @Override
    public int compareTo(Material that) {
        return this.id - that.id;
    }
}
