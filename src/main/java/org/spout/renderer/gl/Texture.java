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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;
import org.spout.renderer.data.VertexAttribute.DataType;

/**
 * Represents a texture for OpenGL. Image data can be set with one of the <code>setImageData(...)</code> methods before creation, but this is not obligatory. This results in an empty texture, with an
 * undefined content. This is mostly used for frame buffers.
 */
public abstract class Texture extends Creatable implements GLVersioned {
	protected int id = 0;
	// The format
	protected Format format = Format.RGB;
	protected InternalFormat internalFormat = null;
	protected DataType type = DataType.UNSIGNED_BYTE;
	// Wrapping modes for s and t
	protected WrapMode wrapT = WrapMode.REPEAT;
	protected WrapMode wrapS = WrapMode.REPEAT;
	// Minimisation and magnification modes
	protected FilterMode minFilter = FilterMode.NEAREST;
	protected FilterMode magFilter = FilterMode.NEAREST;
	protected float anisotropicFiltering = 0;
	// The texture image data
	protected ByteBuffer imageData;
	// Texture image dimensions
	protected int width;
	protected int height;

	@Override
	public void create() {
		imageData = null;
		super.create();
	}

	@Override
	public void destroy() {
		id = 0;
		super.destroy();
	}

	/**
	 * Binds the texture to the OpenGL context.
	 *
	 * @param unit The unit to bind the texture to, or -1 to just bind the texture
	 */
	public abstract void bind(int unit);

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
	 * Sets the texture's format.
	 *
	 * @param format The format to set
	 */
	public void setFormat(Format format) {
		if (format == null) {
			throw new IllegalArgumentException("Format cannot be null");
		}
		this.format = format;
	}

	/**
	 * Sets the texture's internal format. Set to null to use the un-sized format instead.
	 *
	 * @param internalFormat The internal format to set
	 */
	public void setInternalFormat(InternalFormat internalFormat) {
		this.internalFormat = internalFormat;
	}

	/**
	 * Sets the value for anisotropic filtering. A value smaller or equal to zero is considered as no filtering. Note that this is EXT based and might not be supported on all hardware.
	 *
	 * @param value The anisotropic filtering value
	 */
	public void setAnisotropicFiltering(float value) {
		this.anisotropicFiltering = value;
	}

