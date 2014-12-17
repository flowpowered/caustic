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
