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

import java.util.HashMap;
import java.util.Map;

import org.spout.renderer.data.Uniform.ColorUniform;
import org.spout.renderer.data.Uniform.FloatUniform;
import org.spout.renderer.data.Uniform.IntUniform;
import org.spout.renderer.data.Uniform.Matrix2Uniform;
import org.spout.renderer.data.Uniform.Matrix3Uniform;
import org.spout.renderer.data.Uniform.Matrix4Uniform;
import org.spout.renderer.data.Uniform.Vector2Uniform;
import org.spout.renderer.data.Uniform.Vector3Uniform;
import org.spout.renderer.data.Uniform.Vector4Uniform;
import org.spout.renderer.gl20.OpenGL20Program;

public class UniformHolder {
	private final Map<String, Uniform> uniforms = new HashMap<>();

	public void add(Uniform uniform) {
		uniforms.put(uniform.name, uniform);
	}

	public boolean has(String name) {
		return uniforms.containsKey(name);
	}

	public Uniform get(String name) {
		return uniforms.get(name);
	}

	public IntUniform getInt(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof IntUniform)) {
			return null;
		}
		return (IntUniform) uniform;
	}

	public FloatUniform getFloat(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof FloatUniform)) {
			return null;
		}
		return (FloatUniform) uniform;
	}

	public Vector2Uniform getVector2(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof Vector2Uniform)) {
			return null;
		}
		return (Vector2Uniform) uniform;
	}

	public Vector3Uniform getVector3(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof Vector3Uniform)) {
			return null;
		}
		return (Vector3Uniform) uniform;
	}

	public Vector4Uniform getVector4(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof Vector4Uniform)) {
			return null;
		}
		return (Vector4Uniform) uniform;
	}

	public Matrix2Uniform getMatrix2(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof Matrix2Uniform)) {
			return null;
		}
		return (Matrix2Uniform) uniform;
	}

	public Matrix3Uniform getMatrix3(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof Matrix3Uniform)) {
			return null;
		}
		return (Matrix3Uniform) uniform;
	}

	public Matrix4Uniform getMatrix4(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof Matrix4Uniform)) {
			return null;
		}
		return (Matrix4Uniform) uniform;
	}

	public ColorUniform getColor(String name) {
		final Uniform uniform = uniforms.get(name);
		if (!(uniform instanceof ColorUniform)) {
			return null;
		}
		return (ColorUniform) uniform;
	}

	public void remove(Uniform uniform) {
		remove(uniform.getName());
	}

	public void remove(String name) {
		uniforms.remove(name);
	}

	public void clear() {
		uniforms.clear();
	}

	public void upload(OpenGL20Program program) {
		if (!program.isCreated()) {
			throw new IllegalArgumentException("Program hasn't been created yet");
		}
		for (Uniform uniform : uniforms.values()) {
			uniform.upload(program);
		}
	}
}