	/**
	 * Sets the texture's data type.
	 *
	 * @param type The type to set
	 */
	public void setComponentType(DataType type) {
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null");
		}
		this.type = type;
	}

	/**
	 * Sets the horizontal texture wrap.
	 *
	 * @param wrapS Horizontal texture wrap
	 */
	public void setWrapS(WrapMode wrapS) {
		if (wrapS == null) {
			throw new IllegalArgumentException("Wrap cannot be null");
		}
		this.wrapS = wrapS;
	}

	/**
	 * Sets the vertical texture wrap.
	 *
	 * @param wrapT Vertical texture wrap
	 */
	public void setWrapT(WrapMode wrapT) {
		if (wrapT == null) {
			throw new IllegalArgumentException("Wrap cannot be null");
		}
		this.wrapT = wrapT;
	}

	/**
	 * Sets the texture's min filter.
	 *
	 * @param minFilter The min filter
	 */
	public void setMinFilter(FilterMode minFilter) {
		if (minFilter == null) {
			throw new IllegalArgumentException("Filter cannot be null");
		}
		this.minFilter = minFilter;
	}

	/**
	 * Sets the texture's mag filter. Filters that require mipmaps generation cannot be used here.
	 *
	 * @param magFilter The mag filter
	 */
	public void setMagFilter(FilterMode magFilter) {
		if (magFilter == null) {
			throw new IllegalArgumentException("Filter cannot be null");
		}
		if (magFilter.needsMipMaps()) {
			throw new IllegalArgumentException("Mimpmap filters cannot be used for texture magnification");
		}
		this.magFilter = magFilter;
	}

	/**
	 * Sets the texture's image data. The image data reading is done according to the set {@link org.spout.renderer.gl.Texture.Format}.
	 *
	 * @param pixels The image pixels
	 * @param width The width of the image
	 * @param height the height of the image
	 */
	public void setImageData(int[] pixels, int width, int height) {
		// Place the data in the buffer, only adding the required components
		final ByteBuffer data = ByteBuffer.allocateDirect(width * height * format.getComponentCount()).order(ByteOrder.nativeOrder());
		for (int y = height - 1; y >= 0; y--) {
			for (int x = 0; x < width; x++) {
				final int pixel = pixels[x + y * width];
				if (format.hasRed()) {
					data.put((byte) (pixel >> 16 & 0xff));
				}
				if (format.hasGreen()) {
					data.put((byte) (pixel >> 8 & 0xff));
				}
				if (format.hasBlue()) {
					data.put((byte) (pixel & 0xff));
				}
				if (format.hasAlpha()) {
					data.put((byte) (pixel >> 24 & 0xff));
				}
			}
		}
		setImageData(data, width, height);
	}

	/**
	 * Sets the texture's image data. The image data reading is done according the the set {@link org.spout.renderer.gl.Texture.Format}.
	 *
	 * @param imageData The image data
	 * @param width The width of the image
	 * @param height the height of the image
	 */
	public void setImageData(ByteBuffer imageData, int width, int height) {
		if (imageData != null) {
			// TODO: don't flip me
			imageData.flip();
		}
		this.imageData = imageData;
		this.width = width;
		this.height = height;
	}

	/**
	 * An enum of texture component formats.
	 */
	public static enum Format {
		RED(GL.GL_RED, 1, true, false, false, false, false, false),
		RG(GL.GL_RG, 2, true, true, false, false, false, false),
		RGB(GL.GL_RGB, 3, true, true, true, false, false, false),
		RGBA(GL.GL_RGBA, 4, true, true, true, true, false, false),
		DEPTH(GL.GL_DEPTH_COMPONENT, 1, false, false, false, false, true, false),
		DEPTH_STENCIL(GL.GL_DEPTH_STENCIL, 1, false, false, false, false, false, true);
		private final int glConstant;
		private final int components;
		private final boolean hasRed;
		private final boolean hasGreen;
		private final boolean hasBlue;
		private final boolean hasAlpha;
		private final boolean hasDepth;
		private final boolean hasStencil;

		private Format(int glConstant, int components, boolean hasRed, boolean hasGreen, boolean hasBlue, boolean hasAlpha, boolean hasDepth, boolean hasStencil) {
			this.glConstant = glConstant;
			this.components = components;
			this.hasRed = hasRed;
			this.hasGreen = hasGreen;
			this.hasBlue = hasBlue;
			this.hasAlpha = hasAlpha;
			this.hasDepth = hasDepth;
			this.hasStencil = hasStencil;
		}

		/**
		 * Gets the OpenGL constant for this format.
		 *
		 * @return The OpenGL Constant
		 */
		public int getGLConstant() {
			return glConstant;
		}

		/**
		 * Returns the number of components in the format.
		 *
		 * @return The number of components
		 */
		public int getComponentCount() {
			return components;
		}

		/**
		 * Returns true if this format has a red component.
		 *
		 * @return True if a red component is present
		 */
		public boolean hasRed() {
			return hasRed;
		}

		/**
		 * Returns true if this format has a green component.
		 *
		 * @return True if a green component is present
		 */
		public boolean hasGreen() {
			return hasGreen;
		}

		/**
		 * Returns true if this format has a blue component.
		 *
		 * @return True if a blue component is present
		 */
		public boolean hasBlue() {
			return hasBlue;
		}

		/**
		 * Returns true if this format has an alpha component.
		 *
		 * @return True if an alpha component is present
		 */
		public boolean hasAlpha() {
			return hasAlpha;
		}

		/**
		 * Returns true if this format has a depth component.
		 *
		 * @return True if a depth component is present
		 */
		public boolean hasDepth() {
			return hasDepth;
		}

		/**
		 * Returns true if this format has a stencil component.
		 *
		 * @return True if a stencil component is present
		 */
		public boolean hasStencil() {
			return hasStencil;
		}
	}

	/**
	 * An enum of sized texture component formats.
	 */
	public static enum InternalFormat {
		/**
		 * Added in OpenGL 1.1
		 */
		RGB8(0x8051), // GL11.GL_RGB8
		RGBA8(0x8058), // GL11.GL_RGBA8
		RGBA16(0x805B), // GL11.GL_RGBA16

		/**
		 * Added in OpenGL 1.4
		 */
		DEPTH_COMPONENT16(0x81A5), // GL14.GL_DEPTH_COMPONENT16
		DEPTH_COMPONENT24(0x81A6), // GL14.GL_DEPTH_COMPONENT24
		DEPTH_COMPONENT32(0x81A7), // GL14.GL_DEPTH_COMPONENT32

		/**
		 * Added in OpenGL 3.0
		 */
		R8(0x8229), // GL30.GL_R8
		R16(0x822A), // GL30.GL_R16
		RG8(0x822B), // GL30.GL_RG8
		RG16(0x822C), // GL30.GL_RG16
		R16F(0x822D), // GL30.GL_R16F
		R32F(0x822E), // GL30.GL_R32F
		RG16F(0x822F), // GL30.GL_RG16F
		RG32F(0x8230), // GL30.GL_RG32F
		RGBA32F(0x8814), // GL30.GL_RGBA32F
		RGB32F(0x8815), // GL30.GL_RGB32F
		RGBA16F(0x881A), // GL30.GL_RGBA16F
		RGB16F(0x881B); // GL30.GL_RGB16F

		private final int glConstant;

		private InternalFormat(int glConstant) {
			this.glConstant = glConstant;
		}

		/**
		 * Gets the OpenGL constant for this internal format.
		 *
		 * @return The OpenGL Constant
		 */
		public int getGLConstant() {
			return glConstant;
		}

	}

	/**
	 * An enum for the texture wrapping modes.
	 */
	public static enum WrapMode {
		CLAMP_TO_EDGE(0x812F), // GL12.GL_CLAMP_TO_EDGE
		CLAMP_TO_BORDER(0x812D), // GL13.GL_CLAMP_TO_BORDER
		MIRRORED_REPEAT(0x8370), // GL14.GL_MIRRORED_REPEAT
		REPEAT(0x2901); // GL11.GL_REPEAT
		private final int glConstant;

		private WrapMode(int glConstant) {
			this.glConstant = glConstant;
		}

		/**
		 * Gets the OpenGL constant for this texture wrap.
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
		LINEAR(0x2601), // GL11.GL_LINEAR
		NEAREST(0x2600), // GL11.GL_NEAREST
		NEAREST_MIPMAP_NEAREST(0x2700), // GL11.GL_NEAREST_MIPMAP_NEAREST
		LINEAR_MIPMAP_NEAREST(0x2701), //GL11.GL_LINEAR_MIPMAP_NEAREST
		NEAREST_MIPMAP_LINEAR(0x2702), // GL11.GL_NEAREST_MIPMAP_LINEAR
		LINEAR_MIPMAP_LINEAR(0x2703); // GL11.GL_LINEAR_MIPMAP_LINEAR
		private final int glConstant;

		private FilterMode(int glConstant) {
			this.glConstant = glConstant;
		}

		/**
		 * Gets the OpenGL constant for this texture filter.
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
