/*
 * This file is part of Caustic Android.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic Android is licensed under the Spout License Version 1.
 *
 * Caustic Android is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic Android is distributed in the hope that it will be useful, but WITHOUT ANY
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
package com.flowpowered.caustic.android;

import android.opengl.GLES20;
import android.opengl.GLU;

public final class AndroidUtil {
    //public static final GLImplementation GLES20_IMPL = new GLImplementation(GLVersion.GLES20, GLES20GLFactory.class.getName());
    private static boolean debug = true;

    private AndroidUtil() {
    }

    /**
     * Sets the caustic renderer in debug mode.
     *
     * @param enabled If debug should be enabled
     */
    public static void setDebugEnabled(boolean enabled) {
        debug = enabled;
    }

    /**
     * Throws an exception if OpenGL reports an error.
     *
     * @throws com.flowpowered.caustic.android.AndroidUtil.GLESException If OpenGL reports an error
     */
    public static void checkForGLESError() {
        if (debug) {
            final int errorValue = GLES20.glGetError();
            if (errorValue != GLES20.GL_NO_ERROR) {
                throw new GLESException("OPEN GL ERROR: " + GLU.gluErrorString(errorValue));
            }
        }
    }

    /**
     * An exception throw when a GLES exception occurs on Android.
     */
    public static class GLESException extends RuntimeException {
        /**
         * Constructs a new Android GLES exception from the message.
         *
         * @param message The error message
         */
        public GLESException(String message) {
            super(message);
        }
    }
}
