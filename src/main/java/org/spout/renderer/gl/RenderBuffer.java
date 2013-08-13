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

import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;
import org.spout.renderer.gl.Texture.Format;

/**
 * Represents an OpenGL render buffer. A render buffer can be used as a faster alternative to a texture in a frame buffer when its rendering output doesn't need to be read. The storage format, width
 * and height dimensions need to be set with {@link #setFormat(org.spout.renderer.gl.Texture.Format)}, {@link #setWidth(int)} and {@link #setHeight(int)} respectively before the render buffer can
 * be created.
 */
public abstract class RenderBuffer extends Creatable implements GLVersioned {
	protected int id;
	// The render buffer storage format
	protected Format format;
	// The storage dimensions
	protected int width = -1;
	protected int height = -1;

	@Override
	public void destroy() {
		id = 0;
		super.destroy();
	}

	/**
	 * Binds the render buffer to the OpenGL context.
	 */
	public abstract void bind();

	/**
	 * Unbinds the render buffer from the OpenGL context.
	 */
	public abstract void unbind();

	/**
	 * Sets the render buffer storage format.
	 *
	 * @param format The storage format, cannot be null
	 */
	public void setFormat(Format format) {
		if (format == null) {
			throw new IllegalArgumentException("Format cannot be null");
		}
		this.format = format;
	}

	/**
	 * Sets the render buffer storage size.
	 *
	 * @param width The width
	 * @param height The height
	 */
	public void setSize(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	/**
	 * Sets the render buffer storage The.
	 *
	 * @param width The width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Sets the render buffer storage height.
	 *
	 * @param height The height
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Gets the ID for this render buffer as assigned by OpenGL.
	 *
	 * @return The ID
	 */
	public int getID() {
		return id;
	}
}
