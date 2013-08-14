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

import org.spout.math.vector.Vector4;

public class Color extends Vector4 {

	private final boolean normalized;

	public Color(float r, float g, float b, float a) {
		super(r, g, b, a);
		if (r < 0.0f || g < 0.0f || b < 0.0f || a < 0.0f) {
			throw new IllegalArgumentException("Colors can not have negative values");
		}
		normalized = r <= 1.0f && g <= 1.0f && b <= 1.0f && a <= 1.0f;
	}

	public Color(float r, float g, float b) {
		this(r, g, b, 0f);
	}

	public Color(double r, double g, double b, double a) {
		super(r, g, b, a);
		if (r < 0.0f || g < 0.0f || b < 0.0f || a < 0.0f) {
			throw new IllegalArgumentException("Colors can not have negative values");
		}
		normalized = r <= 1.0 && g <= 1.0 && b <= 1.0 && a <= 1.0;
	}

	public Color(int rgba, boolean hasAlpha) {
		this((rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF, hasAlpha ? (rgba >> 24) & 0xFF : 0);
	}

	public float getRed() {
		return getX();
	}

	public float getGreen() {
		return getY();
	}

	public float getBlue() {
		return getZ();
	}

	public float getAlpha() {
		return getW();
	}

	@Override
	public Color normalize() {
		if (isNormalized()) {
			return this;
		} else {
			return new Color(getX() / 255f, getY() / 255f, getZ() / 255f, getW() / 255f);
		}
	}

	public boolean isNormalized() {
		return normalized;
	}

	/**
	 * The color white.
	 */
	public final static Color WHITE = new Color(255f, 255f, 255f);

	/**
	 * The color blue.
	 */
	public final static Color BLUE = new Color(0, 0, 255);

	/**
	 * The color dark gray.
	 */
	public final static Color DARK_GRAY  = new Color(64, 64, 64);
}
