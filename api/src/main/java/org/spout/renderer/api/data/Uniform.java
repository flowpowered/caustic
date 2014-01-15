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
package org.spout.renderer.api.data;

import com.flowpowered.math.matrix.Matrix2f;
import com.flowpowered.math.matrix.Matrix3f;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;

import org.spout.renderer.api.gl.Program;

/**
 * Represents a shader uniform, which has a name and a value.
 */
public abstract class Uniform {
    protected final String name;

    protected Uniform(String name) {
        this.name = name;
    }

    /**
     * Uploads this uniform to the program.
     *
     * @param program The program to upload to
     */
    public abstract void upload(Program program);

    /**
     * Returns the name of the uniform.
     *
     * @return The uniform's name
     */
    public String getName() {
        return name;
    }

    /**
     * Represents a uniform with a boolean value.
     */
    public static class BooleanUniform extends Uniform {
        private boolean value;

        /**
         * Constructs a new boolean uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public BooleanUniform(String name, boolean value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public boolean get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(boolean value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a int value.
     */
    public static class IntUniform extends Uniform {
        private int value;

        /**
         * Constructs a new int uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public IntUniform(String name, int value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public int get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(int value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a float value.
     */
    public static class FloatUniform extends Uniform {
        private float value;

        /**
         * Constructs a new float uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public FloatUniform(String name, float value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public float get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(float value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a vector2 value.
     */
    public static class Vector2Uniform extends Uniform {
        private Vector2f value;

        /**
         * Constructs a new vector2 uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public Vector2Uniform(String name, Vector2f value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Vector2f get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Vector2f value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a vector2 array value.
     */
    public static class Vector2ArrayUniform extends Uniform {
        private Vector2f[] value;

        /**
         * Constructs a new vector2 array uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public Vector2ArrayUniform(String name, Vector2f[] value) {
            super(name);
            this.value = new Vector2f[value.length];
            System.arraycopy(value, 0, this.value, 0, value.length);
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Vector2f[] get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Vector2f[] value) {
            this.value = new Vector2f[value.length];
            System.arraycopy(value, 0, this.value, 0, value.length);
        }
    }

    /**
     * Represents a uniform with a vector3 value.
     */
    public static class Vector3Uniform extends Uniform {
        private Vector3f value;

        /**
         * Constructs a new vector3 uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public Vector3Uniform(String name, Vector3f value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Vector3f get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Vector3f value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a vector3 array value.
     */
    public static class Vector3ArrayUniform extends Uniform {
        private Vector3f[] value;

        /**
         * Constructs a new vector3 array uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public Vector3ArrayUniform(String name, Vector3f[] value) {
            super(name);
            this.value = new Vector3f[value.length];
            System.arraycopy(value, 0, this.value, 0, value.length);
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Vector3f[] get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Vector3f[] value) {
            this.value = new Vector3f[value.length];
            System.arraycopy(value, 0, this.value, 0, value.length);
        }
    }

    /**
     * Represents a uniform with a vector4 value.
     */
    public static class Vector4Uniform extends Uniform {
        private Vector4f value;

        /**
         * Constructs a new vector4 uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public Vector4Uniform(String name, Vector4f value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Vector4f get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Vector4f value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a matrix2 value.
     */
    public static class Matrix2Uniform extends Uniform {
        private Matrix2f value;

        /**
         * Constructs a new matrix2 uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public Matrix2Uniform(String name, Matrix2f value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Matrix2f get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Matrix2f value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a boolean matrix3.
     */
    public static class Matrix3Uniform extends Uniform {
        private Matrix3f value;

        /**
         * Constructs a new matrix3 uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public Matrix3Uniform(String name, Matrix3f value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Matrix3f get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Matrix3f value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a matrix4 value.
     */
    public static class Matrix4Uniform extends Uniform {
        private Matrix4f value;

        /**
         * Constructs a new matrix4 uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public Matrix4Uniform(String name, Matrix4f value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Matrix4f get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Matrix4f value) {
            this.value = value;
        }
    }

    /**
     * Represents a uniform with a color value.
     */
    public static class ColorUniform extends Uniform {
        private Color value;

        /**
         * Constructs a new color uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public ColorUniform(String name, Color value) {
            super(name);
            this.value = value;
        }

        @Override
        public void upload(Program program) {
            program.setUniform(name, value);
        }

        /**
         * Returns the value of the uniform.
         *
         * @return The value
         */
        public Color get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(Color value) {
            this.value = value;
        }
    }
}
