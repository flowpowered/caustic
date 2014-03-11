/*
 * This file is part of Caustic LWJGL.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic LWJGL is licensed under the Spout License Version 1.
 *
 * Caustic LWJGL is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic LWJGL is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.lwjgl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import org.spout.renderer.api.GLImplementation;
import org.spout.renderer.api.GLVersioned.GLVersion;
import org.spout.renderer.api.util.CausticUtil;
import org.spout.renderer.lwjgl.gl20.GL20Context;
import org.spout.renderer.lwjgl.gl21.GL21Context;
import org.spout.renderer.lwjgl.gl30.GL30Context;
import org.spout.renderer.lwjgl.gl32.GL32Context;

public final class LWJGLUtil {
    public static final GLImplementation GL20_IMPL = new GLImplementation(GLVersion.GL20, GL20Context.class.getName());
    public static final GLImplementation GL21_IMPL = new GLImplementation(GLVersion.GL21, GL21Context.class.getName());
    public static final GLImplementation GL30_IMPL = new GLImplementation(GLVersion.GL30, GL30Context.class.getName());
    public static final GLImplementation GL32_IMPL = new GLImplementation(GLVersion.GL32, GL32Context.class.getName());

    private LWJGLUtil() {
    }

    /**
     * Throws an exception if OpenGL reports an error.
     *
     * @throws GLException If OpenGL reports an error
     */
    public static void checkForGLError() {
        if (CausticUtil.isDebugEnabled()) {
            final int errorValue = GL11.glGetError();
            if (errorValue != GL11.GL_NO_ERROR) {
                throw new GLException("GL ERROR: " + GLU.gluErrorString(errorValue));
            }
        }
    }

    /**
     * An exception throw when a GL exception occurs on Android.
     */
    public static class GLException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        /**
         * Constructs a new Android GL exception from the message.
         *
         * @param message The error message
         */
        public GLException(String message) {
            super(message);
        }
    }
}
