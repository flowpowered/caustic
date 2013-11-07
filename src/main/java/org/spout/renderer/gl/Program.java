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
package org.spout.renderer.gl;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.spout.math.matrix.Matrix2f;
import org.spout.math.matrix.Matrix3f;
import org.spout.math.matrix.Matrix4f;
import org.spout.math.vector.Vector2f;
import org.spout.math.vector.Vector3f;
import org.spout.math.vector.Vector4f;
import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;
import org.spout.renderer.data.Color;
import org.spout.renderer.data.Uniform;
import org.spout.renderer.data.UniformHolder;
import org.spout.renderer.gl.Shader.ShaderType;

/**
 * Represents an OpenGL program. A program holds the necessary shaders for the rendering pipeline. This requires at least that the {@link org.spout.renderer.gl.Shader.ShaderType#VERTEX} and {@link
 * org.spout.renderer.gl.Shader.ShaderType#FRAGMENT} shaders be set with {@link #addShader(Shader)} before creation. When using GL20, it is strongly recommended to set the attribute layout in the
 * program with {@link #addAttributeLayout(String, int)}, which must be done before creation. The layout allows for association between the attribute index in the vertex data and the name in the
 * shaders. For GL30, it is recommended to do so in the shaders instead, using the "layout" keyword. Failing to do so might result in partial, wrong or missing rendering, and affects models using
 * multiple attributes. The texture layout should also be setup using {@link #addTextureLayout(String, int)} if textures are used in the shaders. This one can be done after creation, but is necessary
 * for assigning texture units to sampler uniforms.
 */
public abstract class Program extends Creatable implements GLVersioned {
	protected int id;
	// Shaders
	protected final Map<ShaderType, Shader> shaders = new EnumMap<>(ShaderType.class);
	// Map of the attribute names to their vao index (optional for GL30 as they can be defined in the shader instead)
	protected TObjectIntMap<String> attributeLayouts;
	// Map of the texture units to their names. Only necessary if textures are used
	protected TIntObjectMap<String> textureLayouts;

	@Override
	public void create() {
		attributeLayouts = null;
		super.create();
	}

	@Override
	public void destroy() {
		shaders.clear();
		textureLayouts = null;
		id = 0;
		super.destroy();
	}

	/**
	 * Binds this program to the OpenGL context.
	 */
	public abstract void bind();

	/**
	 * Unbinds this program from the OpenGL context.
	 */
	public abstract void unbind();

	/**
	 * Binds the texture unit to the shader uniform. The binding is done according to the texture layout, which must be set in the program for the textures that will be used before any binding can be
	 * done.
	 *
	 * @param unit The unit to bind
	 */
	public abstract void bindTextureUniform(int unit);

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
	 * Sets a uniform {@link org.spout.math.vector.Vector2f} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public abstract void setUniform(String name, Vector2f v);

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector2f} array in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param vs The vector array value
	 */
	public abstract void setUniform(String name, Vector2f[] vs);

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector3f} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public abstract void setUniform(String name, Vector3f v);

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector3f} array in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param vs The vector array value
	 */
	public abstract void setUniform(String name, Vector3f[] vs);

	/**
	 * Sets a uniform {@link org.spout.math.vector.Vector4f} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param v The vector value
	 */
	public abstract void setUniform(String name, Vector4f v);

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4f} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public abstract void setUniform(String name, Matrix2f m);

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4f} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public abstract void setUniform(String name, Matrix3f m);

	/**
	 * Sets a uniform {@link org.spout.math.matrix.Matrix4f} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param m The matrix value
	 */
	public abstract void setUniform(String name, Matrix4f m);

	/**
	 * Sets a uniform {@link java.awt.Color} in the shader to the desired value.
	 *
	 * @param name The name of the uniform to set
	 * @param c The color value
	 */
	public void setUniform(String name, Color c) {
		setUniform(name, (Vector4f) c.normalize());
	}

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

	/**
	 * Adds a shader.
	 *
	 * @param shader The shader to add
	 */
	public void addShader(Shader shader) {
		shaders.put(shader.getType(), shader);
		if (shader.getAttributeLayouts() != null) {
			if (attributeLayouts == null) {
				attributeLayouts = new TObjectIntHashMap<>();
			}
			attributeLayouts.putAll(shader.getAttributeLayouts());
		}
		if (shader.getTextureLayouts() != null) {
			if (textureLayouts == null) {
				textureLayouts = new TIntObjectHashMap<>();
			}
			textureLayouts.putAll(shader.getTextureLayouts());
		}
	}

	/**
	 * Returns the shader for the type.
	 *
	 * @param type The shader type to lookup
	 * @return The shader, or null if none could be found
	 */
	public Shader getShader(ShaderType type) {
		return shaders.get(type);
	}

	/**
	 * Returns all the shaders in this program.
	 *
	 * @return An unmodifiable collection of all the shaders in this program
	 */
	public Collection<Shader> getShaders() {
		return Collections.unmodifiableCollection(shaders.values());
	}

	/**
	 * Returns true if a shader is present for the shader type.
	 *
	 * @param type The shader type to lookup
	 * @return Whether or not a shader of the type is present
	 */
	public boolean hasShader(ShaderType type) {
		return shaders.containsKey(type);
	}

	/**
	 * Removes the shader source associated to the type, if present.
	 *
	 * @param type The type to remove
	 */
	public void removeShader(ShaderType type) {
		shaders.remove(type);
	}

	/**
	 * Sets the index of the attribute of the provided name, in the program.
	 *
	 * @param name The name of the attribute
	 * @param index The index for the attribute
	 */
	public void addAttributeLayout(String name, int index) {
		if (attributeLayouts == null) {
			attributeLayouts = new TObjectIntHashMap<>();
		}
		attributeLayouts.put(name, index);
	}

	/**
	 * Removes the index for the attribute of the provided name.
	 */
	public void removeAttributeLayout(String name) {
		if (attributeLayouts != null) {
			attributeLayouts.remove(name);
			if (attributeLayouts.isEmpty()) {
				attributeLayouts = null;
			}
		}
	}

	/**
	 * Sets the unit of the texture to the provided name, in the program.
	 *
	 * @param name The name of the texture
	 * @param unit The unit for the texture
	 */
	public void addTextureLayout(String name, int unit) {
		if (textureLayouts == null) {
			textureLayouts = new TIntObjectHashMap<>();
		}
		textureLayouts.put(unit, name);
	}

	/**
	 * Removes the layout for the texture at the provided unit.
	 */
	public void removeTextureLayout(int unit) {
		if (textureLayouts != null) {
			textureLayouts.remove(unit);
			if (textureLayouts.isEmpty()) {
				textureLayouts = null;
			}
		}
	}
}
