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

import org.spout.math.GenericMath;
import org.spout.math.vector.Vector4f;

/**
 * Represents an immutable color with red, green and blue components, and an optional alpha component. When no alpha component is present, the return value is 255 (opaque). A color can be normalized
 * (all components have their value between 0 and 1) or not (components are in the range of 0 to 255).
 */
// TODO: extending vector4 might not be that of a good idea because it's immutable and only vector4 objects are returned by the methods not overridden in this class, which can't be used directly
public class Color extends Vector4f {
	/**
	 * The color white.
	 */
	public static final Color WHITE = new Color(255, 255, 255);
	/**
	 * The color blue.
	 */
	public static final Color BLUE = new Color(0, 0, 255);
	/**
	 * The color dark gray.
	 */
	public static final Color DARK_GRAY = new Color(64, 64, 64);
	private static final long serialVersionUID = 1L;
	private final boolean normalized;

	/**
	 * Constructs a new color from the encoded RGBA value. If <code>hasAlpha</code> is false, the alpha value is discarded and replaced with 255 (opaque).
	 *
	 * @param rgba The rgba color
	 * @param hasAlpha Whether or not to keep the alpha component
	 */
	public Color(int rgba, boolean hasAlpha) {
		this((rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF, hasAlpha ? (rgba >> 24) & 0xFF : 255);
	}

	/**
	 * Constructs a new color from the color components as doubles. Alpha will be 255 (opaque). Colors are assumed to be non-normalized.
	 *
	 * @param r The red component
	 * @param g The green component
	 * @param b The blue component
	 */
	public Color(double r, double g, double b) {
		this((float) r, (float) g, (float) b);
	}

	/**
	 * Constructs a new color from the color components as floats. Alpha will be 255 {opaque) Colors are assumed to be non-normalized.
	 *
	 * @param r The red component
	 * @param g The green component
	 * @param b The blue component
	 */
	public Color(float r, float g, float b) {
		this(r, g, b, 255);
	}

	/**
	 * Constructs a new color from the components as doubles. Only accepts values in the range of 0 - 1 for normalized Colors, or 0 - 255 for non-normalized Colors. Values outside of these ranges will
	 * cause an {@link IllegalArgumentException}.
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
	 * Constructs a new color from the components as floats. Only accepts values in the range of 0 - 1 for normalized Colors, or 0 - 255 for non-normalized Colors. Values outside of these ranges will
	 * cause an {@link IllegalArgumentException}.
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
		if (r > 255 || g > 255 || b > 255 || a > 255) {
			throw new IllegalArgumentException("Colors can not have values greater than 255");
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

	/**
	 * Converts the components of a color, as specified by the HSB model, to an equivalent set of values for the default RGB model. The <code>saturation</code> and <code>brightness</code> components
	 * should be floating-point values between zero and one (numbers in the range 0.0-1.0). The <code>hue</code> component can be any floating-point number.
	 *
	 * @param hue The hue component of the color
	 * @param saturation The saturation of the color
	 * @param brightness The brightness of the color
	 * @return The RGB value of the color with the indicated hue, saturation, and brightness.
	 */
	public static Color fromHSB(float hue, float saturation, float brightness) {
		float r = 0;
		float g = 0;
		float b = 0;
		if (saturation == 0) {
			r = g = b = brightness;
		} else {
			final float h = (hue - GenericMath.floor(hue)) * 6.0f;
			final float f = h - GenericMath.floor(h);
			final float p = brightness * (1.0f - saturation);
			final float q = brightness * (1.0f - saturation * f);
			final float t = brightness * (1.0f - saturation * (1.0f - f));
			switch ((int) h) {
				case 0:
					r = brightness;
					g = t;
					b = p;
					break;
				case 1:
					r = q;
					g = brightness;
					b = p;
					break;
				case 2:
					r = p;
					g = brightness;
					b = t;
					break;
				case 3:
					r = p;
					g = q;
					b = brightness;
					break;
				case 4:
					r = t;
					g = p;
					b = brightness;
					break;
				case 5:
					r = brightness;
					g = p;
					b = q;
					break;
			}
		}
		return new Color(r, g, b, 1.0f);
	}
}
