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

import java.io.InputStream;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

/**
 * Represents a texture for OpenGL. The textures image, dimension, wrapping and filters must be set
 * before it can be created.
 */
public abstract class Texture extends Creatable {
	protected int id = 0;
	protected int unit = GL13.GL_TEXTURE0;
	protected TextureFormat format = TextureFormat.RGB;
	protected WrapMode wrapT = WrapMode.REPEAT;
	protected WrapMode wrapS = WrapMode.REPEAT;
	protected FilterMode minFilter = FilterMode.LINEAR;
	protected FilterMode magFilter = FilterMode.NEAREST;
	protected InputStream source;

	/**
	 * Binds the texture to the OpenGL context.
	 */
	public abstract void bind();

	/**
	 * Unbinds the texture from the OpenGL context.
	 */
	public abstract void unbind();

	/**
	 * Gets the ID for this texture as assigned by OpenGL.
	 *
	 * @return The ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Returns the texture unit, a number between 0 and 31.
	 *
	 * @return The texture unit
	 */
	public int getUnit() {
		return unit - GL13.GL_TEXTURE0;
	}

	/**
	 * Sets the texture unit.
	 *
	 * @param unit The texture unit to set
	 */
	public void setUnit(int unit) {
		this.unit = GL13.GL_TEXTURE0 + unit;
	}

	/**
	 * Returns the texture's format.
	 *
	 * @return The texture format
	 */
	public TextureFormat getFormat() {
		return format;
	}

	/**
	 * Sets the texture's format.
	 *
	 * @param format The format to set
	 */
	public void setFormat(TextureFormat format) {
		this.format = format;
	}

	/**
	 * Gets the horizontal texture wrap.
	 *
	 * @return Horizontal texture wrap
	 */
	public WrapMode getWrapS() {
		return wrapS;
	}

	/**
	 * Sets the horizontal texture wrap.
	 *
	 * @param wrapS Horizontal texture wrap
	 */
	public void setWrapS(WrapMode wrapS) {
		this.wrapS = wrapS;
	}

	/**
	 * Gets the vertical texture wrap.
	 *
	 * @return Vertical texture wrap
	 */
	public WrapMode getWrapT() {
		return wrapT;
	}

	/**
	 * Sets the vertical texture wrap.
	 *
	 * @param wrapT Vertical texture wrap
	 */
	public void setWrapT(WrapMode wrapT) {
		this.wrapT = wrapT;
	}

	/**
	 * Gets the texture's min filter.
	 *
	 * @return The min filter
	 */
	public FilterMode getMinFilter() {
		return minFilter;
	}

	/**
	 * Sets the texture's min filter.
	 *
	 * @param minFilter The min filter
	 */
	public void setMinFilter(FilterMode minFilter) {
		this.minFilter = minFilter;
	}

	/**
	 * Gets the texture's mag filter.
	 *
	 * @return The mag filter
	 */
	public FilterMode getMagFilter() {
		return magFilter;
	}

	/**
	 * Sets the texture's mag filter. Filters that require mipmaps generation cannot be used here.
	 *
	 * @param magFilter The mag filter
	 */
	public void setMagFilter(FilterMode magFilter) {
		if (magFilter.needsMipMaps()) {
			throw new IllegalArgumentException("Mimpmap filters cannot be used for texture magnification");
		}
		this.magFilter = magFilter;
	}

	/**
	 * Sets the input stream source of the texture.
	 *
	 * @param source The input stream of the texture
	 */
	public void setSource(InputStream source) {
		this.source = source;
	}

	/**
	 * Represents the pixel format for the texture. Only the specified components will be loaded.
	 */
	public static enum TextureFormat {
		RED(GL11.GL_RED, true, false, false, false),
		RG(GL30.GL_RG, true, true, false, false),
		RGB(GL11.GL_RGB, true, true, true, false),
		RGBA(GL11.GL_RGBA, true, true, true, true);
		private final int glConstant;
		private final boolean hasRed;
		private final boolean hasGreen;
		private final boolean hasBlue;
		private final boolean hasAlpha;

		private TextureFormat(int glConstant, boolean hasRed, boolean hasGreen, boolean hasBlue, boolean hasAlpha) {
			this.glConstant = glConstant;
			this.hasRed = hasRed;
			this.hasGreen = hasGreen;
			this.hasBlue = hasBlue;
			this.hasAlpha = hasAlpha;
		}

		/**
		 * Gets the OpenGL constant for this texture wrap
		 *
		 * @return The OpenGL Constant
		 */
		public int getGLConstant() {
			return glConstant;
		}

		/**
		 * Returns true if this format has a red color component.
		 *
		 * @return True if a red color component is present
		 */
		public boolean hasRed() {
			return hasRed;
		}

		/**
		 * Returns true if this format has a green color component.
		 *
		 * @return True if a green color component is present
		 */
		public boolean hasGreen() {
			return hasGreen;
		}

		/**
		 * Returns true if this format has a blue color component.
		 *
		 * @return True if a blue color component is present
		 */
		public boolean hasBlue() {
			return hasBlue;
		}

		/**
		 * Returns true if this format has a alpha color component.
		 *
		 * @return True if a alpha color component is present
		 */
		public boolean hasAlpha() {
			return hasAlpha;
		}
	}

	/**
	 * An enum for the texture wrapping modes.
	 */
	public static enum WrapMode {
		CLAMP_TO_EDGE(GL12.GL_CLAMP_TO_EDGE),
		CLAMP_TO_BORDER(GL13.GL_CLAMP_TO_BORDER),
		MIRRORED_REPEAT(GL14.GL_MIRRORED_REPEAT),
		REPEAT(GL11.GL_REPEAT);
		private final int glConstant;

		private WrapMode(int glConstant) {
			this.glConstant = glConstant;
		}

		/**
		 * Gets the OpenGL constant for this texture wrap
		 *
		 * @return The OpenGL Constant
		 */
		public int getGLConstant() {
			return glConstant;
		}
	}

	/**
	 * An enum for the texture filtering modes.
	 */
	public static enum FilterMode {
		LINEAR(GL11.GL_LINEAR),
		NEAREST(GL11.GL_NEAREST),
		NEAREST_MIPMAP_NEAREST(GL11.GL_NEAREST_MIPMAP_NEAREST),
		LINEAR_MIPMAP_NEAREST(GL11.GL_LINEAR_MIPMAP_NEAREST),
		NEAREST_MIPMAP_LINEAR(GL11.GL_NEAREST_MIPMAP_LINEAR),
		LINEAR_MIPMAP_LINEAR(GL11.GL_LINEAR_MIPMAP_LINEAR);
		private final int glConstant;

		private FilterMode(int glConstant) {
			this.glConstant = glConstant;
		}

		/**
		 * Gets the OpenGL constant for this texture filter
		 *
		 * @return The OpenGL Constant
		 */
		public int getGLConstant() {
			return glConstant;
		}

		/**
		 * Returns true if the filtering mode required generation of mipmaps.
		 *
		 * @return Whether or not mipmaps are required
		 */
		public boolean needsMipMaps() {
			return this == NEAREST_MIPMAP_NEAREST || this == LINEAR_MIPMAP_NEAREST
					|| this == NEAREST_MIPMAP_LINEAR || this == LINEAR_MIPMAP_LINEAR;
		}
	}
}
