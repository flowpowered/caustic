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

/**
 * Represents a texture for OpenGL. The textures image, dimension, wrapping and filters
 * must be set respectively before it can be created.
 */
public abstract class Texture extends Creatable {

	protected int id = 0;
	protected int[] image;
	protected int width, height;
	protected TextureWrap wrapT, wrapS;
	protected TextureFilter minFilter, magFilter;
	protected InputStream textureSource;

	@Override
	public void create() {
		super.create();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	/**
	 * Returns the image stored as an int[], this array is immutable
	 *
	 * @return Immutable image
	 */
	public int[] getImage() {
		int[] copy = new int[image.length];
		System.arraycopy(image, 0, copy, 0, image.length);
		return copy;
	}

	/**
	 * Sets the image. The new image is a copy of the one
	 * given.
	 *
	 * @param image The image data
	 */
	public void setImage(int[] image) {
		int[] newImage = new int[image.length];
		System.arraycopy(image, 0, newImage, 0, image.length);
		this.image = newImage;
	}
	
	/**
	 * Get the image width
	 *
	 * @return image width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the image height
	 *
	 * @return image height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the horizontal texture wrap
	 *
	 * @return Horizontal texture wrap
	 */
	public TextureWrap getWrapS() {
		return wrapS;
	}

	/**
	 * Sets the horizontal texture wrap
	 *
	 * @param wrapS Horizontal texture wrap
	 */
	public void setWrapS(TextureWrap wrapS) {
		this.wrapS = wrapS;
	}

	/**
	 * Gets the vertical texture wrap
	 *
	 * @return Vertical texture wrap
	 */
	public TextureWrap getWrapT() {
		return wrapT;
	}

	/**
	 * Sets the vertical texture wrap
	 *
	 * @param wrapS Vertical texture wrap
	 */
	public void setWrapT(TextureWrap wrapT) {
		this.wrapT = wrapT;
	}

	/**
	 * Gets the texture's min filter
	 *
	 * @return The min filter
	 */
	public TextureFilter getMinFilter() {
		return minFilter;
	}

	/**
	 * Sets the texture's min filter
	 *
	 * @param minFilter The min filter
	 */
	public void setMinFilter(TextureFilter minFilter) {
		this.minFilter = minFilter;
	}

	/**
	 * Gets the texture's mag filter
	 *
	 * @return The mag filter
	 */
	public TextureFilter getMagFilter() {
		return magFilter;
	}

	/**
	 * Sets the texture's mag filter
	 *
	 * @param mmagFilter The mag filter
	 */
	public void setMagFilter(TextureFilter magFilter) {
		this.magFilter = magFilter;
	}

	/**
	 * Gets the textures input stream
	 *
	 * @return The input stream of the texture
	 */
	public InputStream getTextureSource() {
		return textureSource;
	}

	/**
	 * Sets the input stream source of the texture
	 *
	 * @param textureSource The input stream of the texture
	 */
	public void setTextureSource(InputStream textureSource) {
		this.textureSource = textureSource;
	}

	public enum TextureWrap {

		REPEAT(GL11.GL_REPEAT),
		CLAMP(GL11.GL_CLAMP),
		CLAMP_TO_EDGE(GL12.GL_CLAMP_TO_EDGE);
		private final int wrap;

		TextureWrap(int wrap) {
			this.wrap = wrap;
		}

		public int getWrap() {
			return wrap;
		}
	}

	public enum TextureFilter {

		LINEAR(GL11.GL_LINEAR),
		NEAREST(GL11.GL_NEAREST);
		private final int filter;

		TextureFilter(int filter) {
			this.filter = filter;
		}

		public int getFilter() {
			return filter;
		}
	}
}
