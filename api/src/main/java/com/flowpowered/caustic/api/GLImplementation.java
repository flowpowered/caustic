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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import com.flowpowered.caustic.api.GLVersioned.GLVersion;
import com.flowpowered.caustic.api.gl.Context;
import com.flowpowered.caustic.api.util.CausticUtil;

/**
 * Represents and implementation of an OpenGL or OpenGLES version. Also manages the implementations statically.
 */
public class GLImplementation {
    private static final Map<GLImplementation, Constructor<Context>> implementations = Collections.synchronizedMap(new HashMap<GLImplementation, Constructor<Context>>());
    private final GLVersion version;
    private final String contextName;

    /**
     * Constructs a new implementation from the version and the context class name.
     *
     * @param version The version implementation
     * @param contextName The context class name
     */
    public GLImplementation(GLVersion version, String contextName) {
        this.version = version;
        this.contextName = contextName;
    }

    /**
     * Returns the {@link com.flowpowered.caustic.api.GLVersioned.GLVersion} of this implementation.
     *
     * @return The version
     */
    public GLVersion getVersion() {
        return version;
    }

    /**
     * Returns the full name of the context class.
     *
     * @return The name of the context class
     */
    public String getContextName() {
        return contextName;
    }

    /**
     * Loads the implementation, making it accessible.
     *
     * @param implementation The implementation to load
     * @return Whether or not the loading succeeded
     */
    @SuppressWarnings("unchecked")
    public static boolean load(GLImplementation implementation) {
        try {
            final Constructor<?> constructor = Class.forName(implementation.getContextName()).getDeclaredConstructor();
            constructor.setAccessible(true);
            implementations.put(implementation, (Constructor<Context>) constructor);
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
            CausticUtil.getCausticLogger().log(Level.WARNING, "Couldn't load implementation", ex);
            return false;
        }
    }

    /**
     * Returns a {@link com.flowpowered.caustic.api.gl.Context} for the {@link GLImplementation}, loading it if necessary.
     *
     * @param glImplementation The GL implementation to look up a factory for
     * @return The implementation, as a {@link com.flowpowered.caustic.api.gl.Context} or null if it couldn't be loaded
     */
    public static Context get(GLImplementation glImplementation) {
        if (!implementations.containsKey(glImplementation)) {
            if (!load(glImplementation)) {
                return null;
            }
        }
        try {
            return implementations.get(glImplementation).newInstance();
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException ex) {
            CausticUtil.getCausticLogger().log(Level.WARNING, "Couldn't create Context from loaded implementation class", ex);
            return null;
        }
    }

    /**
     * Returns an unmodifiable set of all the available implementations.
     *
     * @return The available implementations
     */
    public static Set<GLImplementation> getAvailableImplementations() {
        return Collections.unmodifiableSet(implementations.keySet());
    }
}
