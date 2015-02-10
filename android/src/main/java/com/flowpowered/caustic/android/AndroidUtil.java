/*
 * This file is part of Caustic Android, licensed under the MIT License (MIT).
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
