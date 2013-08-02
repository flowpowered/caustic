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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import org.spout.renderer.Creatable;

/**
 * Represents a texture for OpenGL. Image data can be set with one of the
 * <code>setImageData(...)</code> methods before creation, but is not obligatory. This results in an
 * empty texture, with an undefined content. This is mostly used for frame buffers.
 */
public abstract class Texture extends Creatable implements GLVersioned {
	protected int id = 0;
	// The unit
	protected int unit = GL13.GL_TEXTURE0;
	// The format
	protected ImageFormat format = ImageFormat.RGB;
	// Wrapping modes for s and t
	protected WrapMode wrapT = WrapMode.REPEAT;
	protected WrapMode wrapS = WrapMode.REPEAT;
	// Minimisation and magnification modes
	protected FilterMode minFilter = FilterMode.NEAREST;
	protected FilterMode magFilter = FilterMode.NEAREST;
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
	 * Sets the texture's format.
	 *
	 * @param format The format to set
	 */
	public void setFormat(ImageFormat format) {
		if (format == null) {
			throw new IllegalArgumentException("Format cannot be null");
		}
		this.format = format;
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
	 * Sets the texture's image data from a source input stream. The image data reading is done
	 * according to the set {@link Texture.ImageFormat}.
	 *
	 * @param source The input stream of the image
	 */
	public void setImageData(InputStream source) {
		try {
			setImageData(ImageIO.read(source));
			source.close();
		} catch (Exception ex) {
			throw new IllegalStateException("Unreadable texture image data", ex);
		}
	}

	/**
	 * Sets the texture's image data. The image data reading is done according to the set {@link
	 * Texture.ImageFormat}.
	 *
	 * @param image The image
	 */
	public void setImageData(BufferedImage image) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		// Obtain the image raw int data
		final int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		// Place the data in the buffer
		setImageData(pixels, width, height);
	}

	/**
	 * Sets the texture's image data. The image data reading is done according to the set {@link
	 * Texture.ImageFormat}.
	 *
	 * @param pixels The image pixels
	 * @param width The width of the image
	 * @param height the height of the image
	 */
	public void setImageData(int[] pixels, int width, int height) {
		// Place the data in the buffer, only adding the required components
		final ByteBuffer data = BufferUtils.createByteBuffer(width * height * format.getComponentCount());
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
	 * Sets the texture's image data. The image data reading is done according the the set {@link
	 * Texture.ImageFormat}.
	 *
	 * @param imageData The image data
	 * @param width The width of the image
	 * @param height the height of the image
	 */
	public void setImageData(ByteBuffer imageData, int width, int height) {
		if (imageData != null) {
			imageData.flip();
		}
		this.imageData = imageData;
		this.width = width;
		this.height = height;
	}

	/**
	 * Represents the pixel format for the texture. Only the specified components will be loaded.
	 */
	public static enum ImageFormat {
		RED(GL11.GL_RED, 1, true, false, false, false, false, false),
		RG(GL30.GL_RG, 2, true, true, false, false, false, false),
		RGB(GL11.GL_RGB, 3, true, true, true, false, false, false),
		RGBA(GL11.GL_RGBA, 4, true, true, true, true, false, false),
		DEPTH(GL11.GL_DEPTH_COMPONENT, 1, false, false, false, false, true, false),
		DEPTH_STENCIL(GL30.GL_DEPTH_STENCIL, 1, false, false, false, false, false, true);
		private final int glConstant;
		private final int components;
		private final boolean hasRed;
		private final boolean hasGreen;
		private final boolean hasBlue;
		private final boolean hasAlpha;
		private final boolean hasDepth;
		private final boolean hasStencil;

		private ImageFormat(int glConstant, int components, boolean hasRed, boolean hasGreen, boolean hasBlue, boolean hasAlpha, boolean hasDepth, boolean hasStencil) {
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
		 * Gets the OpenGL constant for this image format.
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
