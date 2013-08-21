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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;

import org.spout.renderer.data.Color;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.gl.Texture.Format;

/**
 * Utility methods for rendering.
 */
public final class CausticUtil {
	private CausticUtil() {
	}

	/**
	 * Gets the {@link java.io.InputStream}'s data as a {@link ByteBuffer}. The image data reading is done according to the {@link org.spout.renderer.gl.Texture.Format}. The image size is stored in the
	 * passed {@link Rectangle} instance.
	 *
	 * @param source The image input stream to extract the data from
	 * @param format The format of the image data
	 * @param size The rectangle to store the size in
	 * @return buffer containing the decoded image data
	 */
	public static ByteBuffer getImageData(InputStream source, Format format, Rectangle size) {
		try {
			final BufferedImage image = ImageIO.read(source);
			size.setRect(0, 0, image.getWidth(), image.getHeight());
			return getImageData(image, format);
		} catch (Exception ex) {
			throw new IllegalStateException("Unreadable texture image data", ex);
		}
	}

	/**
	 * Gets the {@link BufferedImage}'s data as a {@link ByteBuffer}. The image data reading is done according to the {@link org.spout.renderer.gl.Texture.Format}.
	 *
	 * @param image The image to extract the data from
	 * @param format The format of the image data
	 * @return buffer containing the decoded image data
	 */
	public static ByteBuffer getImageData(BufferedImage image, Format format) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		final int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		return getImageData(pixels, format, width, height);
	}

	/**
	 * Sets the texture's image data. The image data reading is done according to the set {@link org.spout.renderer.gl.Texture.Format}.
	 *
	 * @param pixels The image pixels
	 * @param width The width of the image
	 * @param height the height of the image
	 */
	public static ByteBuffer getImageData(int[] pixels, Format format, int width, int height) {
		final ByteBuffer data = CausticUtil.createByteBuffer(width * height * format.getComponentCount());
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

	/**
	 * Creates a byte buffer of the desired capacity.
	 *
	 * @param capacity The capacity
	 * @return The byte buffer
	 */
	public static ByteBuffer createByteBuffer(int capacity) {
		return ByteBuffer.allocateDirect(capacity * DataType.BYTE.getByteSize()).order(ByteOrder.nativeOrder());
	}

	/**
	 * Creates a short buffer of the desired capacity.
	 *
	 * @param capacity The capacity
	 * @return The short buffer
	 */
	public static ShortBuffer createShortBuffer(int capacity) {
		return createByteBuffer(capacity * DataType.SHORT.getByteSize()).asShortBuffer();
	}

	/**
	 * Creates an int buffer of the desired capacity.
	 *
	 * @param capacity The capacity
	 * @return The int buffer
	 */
	public static IntBuffer createIntBuffer(int capacity) {
		return createByteBuffer(capacity * DataType.INT.getByteSize()).asIntBuffer();
	}

	/**
	 * Creates a double buffer of the desired capacity.
	 *
	 * @param capacity The capacity
	 * @return The double buffer
	 */
	public static FloatBuffer createFloatBuffer(int capacity) {
		return createByteBuffer(capacity * DataType.FLOAT.getByteSize()).asFloatBuffer();
	}

	/**
	 * Creates a float buffer of the desired capacity.
	 *
	 * @param capacity The capacity
	 * @return The float buffer
	 */
	public static DoubleBuffer createDoubleBuffer(int capacity) {
		return createByteBuffer(capacity * DataType.DOUBLE.getByteSize()).asDoubleBuffer();
	}

	/**
	 * Generate the normals for the positions, according to the indices. This assumes that the positions have 3 components, in the x, y, z order. The normals are stored as a 3 component vector, in the x,
	 * y, z order.
	 *
	 * @param positions The position components
	 * @param indices The indices
	 * @return The normals
	 */
	public static TFloatList generateNormals(TFloatList positions, TIntList indices) {
		final TFloatList normals = new TFloatArrayList();
		generateNormals(positions, indices, normals);
		return normals;
	}

	/**
	 * Generate the normals for the positions, according to the indices. This assumes that the positions have 3 components, in the x, y, z order. The normals are stored as a 3 component vector, in the x,
	 * y, z order.
	 *
	 * @param positions The position components
	 * @param indices The indices
	 * @param normals The list in which to store the normals
	 */
	public static void generateNormals(TFloatList positions, TIntList indices, TFloatList normals) {
		// Initialize normals to (0, 0, 0)
		normals.fill(0, positions.size(), 0);
		// Iterate over the entire mesh
		for (int i = 0; i < indices.size(); i += 3) {
			// Triangle position indices
			final int pos0 = indices.get(i) * 3;
			final int pos1 = indices.get(i + 1) * 3;
			final int pos2 = indices.get(i + 2) * 3;
			// First triangle vertex position
			final float x0 = positions.get(pos0);
			final float y0 = positions.get(pos0 + 1);
			final float z0 = positions.get(pos0 + 2);
			// Second triangle vertex position
			final float x1 = positions.get(pos1);
			final float y1 = positions.get(pos1 + 1);
			final float z1 = positions.get(pos1 + 2);
			// Third triangle vertex position
			final float x2 = positions.get(pos2);
			final float y2 = positions.get(pos2 + 1);
			final float z2 = positions.get(pos2 + 2);
			// First edge position difference
			final float x10 = x1 - x0;
			final float y10 = y1 - y0;
			final float z10 = z1 - z0;
			// Second edge position difference
			final float x20 = x2 - x0;
			final float y20 = y2 - y0;
			final float z20 = z2 - z0;
			// Cross both edges to obtain the normal
			final float nx = y10 * z20 - z10 * y20;
			final float ny = z10 * x20 - x10 * z20;
			final float nz = x10 * y20 - y10 * x20;
			// Add the normal to the first vertex
			normals.set(pos0, normals.get(pos0) + nx);
			normals.set(pos0 + 1, normals.get(pos0 + 1) + ny);
			normals.set(pos0 + 2, normals.get(pos0 + 2) + nz);
			// Add the normal to the second vertex
			normals.set(pos1, normals.get(pos1) + nx);
			normals.set(pos1 + 1, normals.get(pos1 + 1) + ny);
			normals.set(pos1 + 2, normals.get(pos1 + 2) + nz);
			// Add the normal to the third vertex
			normals.set(pos2, normals.get(pos2) + nx);
			normals.set(pos2 + 1, normals.get(pos2 + 1) + ny);
			normals.set(pos2 + 2, normals.get(pos2 + 2) + nz);
		}
		// Iterate over all the normals
		for (int i = 0; i < indices.size(); i++) {
			// Index for the normal
			final int nor = indices.get(i) * 3;
			// Get the normal
			float nx = normals.get(nor);
			float ny = normals.get(nor + 1);
			float nz = normals.get(nor + 2);
			// Length of the normal
			final float l = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
			// Normalize the normal
			nx /= l;
			ny /= l;
			nz /= l;
			// Update the normal
			normals.set(nor, nx);
			normals.set(nor + 1, ny);
			normals.set(nor + 2, nz);
		}
	}

	/**
	 * Generate the tangents for the positions, normals and texture coords, according to the indices. This assumes that the positions and normals have 3 components, in the x, y, z order, and that the
	 * texture coords have 2, in the u, v (or s, t) order. The tangents are stored as a 4 component vector, in the x, y, z, w order. The w component represents the handedness for the bi-tangent
	 * computation, which must be computed with B = T_w * (N x T).
	 *
	 * @param positions The position components
	 * @param normals The normal components
	 * @param textures The texture coord components
	 * @param indices The indices
	 * @return The tangents
	 */
	public static TFloatList generateTangents(TFloatList positions, TFloatList normals, TFloatList textures, TIntList indices) {
		final TFloatList tangents = new TFloatArrayList();
		generateTangents(positions, normals, textures, indices, tangents);
		return tangents;
	}

	/**
	 * Generate the tangents for the positions, normals and texture coords, according to the indices. This assumes that the positions and normals have 3 components, in the x, y, z order, and that the
	 * texture coords have 2, in the u, v (or s, t) order. The tangents are stored as a 4 component vector, in the x, y, z, w order. The w component represents the handedness for the bi-tangent
	 * computation, which must be computed with B = T_w * (N x T).
	 *
	 * @param positions The position components
	 * @param normals The normal components
	 * @param textures The texture coord components
	 * @param indices The indices
	 * @param tangents The list in which to store the tangents
	 */
	public static void generateTangents(TFloatList positions, TFloatList normals, TFloatList textures, TIntList indices, TFloatList tangents) {
		// Adapted from: http://www.terathon.com/code/tangent.html
		// Size of the tangent list (without the handedness value)
		final int size = normals.size();
		// Initialize all tangents to (0, 0, 0, 0)
		tangents.fill(0, size / 3 * 4, 0);
		// Storage for the derivatives in respect to u and v
		final float[] du = new float[size];
		final float[] dv = new float[size];
		// Iterate over the entire mesh
		for (int i = 0; i < indices.size(); i += 3) {
			// Triangle position indices
			final int pos0 = indices.get(i) * 3;
			final int pos1 = indices.get(i + 1) * 3;
			final int pos2 = indices.get(i + 2) * 3;
			// First triangle vertex position
			final float x0 = positions.get(pos0);
			final float y0 = positions.get(pos0 + 1);
			final float z0 = positions.get(pos0 + 2);
			// Second triangle vertex position
			final float x1 = positions.get(pos1);
			final float y1 = positions.get(pos1 + 1);
			final float z1 = positions.get(pos1 + 2);
			// Third triangle vertex position
			final float x2 = positions.get(pos2);
			final float y2 = positions.get(pos2 + 1);
			final float z2 = positions.get(pos2 + 2);
			// Triangle texture coord indices
			final int tex0 = indices.get(i) * 2;
			final int tex1 = indices.get(i + 1) * 2;
			final int tex2 = indices.get(i + 2) * 2;
			// First triangle vertex texture coord
			final float u0 = textures.get(tex0);
			final float v0 = textures.get(tex0 + 1);
			// Second triangle vertex texture coord
			final float u1 = textures.get(tex1);
			final float v1 = textures.get(tex1 + 1);
			// Third triangle vertex texture coord
			final float u2 = textures.get(tex2);
			final float v2 = textures.get(tex2 + 1);
			// First edge position difference
			final float x10 = x1 - x0;
			final float y10 = y1 - y0;
			final float z10 = z1 - z0;
			// Second edge position difference
			final float x20 = x2 - x0;
			final float y20 = y2 - y0;
			final float z20 = z2 - z0;
			// First edge texture coord difference
			final float u10 = u1 - u0;
			final float v10 = v1 - v0;
			// Second edge texture coord difference
			final float u20 = u2 - u0;
			final float v20 = v2 - v0;
			//  Coefficient for derivative calculation
			float r = 1 / (u10 * v20 - u20 * v10);
			// Derivative in respect to U
			final float dux = (v20 * x10 - v10 * x20) * r;
			final float duy = (v20 * y10 - v10 * y20) * r;
			final float duz = (v20 * z10 - v10 * z20) * r;
			// Derivative in respect to V
			final float dvx = (u10 * x20 - u20 * x10) * r;
			final float dvy = (u10 * y20 - u20 * y10) * r;
			final float dvz = (u10 * z20 - u20 * z10) * r;
			// Add the derivative in respect to U to the first vertex
			du[pos0] += dux;
			du[pos0 + 1] += duy;
			du[pos0 + 2] += duz;
			// Add the derivative in respect to U to the second vertex
			du[pos1] += dux;
			du[pos1 + 1] += duy;
			du[pos1 + 2] += duz;
			// Add the derivative in respect to U to the third vertex
			du[pos2] += dux;
			du[pos2 + 1] += duy;
			du[pos2 + 2] += duz;
			// Add the derivative in respect to V to the first vertex
			dv[pos0] += dvx;
			dv[pos0 + 1] += dvy;
			dv[pos0 + 2] += dvz;
			// Add the derivative in respect to V to the second vertex
			dv[pos1] += dvx;
			dv[pos1 + 1] += dvy;
			dv[pos1 + 2] += dvz;
			// Add the derivative in respect to V to the first vertex
			dv[pos2] += dvx;
			dv[pos2 + 1] += dvy;
			dv[pos2 + 2] += dvz;
		}
		// Iterate over all the tangents
		for (int i = 0; i < indices.size(); i++) {
			// Index for the normal
			final int nor = indices.get(i) * 3;
			// Get the normal
			final float nx = normals.get(nor);
			final float ny = normals.get(nor + 1);
			final float nz = normals.get(nor + 2);
			// Get the derivative in respect to U
			final float dux = du[nor];
			final float duy = du[nor + 1];
			final float duz = du[nor + 2];
			// Get the derivative in respect to V
			final float dvx = dv[nor];
			final float dvy = dv[nor + 1];
			final float dvz = dv[nor + 2];
			// Dot the normal and derivative in respect to U
			final float d = nx * dux + ny * duy + nz * duz;
			// Calculate the tangent using Gram-Schmidt
			float tx = (dux - nx * d);
			float ty = (duy - ny * d);
			float tz = (duz - nz * d);
			// Length of the tangent
			final float l = (float) Math.sqrt(tx * tx + ty * ty + tz * tz);
			// Normalize the tangent
			tx /= l;
			ty /= l;
			tz /= l;
			// Index for the tangent
			final int tan = indices.get(i) * 4;
			// Set the tangent coordinates
			tangents.set(tan, tx);
			tangents.set(tan + 1, ty);
			tangents.set(tan + 2, tz);
			// Cross the normal and the derivative in respect to U
			final float cx = ny * duz - nz * duy;
			final float cy = nz * dux - nx * duz;
			final float cz = nx * duy - ny * dux;
			// Dot this cross product with the derivative in respect to V
			final float d2 = cx * dvx + cy * dvy + cz * dvz;
			// Determine the handedness
			final float h = d2 < 0 ? -1 : 1;
			// Set the handedness value
			tangents.set(tan + 3, h);
		}
	}
}
