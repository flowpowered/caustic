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
package org.spout.renderer.api.gl;

import java.util.Set;

import com.flowpowered.math.matrix.Matrix2f;
import com.flowpowered.math.matrix.Matrix3f;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;

import org.spout.renderer.api.Creatable;
import org.spout.renderer.api.GLVersioned;
import org.spout.renderer.api.data.Uniform;
import org.spout.renderer.api.data.UniformHolder;

/**
 * Represents an OpenGL program. A program holds the necessary shaders for the rendering pipeline. When using GL20, it is strongly recommended to set the attribute layout in the {@link
 * org.spout.renderer.api.gl.Shader}s with {@link org.spout.renderer.api.gl.Shader#setAttributeLayout(String, int)}}, which must be done before attaching it. The layout allows for association between
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
     * Uploads the uniform to this program.
     *
     * @param uniform The uniform to upload
     */
    public abstract void upload(Uniform uniform);

    /**
     * Uploads the uniforms to this program.
     *
     * @param uniforms The uniforms to upload
     */
    public abstract void upload(UniformHolder uniforms);

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
    public abstract Set<Shader> getShaders();

    /**
     * Returns an set containing all of the uniform names for this program.
     *
     * @return A set of all the uniform names
     */
    public abstract Set<String> getUniformNames();

    /**
     * Gets the ID for this program as assigned by OpenGL.
     *
     * @return The ID
     */
    public int getID() {
        return id;
    }
}
