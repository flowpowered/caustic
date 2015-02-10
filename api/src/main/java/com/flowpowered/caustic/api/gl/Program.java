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
package com.flowpowered.caustic.api.gl;

import java.util.Collection;
import java.util.Set;

import com.flowpowered.math.matrix.Matrix2f;
import com.flowpowered.math.matrix.Matrix3f;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.Creatable;
import com.flowpowered.caustic.api.GLVersioned;
import com.flowpowered.caustic.api.data.Uniform;
import com.flowpowered.caustic.api.data.UniformHolder;

/**
 * Represents an OpenGL program. A program holds the necessary shaders for the rendering pipeline. When using GL20, it is strongly recommended to set the attribute layout in the {@link
 * com.flowpowered.caustic.api.gl.Shader}s with {@link com.flowpowered.caustic.api.gl.Shader#setAttributeLayout(String, int)}}, which must be done before attaching it. The layout allows for association between
 * the attribute index in the vertex data and the name in the shaders. For GL30, it is recommended to do so in the shaders instead, using the "layout" keyword. Failing to do so might result in
 * partial, wrong or missing rendering, and affects models using multiple attributes. The texture layout should also be setup using {@link Shader#setTextureLayout(int, String)} in the same way.
 */
public abstract class Program extends Creatable implements GLVersioned {
    protected int id;

    @Override
    public void destroy() {
        id = 0;
        super.destroy();
    }

    /**
     * Attaches a shader to the program.
     *
     * @param shader The shader to attach
     */
    public abstract void attachShader(Shader shader);

    /**
     * Detaches a shader from the shader.
     *
     * @param shader The shader to detach
     */
    public abstract void detachShader(Shader shader);

    /**
     * Links the shaders together in the program. This makes it usable.
     */
    public abstract void link();

    /**
     * Binds this program to the OpenGL context.
     */
    public abstract void use();

    /**
     * Binds the sampler to the texture unit. The binding is done according to the texture layout, which must be set in the program for the textures that will be used before any binding can be done.
     *
     * @param unit The unit to bind
     */
    public abstract void bindSampler(int unit);

    /**
     * Sets a uniform boolean in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param b The boolean value
     */
    public abstract void setUniform(String name, boolean b);

    /**
     * Sets a uniform integer in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param i The integer value
     */
    public abstract void setUniform(String name, int i);

    /**
     * Sets a uniform float in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param f The float value
     */
    public abstract void setUniform(String name, float f);

    /**
     * Sets a uniform float array in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param fs The float array value
     */
    public abstract void setUniform(String name, float[] fs);

    /**
     * Sets a uniform {@link com.flowpowered.math.vector.Vector2f} in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param v The vector value
     */
    public abstract void setUniform(String name, Vector2f v);

    /**
     * Sets a uniform {@link com.flowpowered.math.vector.Vector2f} array in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param vs The vector array value
     */
    public abstract void setUniform(String name, Vector2f[] vs);

    /**
     * Sets a uniform {@link com.flowpowered.math.vector.Vector3f} in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param v The vector value
     */
    public abstract void setUniform(String name, Vector3f v);

    /**
     * Sets a uniform {@link com.flowpowered.math.vector.Vector3f} array in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param vs The vector array value
     */
    public abstract void setUniform(String name, Vector3f[] vs);

    /**
     * Sets a uniform {@link com.flowpowered.math.vector.Vector4f} in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param v The vector value
     */
    public abstract void setUniform(String name, Vector4f v);

    /**
     * Sets a uniform {@link com.flowpowered.math.matrix.Matrix4f} in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param m The matrix value
     */
    public abstract void setUniform(String name, Matrix2f m);

    /**
     * Sets a uniform {@link com.flowpowered.math.matrix.Matrix4f} in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param m The matrix value
     */
    public abstract void setUniform(String name, Matrix3f m);

    /**
     * Sets a uniform {@link com.flowpowered.math.matrix.Matrix4f} in the shader to the desired value.
     *
     * @param name The name of the uniform to set
     * @param m The matrix value
     */
    public abstract void setUniform(String name, Matrix4f m);

    /**
     * Returns the shaders that have been attached to this program.
     *
     * @return The attached shaders
     */
    public abstract Collection<? extends Shader> getShaders();

    /**
     * Returns an set containing all of the uniform names for this program.
     *
     * @return A set of all the uniform names
     */
    public abstract Set<String> getUniformNames();

    /**
     * Uploads the uniform to this program.
     *
     * @param uniform The uniform to upload
     */
    public void upload(Uniform uniform) {
        uniform.upload(this);
    }

    /**
     * Uploads the uniforms to this program.
     *
     * @param uniforms The uniforms to upload
     */
    public void upload(UniformHolder uniforms) {
        for (Uniform uniform : uniforms) {
            uniform.upload(this);
        }
    }

    /**
     * Gets the ID for this program as assigned by OpenGL.
     *
     * @return The ID
     */
    public int getID() {
        return id;
    }
}
