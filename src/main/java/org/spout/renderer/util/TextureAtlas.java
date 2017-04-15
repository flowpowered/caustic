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
package org.spout.renderer.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.spout.renderer.gl.Texture;

/**
 * @author thehutch
 */
public abstract class TextureAtlas extends Texture {

	protected int[] data = null;

	@Override
	public void destroy() {
		data = null;
		super.destroy();
	}

	public void addTexture(InputStream input) {
		try {
			addTexture(ImageIO.read(input));
			input.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void addTexture(BufferedImage image) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		final int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		addTexture(pixels, 0, pixels.length);
	}

	public void addTexture(int[] data, int offset, int len) {
		if (this.data == null) {
			this.data = data;
		} else {
			final int[] newData = new int[this.data.length + data.length];
			System.arraycopy(this.data, 0, newData, 0, this.data.length);
			System.arraycopy(data, 0, newData, this.data.length, data.length);
			this.data = newData;
		}
	}
}
