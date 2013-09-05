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
package org.spout.renderer.android;

import android.opengl.GLES20;
import android.opengl.GLU;

import org.spout.renderer.GLVersioned;

public final class AndroidUtil {
	private AndroidUtil() {
	}

	private static boolean debug = true;

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
	 * @throws org.spout.renderer.android.AndroidUtil.GLESException If OpenGL reports an error
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
	 * Checks if two OpenGL versioned object have the same version. Throws an exception if that's not the case.
	 *
	 * @param required The required version
	 * @param object The object to check the version of
	 * @throws IllegalStateException If the object versions to not match
	 */
	public static void checkVersion(GLVersioned required, GLVersioned object) {
		if (required.getGLVersion() != object.getGLVersion()) {
			throw new IllegalStateException("Version mismatch: expected " + required.getGLVersion() + ", got " + object.getGLVersion());
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
