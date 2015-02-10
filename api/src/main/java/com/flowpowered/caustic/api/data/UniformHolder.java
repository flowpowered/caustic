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
package com.flowpowered.caustic.api.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a set of uniforms held by an object. Uniforms can be added, removed and modified.
 */
public class UniformHolder implements Iterable<Uniform> {
    private final Map<String, Uniform> uniforms = new HashMap<>();

    /**
     * Adds a uniform to the holder.
     *
     * @param uniform The uniform to add
     */
    public void add(Uniform uniform) {
        uniforms.put(uniform.name, uniform);
    }

    /**
     * Adds all the uniforms to the holder.
     *
     * @param uniforms The uniforms to add
     */
    public void addAll(UniformHolder uniforms) {
        for (Uniform uniform : uniforms) {
            add(uniform);
        }
    }

    /**
     * Returns true if the holder has a uniform with the provided name
     *
     * @param name The name to lookup
     * @return Whether or not this holder has a uniform with that name
     */
    public boolean has(String name) {
        return uniforms.containsKey(name);
    }

    /**
     * Returns the uniform with the provided name, or null if non can be found.
     *
     * @param name The name to lookup
     * @return The uniform
     */
    @SuppressWarnings("unchecked")
    public <U extends Uniform> U get(String name) {
        final Uniform uniform = uniforms.get(name);
        if (uniform == null) {
            return null;
        }
        return (U) uniform;
    }

    /**
     * Remove the uniform from the holder, if present.
     *
     * @param uniform The uniform to remove
     */
    public void remove(Uniform uniform) {
        remove(uniform.getName());
    }

    /**
     * Remove the uniform from the holder with the provided name, if present.
     *
     * @param name The name of the uniform to remove
     */
    public void remove(String name) {
        uniforms.remove(name);
    }

    /**
     * Removes all the uniforms.
     */
    public void clear() {
        uniforms.clear();
    }

    @Override
    public Iterator<Uniform> iterator() {
        return uniforms.values().iterator();
    }
}
