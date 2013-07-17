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
package org.spout.renderer;

import java.awt.Color;

import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector3;

/**
 * An abstract model for OpenGL 3.2. There are currently two possible implementations: solid and
 * wireframe. A solid model has a surface made up of smaller triangles. A wireframe is made out of
 * lines, and is only the outline of the shape. Each model has it's own position, rotation and
 * color. The {@link org.spout.renderer.gl30.OpenGL30Renderer} should always be created before the
 * models.
 */
public abstract class Model {
	// State
	protected boolean created = false;
	// Properties
	protected Vector3 position = new Vector3(0, 0, 0);
	protected Vector3 scale = new Vector3(1, 1, 1);
	protected Quaternion rotation = new Quaternion();
	protected Matrix4 matrix = new Matrix4();
	protected boolean updateMatrix = true;
	protected Color modelColor = new Color(0.8f, 0.1f, 0.1f, 1);

	/**
	 * Creates the model. It can now be rendered.
	 */
	public abstract void create();

	/**
	 * Releases the model resources. It can not longer be rendered.
	 */
	public abstract void destroy();

	protected abstract void render();

	/**
	 * Returns true if the display was created and is ready for rendering, false if otherwise.
	 *
	 * @return True if the model can be rendered, false if not
	 */
	public boolean isCreated() {
		return created;
	}

	/**
	 * Returns the transformation matrix that represent the model's current scale, rotation and
	 * position.
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
	 * Gets the model color.
	 *
	 * @return The model color
	 */
	public Color getColor() {
		return modelColor;
	}

	/**
	 * Sets the model color.
	 *
	 * @param color The model color
	 */
	public void setColor(Color color) {
		modelColor = color;
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
}
