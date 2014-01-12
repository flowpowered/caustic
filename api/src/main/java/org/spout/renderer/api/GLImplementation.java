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

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spout.renderer.api.GLVersioned.GLVersion;
import org.spout.renderer.api.gl.GLFactory;

/**
 * Represents and implementation of an OpenGL or OpenGLES version. Also manages the implementations statically.
 */
public final class GLImplementation {
    private static final Map<GLImplementation, GLFactory> implementations = Collections.synchronizedMap(new HashMap<GLImplementation, GLFactory>());
    private final GLVersion version;
    private final String factoryName;

    /**
     * Constructs a new implementation from the version and the factory class name.
     *
     * @param version The version implementation
     * @param factoryName The factory class name
     */
    public GLImplementation(GLVersion version, String factoryName) {
        this.version = version;
        this.factoryName = factoryName;
    }

    /**
     * Returns the {@link org.spout.renderer.api.GLVersioned.GLVersion} of this implementation.
     *
     * @return The version
     */
    public GLVersion getVersion() {
        return version;
    }

    /**
     * Returns the full name of the factor class.
     *
     * @return The name of the factory class
     */
    public String getFactoryName() {
        return factoryName;
    }

    /**
     * Loads the implementation, making it accessible.
     *
     * @param implementation The implementation to load
     * @return Whether or not the loading succeeded
     */
    public static boolean load(GLImplementation implementation) {
        final Class<?> clazz = loadClass(implementation.getFactoryName());
        if (clazz == null) {
            return false;
        }
        final GLFactory factory;
        try {
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            factory = (GLFactory) constructor.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        implementations.put(implementation, factory);
        return true;
    }

    /**
     * Returns a {@link GLFactory} for the {@link GLImplementation}, loading it if necessary.
     *
     * @param glImplementation The GL implementation to look up a factory for
     * @return The implementation, as a {@link GLFactory} or null if it couldn't be loaded
     */
    public static GLFactory get(GLImplementation glImplementation) {
        if (!implementations.containsKey(glImplementation)) {
            if (!load(glImplementation)) {
                return null;
            }
        }
        return implementations.get(glImplementation);
    }

    /**
     * Returns an unmodifiable set of all the available implementations.
     *
     * @return The available implementations
     */
    public static Set<GLImplementation> getAvailableImplementations() {
        return Collections.unmodifiableSet(implementations.keySet());
    }

    private static Class<?> loadClass(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
