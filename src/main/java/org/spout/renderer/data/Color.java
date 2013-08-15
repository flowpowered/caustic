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

import org.spout.math.vector.Vector4;

/**
 * Represents an immutable color with red, green and blue components, and an optional alpha component. When no alpha component is present, the return value is 0. A color can be normalized (all
 * components have their value between 0 and 1) or not (components are in the range of 0 to 255).
 */
public class Color extends Vector4 {
	/**
	 * The color white.
	 */
	public final static Color WHITE = new Color(255, 255, 255, 255);
	/**
	 * The color blue.
	 */
	public final static Color BLUE = new Color(0, 0, 255, 255);
	/**
	 * The color dark gray.
	 */
	public final static Color DARK_GRAY = new Color(64, 64, 64, 255);
	private static final long serialVersionUID = 1L;
	private final boolean normalized;

	/**
	 * Constructs a new color from the encoded RGBA value. If <code>hasAlpha</code> is false, the alpha value is discarded and replaced with 0.
	 *
	 * @param rgba The rgba color
	 * @param hasAlpha Whether or not to keep the alpha component
	 */
	public Color(int rgba, boolean hasAlpha) {
		this((rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF, hasAlpha ? (rgba >> 24) & 0xFF : 0);
	}

	/**
	 * Constructs a new color from the color components as doubles. Alpha will be 0.
	 *
	 * @param r The red component
	 * @param g The green component
	 * @param b The blue component
	 */
	public Color(double r, double g, double b) {
		this((float) r, (float) g, (float) b);
	}

	/**
	 * Constructs a new color from the color components as floats. Alpha will be 0.
	 *
	 * @param r The red component
	 * @param g The green component
	 * @param b The blue component
	 */
	public Color(float r, float g, float b) {
		this(r, g, b, 0);
	}

	/**
	 * Constructs a new color from the components as doubles.
	 *
	 * @param r The red component
	 * @param g The green component
	 * @param b The blue component
	 * @param a The alpha component
	 */
	public Color(double r, double g, double b, double a) {
		this((float) r, (float) g, (float) b, (float) a);
	}

	/**
	 * Constructs a new color from the components as floats.
	 *
	 * @param r The red component
	 * @param g The green component
	 * @param b The blue component
	 * @param a The alpha component
	 */
	public Color(float r, float g, float b, float a) {
		super(r, g, b, a);
		if (r < 0 || g < 0 || b < 0 || a < 0) {
			throw new IllegalArgumentException("Colors can not have negative values");
		}
		normalized = r <= 1 && g <= 1 && b <= 1 && a <= 1;
	}

	/**
	 * Returns the red component.
	 *
	 * @return The red component
	 */
	public float getRed() {
		return getX();
	}

	/**
	 * Returns the green component.
	 *
	 * @return The green component
	 */
	public float getGreen() {
		return getY();
	}

	/**
	 * Returns the blue component.
	 *
	 * @return The blue component
	 */
	public float getBlue() {
		return getZ();
	}

	/**
	 * Returns the alpha component.
	 *
	 * @return The alpha component
	 */
	public float getAlpha() {
		return getW();
	}

	/**
	 * Returns the normalized version of this color. If all the component are already smaller or equal to one, the color itself is returned. Else, all the values are divided by 255 and the new normalized
	 * color is returned.
	 *
	 * @return The normalized color
	 */
	@Override
	public Color normalize() {
		if (isNormalized()) {
			return this;
		} else {
			return new Color(getX() / 255f, getY() / 255f, getZ() / 255f, getW() / 255f);
		}
	}

	/**
	 * Returns true if all the component are smaller or equal to one.
	 *
	 * @return Whether or not this color is normalized
	 */
	public boolean isNormalized() {
		return normalized;
	}
}
