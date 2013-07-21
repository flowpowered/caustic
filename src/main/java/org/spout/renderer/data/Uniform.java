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
package org.spout.renderer.data;

import java.awt.Color;

import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector3;
import org.spout.math.vector.Vector4;
import org.spout.renderer.gl20.OpenGL20Program;

public abstract class Uniform {
	protected final String name;

	protected Uniform(String name) {
		this.name = name;
	}

	public abstract void upload(OpenGL20Program program);

	public String getName() {
		return name;
	}

	public static class BooleanUniform extends Uniform {
		private boolean value;

		public BooleanUniform(String name, boolean value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public boolean get() {
			return value;
		}

		public void set(boolean value) {
			this.value = value;
		}
	}

	public static class IntUniform extends Uniform {
		private int value;

		public IntUniform(String name, int value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public int get() {
			return value;
		}

		public void set(int value) {
			this.value = value;
		}
	}

	public static class FloatUniform extends Uniform {
		private float value;

		public FloatUniform(String name, float value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public float get() {
			return value;
		}

		public void set(float value) {
			this.value = value;
		}
	}

	public static class Vector2Uniform extends Uniform {
		private Vector2 value;

		public Vector2Uniform(String name, Vector2 value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public Vector2 get() {
			return value;
		}

		public void set(Vector2 value) {
			this.value = value;
		}
	}

	public static class Vector3Uniform extends Uniform {
		private Vector3 value;

		public Vector3Uniform(String name, Vector3 value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public Vector3 get() {
			return value;
		}

		public void set(Vector3 value) {
			this.value = value;
		}
	}

	public static class Vector4Uniform extends Uniform {
		private Vector4 value;

		public Vector4Uniform(String name, Vector4 value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public Vector4 get() {
			return value;
		}

		public void set(Vector4 value) {
			this.value = value;
		}
	}

	public static class Matrix2Uniform extends Uniform {
		private Matrix2 value;

		public Matrix2Uniform(String name, Matrix2 value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public Matrix2 get() {
			return value;
		}

		public void set(Matrix2 value) {
			this.value = value;
		}
	}

	public static class Matrix3Uniform extends Uniform {
		private Matrix3 value;

		public Matrix3Uniform(String name, Matrix3 value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public Matrix3 get() {
			return value;
		}

		public void set(Matrix3 value) {
			this.value = value;
		}
	}

	public static class Matrix4Uniform extends Uniform {
		private Matrix4 value;

		public Matrix4Uniform(String name, Matrix4 value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public Matrix4 get() {
			return value;
		}

		public void set(Matrix4 value) {
			this.value = value;
		}
	}

	public static class ColorUniform extends Uniform {
		private Color value;

		public ColorUniform(String name, Color value) {
			super(name);
			this.value = value;
		}

		@Override
		public void upload(OpenGL20Program program) {
			program.setUniform(name, value);
		}

		public Color get() {
			return value;
		}

		public void set(Color value) {
			this.value = value;
		}
	}
}
