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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

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
    private static final String[] WINDOWS_NATIVE_LIBRARIES = {
            "jinput-dx8_64.dll", "jinput-dx8.dll", "jinput-raw_64.dll", "jinput-raw.dll",
            "jinput-wintab.dll", "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll"
    };
    private static final String WINDOWS_NATIVES_DIRECTORY = "natives/windows/";
    private static final String[] MACOSX_NATIVE_LIBRARIES = {
            "libjinput-osx.jnilib", "liblwjgl.jnilib", "openal.dylib"
    };
    private static final String MACOSX_NATIVES_DIRECTORY = "natives/mac/";
    private static final String[] LINUX_NATIVE_LIBRARIES = {
            "liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so", "libjinput-linux.so",
            "libjinput-linux64.so"
    };
    private static final String LINUX_NATIVES_DIRECTORY = "natives/linux/";

    private LWJGLUtil() {
    }

    /**
     * Attempts to deploy the LWJGL native libraries into the given directory. The sub-directory "natives/PLATFORM_NAME" (where PLATFORM_NAME is the name of the platform) will be created inside, and
     * will contain the natives. The path to the natives property will be set, but the libraries not loaded. If any error is encountered while copying the natives, the operation will be aborted and
     * the method will return false.
     *
     * @param directory The directory into which to deploy the natives. Can be null if the path to use is the working directory
     * @return Whether or not the operation succeeded.
     */
    public static boolean deployNatives(File directory) {
        final String[] nativeLibs = getNativeLibraries();
        final String nativesDirectory = getNativesDirectory();
        final File nativesDir = new File(directory, nativesDirectory);
        nativesDir.mkdirs();
        for (String nativeLib : nativeLibs) {
            final File nativeFile = new File(nativesDir, nativeLib);
            if (!nativeFile.exists()) {
                try {
                    Files.copy(LWJGLUtil.class.getResourceAsStream("/" + nativeLib), nativeFile.toPath());
                } catch (IOException ex) {
                    CausticUtil.getCausticLogger().log(Level.SEVERE, "Failed to copy native library file", ex);
                    return false;
                }
            }
        }
        final String nativesPath = nativesDir.getAbsolutePath();
        System.setProperty("org.lwjgl.librarypath", nativesPath);
        System.setProperty("net.java.games.input.librarypath", nativesPath);
        return true;
    }

    private static String[] getNativeLibraries() {
        final int platform = org.lwjgl.LWJGLUtil.getPlatform();
        if (platform == org.lwjgl.LWJGLUtil.PLATFORM_WINDOWS) {
            return WINDOWS_NATIVE_LIBRARIES;
        } else if (platform == org.lwjgl.LWJGLUtil.PLATFORM_MACOSX) {
            return MACOSX_NATIVE_LIBRARIES;
        } else if (platform == org.lwjgl.LWJGLUtil.PLATFORM_LINUX) {
            return LINUX_NATIVE_LIBRARIES;
        } else {
            throw new IllegalStateException("Could not get lwjgl natives for platform \"" + org.lwjgl.LWJGLUtil.getPlatformName() + "\".");
        }
    }

    private static String getNativesDirectory() {
        final int platform = org.lwjgl.LWJGLUtil.getPlatform();
        if (platform == org.lwjgl.LWJGLUtil.PLATFORM_WINDOWS) {
            return WINDOWS_NATIVES_DIRECTORY;
        } else if (platform == org.lwjgl.LWJGLUtil.PLATFORM_MACOSX) {
            return MACOSX_NATIVES_DIRECTORY;
        } else if (platform == org.lwjgl.LWJGLUtil.PLATFORM_LINUX) {
            return LINUX_NATIVES_DIRECTORY;
        } else {
            throw new IllegalStateException("Could not get lwjgl natives for platform \"" + org.lwjgl.LWJGLUtil.getPlatformName() + "\".");
        }
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
