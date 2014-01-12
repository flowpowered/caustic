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
/*
 * This file is part of Caustic.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic is licensed under the Spout License Version 1.
 *
 * Caustic is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic is distributed in the hope that it will be useful, but WITHOUT ANY
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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spout.renderer.api.GLVersioned.GLVersion;
import org.spout.renderer.api.gl.GLFactory;

/**
 * A manager for the implementations of the various OpenGL and OpenGLES versions.
 */
public final class GLImplementation {
    private static final Logger logger = Logger.getLogger("GLImplementation.logger");
    private static final Map<GLVersion, GLFactory> implementations = new EnumMap<>(GLVersion.class);

    private GLImplementation() {
    }

    static {
        loadDefaults();
    }

    /**
     * Registers an implementation for a version. Only one implementation per version is allowed. Any previous one is replaced.
     *
     * @param glVersion The GL version of the implementation
     * @param glFactory The factory for the implementation
     */
    public static void register(GLVersion glVersion, GLFactory glFactory) {
        implementations.put(glVersion, glFactory);
    }

    /**
     * Returns a {@link GLFactory} for the {@link GLVersion}, or null if none has been registered.
     *
     * @param glVersion The GL version to look up an implementation for
     * @return The implementation, as a {@link GLFactory}
     */
    public static GLFactory get(GLVersion glVersion) {
        return implementations.get(glVersion);
    }

    public static Set<GLVersion> getAvailableVersions() {
        return Collections.unmodifiableSet(implementations.keySet());
    }

    private static void loadDefaults() {
        tryLoadClass(GLVersion.GL20, "lwjgl.gl20.GL20GLFactory");
        tryLoadClass(GLVersion.GL30, "lwjgl.gl30.GL30GLFactory");
        tryLoadClass(GLVersion.GLES20, "android.gles20.GLES20GLFactory");
        //tryLoadClass(GLVersion.GLES30, "android.gles30.GLES30GLFactory");
    }

    private static void tryLoadClass(GLVersion version, String localPkg) {
        try {
            Class.forName("org.spout.renderer." + localPkg);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, "Default " + version + " implementation \"" + localPkg + "\" not found");
            return;
        }
        logger.log(Level.INFO, "Loaded default " + version + " implementation \"" + localPkg + "\"");
    }
}
