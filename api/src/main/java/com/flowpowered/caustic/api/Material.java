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
package com.flowpowered.caustic.api;

import java.util.concurrent.atomic.AtomicInteger;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import com.flowpowered.caustic.api.data.UniformHolder;
import com.flowpowered.caustic.api.gl.Program;
import com.flowpowered.caustic.api.gl.Texture;

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
        program.use();
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
