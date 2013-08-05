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

import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector3;
import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;
import org.spout.renderer.data.Uniform.Matrix4Uniform;
import org.spout.renderer.data.UniformHolder;

/**
 * Represents an OpenGL model. Each model has it's own position and rotation and set of uniforms. The material needs to be set with {@link #setMaterial(Material)}, before the model can be created. To
 * give the material a mesh, use {@link #getVertexArray()} then {@link VertexArray#setData(org.spout.renderer.data.VertexData)}.
 */
public abstract class Model extends Creatable implements GLVersioned {
	// Position and rotation properties
	protected Vector3 position = new Vector3(0, 0, 0);
	protected Vector3 scale = new Vector3(1, 1, 1);
	protected Quaternion rotation = new Quaternion();
	private Matrix4 matrix = new Matrix4();
	private boolean updateMatrix = true;
	// Model uniforms
	protected final UniformHolder uniforms = new UniformHolder();

	@Override
	public void create() {
		uniforms.add(new Matrix4Uniform("modelMatrix", getMatrix()));
		super.create();
	}

	@Override
	public void destroy() {
		uniforms.clear();
		super.destroy();
	}

	/**
	 * Uploads the model's uniforms to its material's program.
	 */
	public void uploadUniforms() {
		checkCreated();
		uniforms.getMatrix4("modelMatrix").set(getMatrix());
	}

	/**
	 * Draws the model to the screen.
	 */
	public abstract void render();

	/**
	 * Returns the model's vertex array.
	 *
	 * @return The vertex array
	 */
	public abstract VertexArray getVertexArray();

	public abstract void setVertexArray(VertexArray vertexArray);

	/**
	 * Returns the model's material.
	 *
	 * @return The material
	 */
	public abstract Material getMaterial();

	/**
	 * Sets the model's material.
	 *
	 * @param material The material
	 */
	public abstract void setMaterial(Material material);

	/**
	 * Returns the transformation matrix that represent the model's current scale, rotation and position.
	 *
	 * @return The transformation matrix
	 */
	public Matrix4 getMatrix() {
		if (updateMatrix) {
			matrix = Matrix4.createScaling(scale.toVector4(1)).rotate(rotation).translate(position);
			updateMatrix = false;
		}
		return matrix;
	}

	/**
	 * Gets the model position.
	 *
	 * @return The model position
	 */
	public Vector3 getPosition() {
		return position;
	}

	/**
	 * Sets the model position.
	 *
	 * @param position The model position
	 */
	public void setPosition(Vector3 position) {
		this.position = position;
		updateMatrix = true;
	}

	/**
	 * Gets the model rotation.
	 *
	 * @return The model rotation
	 */
	public Quaternion getRotation() {
		return rotation;
	}

	/**
	 * Sets the model rotation.
	 *
	 * @param rotation The model rotation
	 */
	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
		updateMatrix = true;
	}

	/**
	 * Gets the model scale.
	 *
	 * @return The model scale
	 */
	public Vector3 getScale() {
		return scale;
	}

	/**
	 * Sets the model scale.
	 *
	 * @param scale The model scale
	 */
	public void setScale(Vector3 scale) {
		this.scale = scale;
		updateMatrix = true;
	}

	/**
	 * Returns the model's uniforms
	 *
	 * @return The uniforms
	 */
	public UniformHolder getUniforms() {
		return uniforms;
	}
}
