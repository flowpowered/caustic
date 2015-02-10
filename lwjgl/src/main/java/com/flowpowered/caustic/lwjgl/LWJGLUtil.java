/*
 * This file is part of Caustic LWJGL, licensed under the MIT License (MIT).
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
package com.flowpowered.caustic.lwjgl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.flowpowered.caustic.api.GLImplementation;
import com.flowpowered.caustic.api.GLVersioned.GLVersion;
import com.flowpowered.caustic.api.util.CausticUtil;
import com.flowpowered.caustic.lwjgl.gl20.GL20Context;
import com.flowpowered.caustic.lwjgl.gl21.GL21Context;
import com.flowpowered.caustic.lwjgl.gl30.GL30Context;
import com.flowpowered.caustic.lwjgl.gl32.GL32Context;

public final class LWJGLUtil {
    public static final GLImplementation GL20_IMPL = new GLImplementation(GLVersion.GL20, GL20Context.class.getName());
    public static final GLImplementation GL21_IMPL = new GLImplementation(GLVersion.GL21, GL21Context.class.getName());
    public static final GLImplementation GL30_IMPL = new GLImplementation(GLVersion.GL30, GL30Context.class.getName());
    public static final GLImplementation GL32_IMPL = new GLImplementation(GLVersion.GL32, GL32Context.class.getName());
    private static final String NATIVES_DIRECTORY = "natives";
    private static final String[] WINDOWS_NATIVE_LIBRARIES = {
            "jinput-dx8_64.dll", "jinput-dx8.dll", "jinput-raw_64.dll", "jinput-raw.dll",
            "jinput-wintab.dll", "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll"
    };
    private static final String WINDOWS_NATIVES_DIRECTORY = NATIVES_DIRECTORY + File.separator + "windows";
    private static final String[] MACOSX_NATIVE_LIBRARIES = {
            "libjinput-osx.jnilib", "liblwjgl.jnilib", "openal.dylib"
    };
    private static final String MACOSX_NATIVES_DIRECTORY = NATIVES_DIRECTORY + File.separator + "mac";
    private static final String[] LINUX_NATIVE_LIBRARIES = {
            "liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so", "libjinput-linux.so",
            "libjinput-linux64.so"
    };
    private static final String LINUX_NATIVES_DIRECTORY = NATIVES_DIRECTORY + File.separator + "linux";

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
        final File nativesDir = new File(directory, getNativesDirectory());
        nativesDir.mkdirs();
        for (String nativeLib : getNativeLibraries()) {
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
