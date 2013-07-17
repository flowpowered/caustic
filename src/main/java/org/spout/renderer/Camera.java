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

import org.spout.math.imaginary.Quaternion;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector3;

/**
 * Represents a camera with a projection, position and rotation, for rendering purposes.
 */
public class Camera {
	private Matrix4 projectionMatrix = new Matrix4();
	private Vector3 position = new Vector3(0, 0, 0);
	private Quaternion rotation = new Quaternion();
	private Matrix4 rotationMatrixInverse = new Matrix4();
	private Matrix4 matrix = new Matrix4();
	private boolean updateMatrix = true;

	private Camera(Matrix4 projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}

	/**
	 * Returns the projection matrix.
	 *
	 * @return The projection matrix
	 */
	public Matrix4 getProjectionMatrix() {
		return projectionMatrix;
	}

	/**
	 * Returns the camera matrix, which is the transformation matrix for the position and rotation.
	 *
	 * @return The camera matrix
	 */
	public Matrix4 getMatrix() {
		if (updateMatrix) {
			final Matrix4 rotationMatrix = Matrix4.createRotation(rotation.invert());
			rotationMatrixInverse = rotationMatrix.invert();
			matrix = rotationMatrix.mul(Matrix4.createTranslation(position.negate()));
			updateMatrix = false;
		}
		return matrix;
	}

	/**
	 * Gets the camera position.
	 *
	 * @return The camera position
	 */
	public Vector3 getPosition() {
		return position;
	}

	/**
	 * Sets the camera position.
	 *
	 * @param position The camera position
	 */
	public void setPosition(Vector3 position) {
		this.position = position;
		updateMatrix = true;
	}

	/**
	 * Gets the camera rotation.
	 *
	 * @return The camera rotation
	 */
	public Quaternion getRotation() {
		return rotation;
	}

	/**
	 * Sets the camera rotation.
	 *
	 * @param rotation The camera rotation
	 */
	public void setRotation(Quaternion rotation) {
		this.rotation = rotation;
		updateMatrix = true;
	}

	/**
	 * Gets the vector representing the right direction for the camera.
	 *
	 * @return The camera's right direction vector
	 */
	public Vector3 getRight() {
		return toCamera(new Vector3(-1, 0, 0));
	}

	/**
	 * Gets the vector representing the up direction for the camera.
	 *
	 * @return The camera's up direction vector
	 */
	public Vector3 getUp() {
		return toCamera(new Vector3(0, 1, 0));
	}

	/**
	 * Gets the vector representing the forward direction for the camera.
	 *
	 * @return The camera's forward direction vector
	 */
	public Vector3 getForward() {
		return toCamera(new Vector3(0, 0, -1));
	}

	private Vector3 toCamera(Vector3 v) {
		if (rotationMatrixInverse != null) {
			return rotationMatrixInverse.transform(v.toVector4(1)).toVector3();
		}
		return v;
	}

	/**
	 * Creates a new perspective camera.
	 *
	 * @param fieldOfView The field of view, in degrees
	 * @param windowWidth The window width
	 * @param windowHeight The widow height
	 * @param near The near plane, cannot be 0
	 * @param far The far plane
	 * @return The camera
	 */
	public static Camera createPerspective(float fieldOfView, int windowWidth, int windowHeight,
										   float near, float far) {
		if (near == 0) {
			throw new IllegalArgumentException("Near cannot be zero");
		}
		final float aspectRatio = windowWidth / windowHeight;
		return new Camera(Matrix4.createPerspective(fieldOfView, aspectRatio, near, far));
	}
}
