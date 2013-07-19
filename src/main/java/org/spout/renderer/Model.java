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

import org.lwjgl.opengl.GL11;

import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector3;
import org.spout.renderer.data.VertexData;

/**
 * Represents a model for OpenGL. Each model has it's own position, rotation and color. The {@link
 * org.spout.renderer.Renderer} should always be created before the models.
 */
public abstract class Model extends Creatable {
	// Position and rotation properties
	protected Vector3 position = new Vector3(0, 0, 0);
	protected Vector3 scale = new Vector3(1, 1, 1);
	protected Quaternion rotation = new Quaternion();
	protected Matrix4 matrix = new Matrix4();
	protected boolean updateMatrix = true;
	// Material
	protected Color modelColor = new Color(0.8f, 0.1f, 0.1f, 1);
	// Vertex data
	protected final VertexData vertices = new VertexData();
	// Drawing mode
	protected DrawMode mode = DrawMode.TRIANGLES;

	protected Model() {
	}

	@Override
	public void destroy() {
		vertices.clear();
		super.destroy();
	}

	/**
	 * Draws the model to the screen.
	 */
	protected abstract void render();

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

	/**
	 * Returns the of vertex data. Use it to add mesh data.
	 *
	 * @return The vertex data
	 */
	public VertexData getVertexData() {
		return vertices;
	}

	/**
	 * Deletes all the vertex data associated to the model.
	 */
	public void clearVertexData() {
		vertices.clear();
	}

	/**
	 * Returns the model's drawing mode.
	 *
	 * @return The drawing mode
	 */
	public DrawMode getDrawMode() {
		return mode;
	}

	/**
	 * Sets the model's drawing mode.
	 *
	 * @param mode The drawing mode to use
	 */
	public void setDrawMode(DrawMode mode) {
		this.mode = mode;
	}

	/**
	 * Represents the different drawing modes for the model
	 */
	public static enum DrawMode {
		LINES(GL11.GL_LINES),
		TRIANGLES(GL11.GL_TRIANGLES);
		private final int glConstant;

		private DrawMode(int constant) {
			this.glConstant = constant;
		}

		/**
		 * Returns the OpenGL constant associated to the drawing mode
		 *
		 * @return The OpenGL constant
		 */
		public int getGLConstant() {
			return glConstant;
		}
	}
}
