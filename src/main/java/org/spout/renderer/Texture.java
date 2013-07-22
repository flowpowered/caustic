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

	public int[] getImage() {
		int[] copy = new int[image.length];
		System.arraycopy(image, 0, copy, 0, image.length);
		return copy;
	}

	public void setImage(int[] image) {
		this.image = image;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public TextureWrap getWrapT() {
		return wrapT;
	}

	public void setWrapT(TextureWrap wrapT) {
		this.wrapT = wrapT;
	}

	public TextureWrap getWrapS() {
		return wrapS;
	}

	public void setWrapS(TextureWrap wrapS) {
		this.wrapS = wrapS;
	}

	public TextureFilter getMinFilter() {
		return minFilter;
	}

	public void setMinFilter(TextureFilter minFilter) {
		this.minFilter = minFilter;
	}

	public TextureFilter getMagFilter() {
		return magFilter;
	}

	public void setMagFilter(TextureFilter magFilter) {
		this.magFilter = magFilter;
	}

	public InputStream getTextureSource() {
		return textureSource;
	}

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
