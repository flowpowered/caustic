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

/**
 * Represents an object that has an OpenGL version associated to it.
 */
public interface GLVersioned {
    /**
     * Returns the lowest OpenGL version required by this object's implementation.
     *
     * @return The lowest required OpenGL version
     */
    GLVersion getGLVersion();

    /**
     * An enum of the existing OpenGL versions. Use this class to generate rendering objects compatible with the version.
     */
    public static enum GLVersion {
        GL11(1, 1, false),
        GL12(1, 2, false),
        GL13(1, 3, false),
        GL14(1, 4, false),
        GL15(1, 5, false),
        GL20(2, 0, false),
        GL21(2, 1, false),
        GL30(3, 0, false),
        GL31(3, 1, false),
        GL32(3, 2, false),
        GL33(3, 3, false),
        GL40(4, 0, false),
        GL41(4, 1, false),
        GL42(4, 2, false),
        GL43(4, 3, false),
        GL44(4, 4, false),
        GLES10(1, 0, true),
        GLES20(2, 0, true),
        GLES30(3, 0, true);
        private final int major;
        private final int minor;
        private final boolean es;

        private GLVersion(int major, int minor, boolean es) {
            this.major = major;
            this.minor = minor;
            this.es = es;
        }

        /**
         * Returns the major version number of the version.
         *
         * @return The major version number
         */
        public int getMajor() {
            return major;
        }

        /**
         * Returns the minor version number of the version.
         *
         * @return The minor version number
         */
        public int getMinor() {
            return minor;
        }

        /**
         * Returns true if the version is ES compatible, false if not.
         *
         * @return Whether or not this is an ES compatible version
         */
        public boolean isES() {
            return es;
        }
    }
}
