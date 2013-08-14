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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.util.glu.GLU;

import org.spout.renderer.GLVersioned;
import org.spout.renderer.data.Color;
import org.spout.renderer.gl.Texture.Format;

/**
 * Utility methods for rendering.
 */
public class RenderUtil {
	private static final boolean DEBUG = true;

	/**
	 * Throws an exception if OpenGL reports an error.
	 *
	 * @throws OpenGLException If OpenGL reports an error
	 */
	public static void checkForOpenGLError() {
		if (DEBUG) {
			final int errorValue = GL11.glGetError();
			if (errorValue != GL11.GL_NO_ERROR) {
				throw new OpenGLException("OPEN GL ERROR: " + GLU.gluErrorString(errorValue));
			}
		}
	}

	/**
	 * Checks if two OpenGL versioned object have the same version. Throws an exception if that's not the case.
	 *
	 * @param required The required version
	 * @param object The object to check the version of
	 * @throws IllegalStateException If the object versions to not match
	 */
	public static void checkVersion(GLVersioned required, GLVersioned object) {
		if (required.getGLVersion() != object.getGLVersion()) {
			throw new IllegalStateException("Version mismatch: expected " + required.getGLVersion() + ", got " + object.getGLVersion());
		}
	}

	/**
	 * Gets the {@link BufferedImage}'s data as a {@link ByteBuffer}. The image data reading is done according to the {@link org.spout.renderer.gl.Texture.Format}
	 *
	 * @param image The image to extract the data from
	 * @param format The format of the image data
	 * @return buffer containing the decoded image data
	 */
	public static ByteBuffer getImageData(BufferedImage image, Format format) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		// Obtain the image raw int data
		final int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		// Place the data in the buffer
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
		return data;
	}

	/**
	 * Gets the {@link InputStream}'s data as a {@link ByteBuffer}. The image data reading is done according to the {@link org.spout.renderer.gl.Texture.Format}
	 *
	 * @param source The image input stream to extract the data from
	 * @param format The format of the image data
	 * @return buffer containing the decoded image data
	 */
	public static ByteBuffer getImageData(InputStream source, Format format) throws IOException {
		try {
			return getImageData(ImageIO.read(source), format);
		} catch (Exception ex) {
			throw new IllegalStateException("Unreadable texture image data", ex);
		} finally {
			source.close();
		}
	}

	/**
	 * Converts a {@link org.spout.renderer.data.Color} to a {@link java.awt.Color}.
	 *
	 * @param c The Caustic color to convert
	 * @return The AWT color
	 */
	public static java.awt.Color toAWTColor(Color c) {
		if (c.isNormalized()) {
			return new java.awt.Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		} else {
			return new java.awt.Color((int) c.getRed(), (int) c.getGreen(), (int) c.getBlue(), (int) c.getAlpha());
		}
	}
}
