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
package com.flowpowered.caustic.api.data;

import com.flowpowered.math.matrix.Matrix2f;
import com.flowpowered.math.matrix.Matrix3f;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.gl.Program;

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
     * Represents a uniform with a float array value.
     */
    public static class FloatArrayUniform extends Uniform {
        private float[] value;

        /**
         * Constructs a new float array uniform from the name and the value.
         *
         * @param name The name of the uniform
         * @param value Its value
         */
        public FloatArrayUniform(String name, float[] value) {
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
        public float[] get() {
            return value;
        }

        /**
         * Sets the value of the uniform.
         *
         * @param value The value
         */
        public void set(float[] value) {
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
}
